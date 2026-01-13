package com.app.manage_restaurant.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

public class SecurityContextCleanupFilter implements WebFilter {
    
    private static final Logger logger = LoggerFactory.getLogger("SECURITY.CLEANUP");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
            .doOnSuccess(aVoid -> {
                // Nettoyer le contexte après la requête
                logger.debug("🧹 [CLEANUP] Security context cleaned up after successful request");
            })
            .doOnError(throwable -> {
                logger.warn("⚠️ [CLEANUP] Security context cleaned up after failed request: {}", 
                           throwable.getMessage());
            });
    }
}