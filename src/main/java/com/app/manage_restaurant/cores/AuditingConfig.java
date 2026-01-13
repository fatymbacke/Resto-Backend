package com.app.manage_restaurant.cores;

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

@Configuration
@EnableR2dbcAuditing(auditorAwareRef = "auditorProvider")
public class AuditingConfig {

    @Bean
    public ReactiveAuditorAware<UUID> auditorProvider() {
        return new ReactiveSecurityAuditorAware();
    }
}
