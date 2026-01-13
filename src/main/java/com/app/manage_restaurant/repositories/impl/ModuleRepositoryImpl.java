package com.app.manage_restaurant.repositories.impl;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.app.manage_restaurant.cores.BaseRepositoryImpl;
import com.app.manage_restaurant.cores.EnumFilter;
import com.app.manage_restaurant.entities.Module;
import com.app.manage_restaurant.repositories.ModuleRepository;
import com.app.manage_restaurant.security.SecurityUser;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ModuleRepositoryImpl extends BaseRepositoryImpl<Module, UUID> implements ModuleRepository {
    
    private final DatabaseClient databaseClient;
    
    public ModuleRepositoryImpl(R2dbcEntityTemplate template) {
        super(template, Module.class);
        this.databaseClient = template.getDatabaseClient();
    }

    @Override
    public Mono<Module> findByName(String name) {
        logger.debug("Finding Module by name: {}", name);
        
        return applyGlobalFilter(Query.query(Criteria.where("name").is(name)),EnumFilter.ALL)
                .flatMap(q -> template.selectOne(q, Module.class))
                .doOnSuccess(module -> {
                    if (module != null) {
                        logger.debug("Found Module by name: {}", name);
                    } else {
                        logger.debug("No Module found by name: {}", name);
                    }
                })
                .doOnError(error -> logger.error("Error finding Module by name {}: {}", 
                    name, error.getMessage(), error));
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        logger.debug("Checking existence of Module by name: {}", name);
        
        return applyGlobalFilter(Query.query(Criteria.where("name").is(name)),EnumFilter.ALL)
                .flatMap(q -> template.exists(q, Module.class))
                .doOnSuccess(exists -> logger.debug("Existence check result for Module name {}: {}", name, exists))
                .doOnError(error -> logger.error("Error checking existence of Module by name {}: {}", 
                    name, error.getMessage(), error));
    }

    @Override
    public Flux<Module> findByNameContaining(String name) {
        logger.debug("Finding Modules by name containing: {}", name);
        
        // Utilisation de requête native avec ILIKE pour la recherche insensible à la casse
        String sql = "SELECT m.* FROM modules m WHERE m.name ILIKE :name";
        
        return Mono.deferContextual(ctx -> {
            SecurityUser securityUser = ctx.getOrDefault("CURRENT_USER", null);
            
            StringBuilder finalSql = new StringBuilder(sql);
            
            // Ajouter les conditions de sécurité si disponibles
            if (securityUser != null) {
                if (securityUser.getOwnerCode() != null) {
                    finalSql.append(" AND m.owner_code = :ownerCode");
                }
                if (securityUser.getRestoCode() != null) {
                    finalSql.append(" AND m.resto_code = :restoCode");
                }
            }
            
            var dbClient = databaseClient.sql(finalSql.toString())
                    .bind("name", "%" + name + "%");
            
            if (securityUser != null) {
                if (securityUser.getOwnerCode() != null) {
                    dbClient = dbClient.bind("ownerCode", securityUser.getOwnerCode());
                }
                if (securityUser.getRestoCode() != null) {
                    dbClient = dbClient.bind("restoCode", securityUser.getRestoCode());
                }
            }
            
            return Mono.just(dbClient.map((row, metadata) -> {
                    Module module = new Module();
                    module.setId(row.get("id", UUID.class));
                    module.setName(row.get("name", String.class));
                    module.setDescription(row.get("description", String.class));
                    module.setActive(row.get("active", Boolean.class));
                    module.setOwnerCode(row.get("owner_code", UUID.class));
                    module.setRestoCode(row.get("resto_code", UUID.class));
                    return module;
                })
                .all());
        })
        .flatMapMany(flux -> flux) // ← Conversion de Mono<Flux<Module>> vers Flux<Module>
        .doOnComplete(() -> logger.debug("Completed finding Modules by name containing: {}", name))
        .doOnError(error -> logger.error("Error finding Modules by name containing {}: {}", 
            name, error.getMessage(), error));
    }

    // Méthode alternative pour findByNameContaining utilisant Criteria (si pas besoin de ILIKE)
    public Flux<Module> findByNameContainingWithCriteria(String name) {
        logger.debug("Finding Modules by name containing with criteria: {}", name);
        
        return applyGlobalFilter(Query.query(Criteria.where("name").like("%" + name + "%")),EnumFilter.ALL)
                .flatMapMany(q -> template.select(q, Module.class))
                .doOnComplete(() -> logger.debug("Completed finding Modules by name containing: {}", name))
                .doOnError(error -> logger.error("Error finding Modules by name containing {}: {}", 
                    name, error.getMessage(), error));
    }

    // Méthode utilitaire pour trouver les modules actifs
    public Flux<Module> findActiveModules() {
        logger.debug("Finding active Modules");
        
        return applyGlobalFilter(Query.query(Criteria.where("active").is(true)),EnumFilter.ALL)
                .flatMapMany(q -> template.select(q, Module.class))
                .doOnComplete(() -> logger.debug("Completed finding active Modules"))
                .doOnError(error -> logger.error("Error finding active Modules: {}", error.getMessage(), error));
    }

    // Méthode utilitaire pour trouver les modules par statut
    public Flux<Module> findByActiveStatus(Boolean active) {
        logger.debug("Finding Modules by active status: {}", active);
        
        return applyGlobalFilter(Query.query(Criteria.where("active").is(active)),EnumFilter.ALL)
                .flatMapMany(q -> template.select(q, Module.class))
                .doOnComplete(() -> logger.debug("Completed finding Modules by active status: {}", active))
                .doOnError(error -> logger.error("Error finding Modules by active status {}: {}", 
                    active, error.getMessage(), error));
    }

    // Méthode utilitaire pour compter les modules par statut
    public Mono<Long> countByActiveStatus(Boolean active) {
        logger.debug("Counting Modules by active status: {}", active);
        
        return applyGlobalFilter(Query.query(Criteria.where("active").is(active)),EnumFilter.ALL)
                .flatMap(q -> template.select(q, Module.class).count())
                .doOnSuccess(count -> logger.debug("Modules count for active status {}: {}", active, count))
                .doOnError(error -> logger.error("Error counting Modules by active status {}: {}", 
                    active, error.getMessage(), error));
    }

    // Méthode utilitaire pour trouver les modules par ordre
    public Flux<Module> findByOrder(Integer order) {
        logger.debug("Finding Modules by order: {}", order);
        
        return applyGlobalFilter(Query.query(Criteria.where("order").is(order)),EnumFilter.ALL)
                .flatMapMany(q -> template.select(q, Module.class))
                .doOnComplete(() -> logger.debug("Completed finding Modules by order: {}", order))
                .doOnError(error -> logger.error("Error finding Modules by order {}: {}", 
                    order, error.getMessage(), error));
    }

    // Méthode utilitaire pour trouver les modules avec ordre supérieur ou égal
    public Flux<Module> findByOrderGreaterThanEqual(Integer minOrder) {
        logger.debug("Finding Modules with order >= : {}", minOrder);
        
        return applyGlobalFilter(Query.query(Criteria.where("order").greaterThanOrEquals(minOrder)),EnumFilter.ALL)
                .flatMapMany(q -> template.select(q, Module.class))
                .doOnComplete(() -> logger.debug("Completed finding Modules with order >= {}", minOrder))
                .doOnError(error -> logger.error("Error finding Modules with order >= {}: {}", 
                    minOrder, error.getMessage(), error));
    }

	@Override
	public Flux<Module> search(Map<String, Object> filters) {
		// TODO Auto-generated method stub
		return null;
	}
}