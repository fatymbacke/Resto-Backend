package com.app.manage_restaurant.security;

import java.util.UUID;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public class SecurityContext {
    private static final Logger logger = LoggerFactory.getLogger(SecurityContext.class);
    private static final String CURRENT_USER = "CURRENT_USER";

    // Retourner une Function pour contextWrite
    public static Function<Context, Context> contextWithUser(SecurityUser user) {  
        return ctx -> {
            if (user != null && user.getUser() != null) {
                logger.info("🔄 Setting user in context: {}", user.getUser().getEmail());
            } else {
                logger.warn("⚠️ Setting null user in context");
            }
            return ctx.put(CURRENT_USER, user);
        };
    }

    // Lire l'utilisateur courant depuis le contexte Reactor
    public static Mono<SecurityUser> getCurrentUser() {
        return Mono.deferContextual(ctx -> {
            SecurityUser user = ctx.getOrDefault(CURRENT_USER, null);
            if (user != null && user.getUser() != null) {
                logger.info("📖 Reading user from context: {}", user.getUser().getEmail());
            } else {
                logger.warn("🚨 No user found in Reactor context");
                logger.debug("Context keys available: {}", ctx.stream()
                    .map(entry -> entry.getKey().toString())
                    .toList());
            }
            return Mono.justOrEmpty(user);
        });
    }

    public static Mono<UUID> getCurrentOwnerCode() {
        logger.debug("🔑 Getting current owner code");
        return getCurrentUser()
            .doOnNext(user -> 
                logger.debug("📋 Retrieved owner code: {}", user.getOwnerCode()))
            .map(SecurityUser::getOwnerCode)
            .doOnError(error -> 
                logger.error("💥 Error retrieving owner code: {}", error.getMessage(), error));
    }

    public static Mono<UUID> getCurrentRestoCode() {
        logger.debug("🏪 Getting current restaurant code");
        return getCurrentUser()
            .doOnNext(user -> 
                logger.debug("📋 Retrieved restaurant code: {}", user.getRestoCode()))
            .map(SecurityUser::getRestoCode)
            .doOnError(error -> 
                logger.error("💥 Error retrieving restaurant code: {}", error.getMessage(), error));
    }
    public static Mono<String> getCurrentRole() {
        logger.debug("🏪 Getting current user role");
        return getCurrentUser()
            .doOnNext(user -> 
                logger.debug("📋 Retrieved user role: {}", user.getUser().getRole()))
            .map(user-> user.getUser().getRole())
            .doOnError(error -> 
                logger.error("💥 Error retrieving user role: {}", error.getMessage(), error));
    }

    // Méthode utilitaire pour debugger le contexte
    public static Mono<Void> debugContext() {
        return Mono.deferContextual(ctx -> {
            if (logger.isDebugEnabled()) {
                logger.debug("=== 🔍 CONTEXT DEBUG ===");
                logger.debug("Context size: {}", ctx.size());
                logger.debug("Has CURRENT_USER: {}", ctx.hasKey(CURRENT_USER));
                
                if (ctx.hasKey(CURRENT_USER)) {
                    Object user = ctx.get(CURRENT_USER);
                    logger.debug("User type: {}", user != null ? user.getClass().getName() : "null");
                    if (user instanceof SecurityUser securityUser && securityUser.getUser() != null) {
                        logger.debug("User email: {}", securityUser.getUser().getEmail());
                        logger.debug("OwnerCode: {}", securityUser.getOwnerCode());
                        logger.debug("RestoCode: {}", securityUser.getRestoCode());
                    }
                } else {
                    logger.debug("Available context keys: {}", 
                        ctx.stream().map(entry -> entry.getKey().toString()).toList());
                }
                logger.debug("=== END CONTEXT DEBUG ===");
            }
            return Mono.empty();
        });
    }
    public static void clearContext() {
        // Si vous utilisez aussi SecurityContextHolder quelque part
        org.springframework.security.core.context.SecurityContextHolder.clearContext();;
        logger.debug("🧹 SecurityContext cleared");
    }
}