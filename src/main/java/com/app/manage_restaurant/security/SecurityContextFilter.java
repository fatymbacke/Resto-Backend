package com.app.manage_restaurant.security;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.app.manage_restaurant.repositories.PrsnlRepository;

import reactor.core.publisher.Mono;

@Component
public class SecurityContextFilter implements WebFilter {
    
    private static final Logger logger = LoggerFactory.getLogger("SECURITY.CONTEXT");
    private final JwtService jwtService;
    private final PrsnlRepository repository;

    public SecurityContextFilter(JwtService jwtService, PrsnlRepository repository) {
        this.jwtService = jwtService;
        this.repository = repository;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        
        // Endpoints publics → pas de traitement d'authentification
        if (isPublicEndpoint(path)) {
            logger.debug("🔓 [CONTEXT] Public endpoint: {}", path);
            return chain.filter(exchange);
        }

        // Récupérer le token
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("🔐 [CONTEXT] No Bearer token found");
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);

        return validateTokenAndCreateContext(token)
            .flatMap(securityUser -> {
                logger.info("✅ [CONTEXT] Setting context for user: {}", securityUser.getUser().getEmail());
                // Propager le contexte utilisateur
                return chain.filter(exchange)
                    .contextWrite(ctx -> ctx.put("CURRENT_USER", securityUser));
            })
            .switchIfEmpty(Mono.defer(() -> {
                logger.debug("🔐 [CONTEXT] No user context created");
                return chain.filter(exchange);
            }));
    }

    private Mono<SecurityUser> validateTokenAndCreateContext(String token) {
        try {
            if (jwtService.isTokenExpired(token)) {
                logger.warn("⚠️ [CONTEXT] Token expired");
                return Mono.empty();
            }

            JwtService.JwtUserInfo userInfo = jwtService.extractUserInfo(token);
            if (userInfo.username() == null) {
                logger.warn("⚠️ [CONTEXT] No username in token");
                return Mono.empty();
            }

            logger.debug("🔍 [CONTEXT] Processing user: {}", userInfo.username());
            
            // ✅ UNIQUEMENT extraire l'utilisateur pour le contexte
            // La validation complète sera faite dans JwtAuthenticationFilter
            return  repository.findByPhoneAndRestoCodeAndType(userInfo.username(), userInfo.restoCode(), userInfo.type())
                .map(user -> {
                    UUID restoCode = parseUUID(userInfo.restoCode());
                    UUID ownerCode = parseUUID(userInfo.ownerCode());
                    return new SecurityUser(user, restoCode, ownerCode, user.getRole(), user.getId(),userInfo.type());
                })
                .doOnSuccess(user -> logger.debug("✅ [CONTEXT] User context created: {}", user.getUser().getEmail()))
                .doOnError(error -> logger.error("❌ [CONTEXT] Error creating context: {}", error.getMessage()));

        } catch (Exception e) {
            logger.debug("🔐 [CONTEXT] Token validation failed: {}", e.getMessage());
            return Mono.empty();
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
               path.startsWith("/api/reservations/home") ||
               path.startsWith("/api/restaurants/home/search") ||
               path.startsWith("/api/prsnls/partenaires") ||
               path.startsWith("/swagger-ui") || 
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/webjars") || 
               path.equals("/swagger-ui.html") ||
               path.contains("/favicon.ico");
    }
}