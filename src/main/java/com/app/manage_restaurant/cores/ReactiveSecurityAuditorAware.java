package com.app.manage_restaurant.cores;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;

import com.app.manage_restaurant.entities.Person;

import reactor.core.publisher.Mono;

@Component
public class ReactiveSecurityAuditorAware implements ReactiveAuditorAware<UUID> {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveSecurityAuditorAware.class);

    // UUID par défaut si aucun utilisateur connecté (doit exister dans la table prsnl)
    private static final UUID DEFAULT_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Override
    public Mono<UUID> getCurrentAuditor() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .filter(auth -> auth != null && auth.isAuthenticated())
                .flatMap(auth -> {
                    Object principal = auth.getPrincipal();

                    if (principal instanceof Person user) {
                        logger.info("[AuditorAware] Utilisateur connecté: {} {}", user.getFirstname(), user.getLastname());
                        logger.info("[AuditorAware] UUID récupéré: {}", user.getId());
                        return Mono.justOrEmpty(user.getId());
                    } else {
                        logger.warn("[AuditorAware] Principal non reconnu, utilisation du DEFAULT_USER_ID={}", DEFAULT_USER_ID);
                        return Mono.just(DEFAULT_USER_ID);
                    }
                })
                .defaultIfEmpty(DEFAULT_USER_ID);
    }
}
