package com.app.manage_restaurant.security;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.server.ServerWebExchange;

import com.app.manage_restaurant.repositories.PrsnlRepository;

import reactor.core.publisher.Mono;

public class JwtAuthenticationFilter extends AuthenticationWebFilter {

    private static final Logger logger = LoggerFactory.getLogger("SECURITY.JWT");
    private final JwtService jwtService;
    private final PrsnlRepository repository;

    public JwtAuthenticationFilter(ReactiveAuthenticationManager authManager,
                                   JwtService jwtService,
                                   PrsnlRepository repository) {
        super(authManager);
        this.jwtService = jwtService;
        this.repository = repository;
        this.setServerAuthenticationConverter(this::convert);
    }

    private Mono<Authentication> convert(ServerWebExchange exchange) {
        // OPTIONS → laisser passer
        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            logger.debug("🔄 [JWT] OPTIONS request - skipping authentication");
            return Mono.empty();
        }

        String path = exchange.getRequest().getPath().value();
        // Endpoints publics → laisser passer
        if (isPublicEndpoint(path)) {
            logger.debug("🔓 [JWT] Public endpoint: {}", path);
            return Mono.empty();
        }

        // Récupérer le token Authorization
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("❌ [JWT] No Bearer token for: {}", path);
            return unauthorized(exchange, "Token d'authentification manquant");
        }

        String token = authHeader.substring(7);

        try {
            // Vérifier l'expiration
            if (jwtService.isTokenExpired(token)) {
                logger.warn("❌ [JWT] Token expired");
                return tokenExpired(exchange);
            }

            JwtService.JwtUserInfo userInfo = jwtService.extractUserInfo(token);
            if (userInfo.username() == null) {
                logger.warn("❌ [JWT] No username in token");
                return unauthorized(exchange, "Utilisateur non spécifié dans le token JWT");
            }

            logger.debug("🔍 [JWT] Authenticating user: {}", userInfo.username());
           
            // ✅ Utiliser le contexte déjà créé par SecurityContextFilter si disponible
            return exchange.getAttributeOrDefault("CURRENT_USER", Mono.empty())
                .cast(SecurityUser.class)
                .switchIfEmpty(Mono.defer(() -> {
                    // Fallback: chercher l'utilisateur si pas dans le contexte
                    return repository.findByPhoneAndRestoCodeAndType(userInfo.username(), userInfo.restoCode(), userInfo.type())
                        .map(user -> {
                            UUID restoCode = parseUUID(userInfo.restoCode());
                            UUID ownerCode = parseUUID(userInfo.ownerCode());
                            return new SecurityUser(user, restoCode, ownerCode, user.getRole(), user.getId(),userInfo.type());
                        });
                }))
                .flatMap(securityUser -> {
                    if (securityUser.getRestoCode() == null && securityUser.getOwnerCode() == null) {
                        logger.warn("❌ [JWT] No restoCode or ownerCode for user: {}", userInfo.username());
                        return Mono.error(new SecurityException(
                                "Accès refusé : au moins un code (restoCode ou ownerCode) requis"));
                    }

                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            securityUser.getUser(),
                            token,
                            securityUser.getUser().getAuthorities()
                    );

                    logger.info("✅ [JWT] Authentication successful for: {}", securityUser.getUser().getEmail());
                    return Mono.just(auth);
                })
                .switchIfEmpty(Mono.error(new SecurityException("Utilisateur introuvable")));

        } catch (Exception e) {
            logger.error("💥 [JWT] Authentication error: {}", e.getMessage());
            return unauthorized(exchange, "Token JWT invalide : " + e.getMessage());
        }
    }

    private UUID parseUUID(String code) {
        if (code == null || code.trim().isEmpty() || "null".equals(code)) {
            return null;
        }
        try {
            return UUID.fromString(code);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private boolean isPublicEndpoint(String path) {
        return path.contains("/api/auth/login") || 
               path.contains("/files") || 
               path.startsWith("/api/restaurants/home") ||
               path.startsWith("/api/restaurants/specials") ||
               path.startsWith("/api/commands/home") ||
               path.startsWith("/api/reservations/home") ||
               path.startsWith("/api/prsnls/partenaires") ||
               path.startsWith("/swagger-ui") || 
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/webjars") || 
               path.equals("/swagger-ui.html") ||
               path.contains("/favicon.ico");
    }

    private Mono<Authentication> tokenExpired(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        String body = "{\"error\":\"Token expiré\",\"message\":\"Veuillez vous reconnecter\",\"code\":401}";
        byte[] bytes = body.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)))
                .then(Mono.error(new SecurityException("Token expiré")));
    }

    private Mono<Authentication> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
        String body = "{\"error\":\"Erreur d'authentification\",\"message\":\"" + message + "\",\"code\":401}";
        byte[] bytes = body.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)))
                .then(Mono.error(new SecurityException(message)));
    }
}