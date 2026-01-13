package com.app.manage_restaurant.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
// Désactiver les auto-configurations de sécurité indésirables
@EnableConfigurationProperties
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class SecurityConfig {

    private final JwtService jwtService;
    private final ReactiveAuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtService jwtService,
                          ReactiveAuthenticationManager authManager,
                          PasswordEncoder passwordEncoder,
                          CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.authManager = authManager;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(authManager, jwtService, userDetailsService.getRepository());
    }

    @Bean
    public SecurityContextFilter securityContextFilter() {
        return new SecurityContextFilter(jwtService, userDetailsService.getRepository());
    }

    @Bean
    public SecurityContextCleanupFilter securityContextCleanupFilter() {
        return new SecurityContextCleanupFilter();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .cors().configurationSource(corsConfigurationSource()).and()
            .csrf().disable()
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers(
                    "/api/auth/login",
                    "/api/restaurants/home",
                    "/api/reservations/home",
                    "/api/commands/home",
                    "/api/restaurants/home/search",
                    "/api/prsnls/partenaires/**",
                     "/api/restaurants/specials",

                    "/swagger-ui.html", 
                    "/swagger-ui/**", 
                    "/v3/api-docs/**",
                    "/webjars/**", 
                    "/favicon.ico",
                    "/files/**"
                ).permitAll()
                .anyExchange().authenticated()
            )
            // ✅ ORDRE CORRIGÉ : Un seul filtre d'authentification
            .addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            // ✅ SecurityContextFilter AVANT l'authentification
            .addFilterBefore(securityContextFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            // ✅ Cleanup APRÈS l'authentification
            .addFilterAfter(securityContextCleanupFilter(), SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        config.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}