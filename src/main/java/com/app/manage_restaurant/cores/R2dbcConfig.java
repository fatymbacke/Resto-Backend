package com.app.manage_restaurant.cores;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.core.DatabaseClient;

@Configuration
@EnableR2dbcRepositories(
        basePackages = "com.app.manage_restaurant.repositories"
)
public class R2dbcConfig {

    private final R2dbcProperties properties;

    public R2dbcConfig(R2dbcProperties properties) {
        this.properties = properties;
    }

    /**
     * Factory R2DBC de base (non filtrée)
     */
    @Bean("baseConnectionFactory")
    public ConnectionFactory baseConnectionFactory() {
        return ConnectionFactoryBuilder.withUrl(properties.getUrl())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .build();
    }

    /**
     * Factory filtrée pour ownerCode/restoCode
     */
    @Bean("filteredConnectionFactory")
    @Primary
    public ConnectionFactory filteredConnectionFactory(
            @Qualifier("baseConnectionFactory") ConnectionFactory baseFactory
    ) {
        return new FilteredConnectionFactory(baseFactory);
    }

    /**
     * DatabaseClient basé sur la factory filtrée
     */
    @Bean
    @Primary
    public DatabaseClient databaseClient(
            @Qualifier("filteredConnectionFactory") ConnectionFactory filteredConnectionFactory
    ) {
        return DatabaseClient.create(filteredConnectionFactory);
    }

    /**
     * R2dbcEntityTemplate pour les opérations CRUD dynamiques
     */
    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(
            @Qualifier("filteredConnectionFactory") ConnectionFactory filteredConnectionFactory
    ) {
        return new R2dbcEntityTemplate(filteredConnectionFactory);
    }
}
