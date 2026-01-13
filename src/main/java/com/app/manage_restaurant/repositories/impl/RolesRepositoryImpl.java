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
import com.app.manage_restaurant.entities.Roles;
import com.app.manage_restaurant.repositories.RolesRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class RolesRepositoryImpl extends BaseRepositoryImpl<Roles, UUID> implements RolesRepository {
    
    private final DatabaseClient databaseClient;
    
    public RolesRepositoryImpl(R2dbcEntityTemplate template) {
        super(template, Roles.class);
        this.databaseClient = template.getDatabaseClient();
    }

    @Override
    public Mono<Roles> findByName(String name) {
        logger.debug("Finding Role by name: {}", name);
        
        return applyGlobalFilter(Query.query(Criteria.where("name").is(name)),EnumFilter.ALL)
                .flatMap(q -> template.selectOne(q, Roles.class))
                .doOnSuccess(role -> {
                    if (role != null) {
                        logger.debug("Found Role by name: {}", name);
                    } else {
                        logger.debug("No Role found by name: {}", name);
                    }
                })
                .doOnError(error -> logger.error("Error finding Role by name {}: {}", 
                    name, error.getMessage(), error));
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        logger.debug("Checking existence of Role by name: {}", name);
        
        return applyGlobalFilter(Query.query(Criteria.where("name").is(name)),EnumFilter.ALL)
                .flatMap(q -> template.exists(q, Roles.class))
                .doOnSuccess(exists -> logger.debug("Existence check result for Role name {}: {}", name, exists))
                .doOnError(error -> logger.error("Error checking existence of Role by name {}: {}", 
                    name, error.getMessage(), error));
    }

    @Override
    public Mono<Roles> findByIsDefaultTrue() {
        logger.debug("Finding default Role");
        
        return applyGlobalFilter(Query.query(Criteria.where("is_default").is(true)),EnumFilter.ALL)
                .flatMap(q -> template.selectOne(q, Roles.class))
                .doOnSuccess(role -> {
                    if (role != null) {
                        logger.debug("Found default Role: {}", role.getName());
                    } else {
                        logger.debug("No default Role found");
                    }
                })
                .doOnError(error -> logger.error("Error finding default Role: {}", error.getMessage(), error));
    }

    @Override
    public Flux<Roles> findByActive(Boolean active) {
        logger.debug("Finding Roles by active status: {}", active);
        
        return applyGlobalFilter(Query.query(Criteria.where("active").is(active)),EnumFilter.ALL)
                .flatMapMany(q -> template.select(q, Roles.class))
                .doOnComplete(() -> logger.debug("Completed finding Roles by active status: {}", active))
                .doOnError(error -> logger.error("Error finding Roles by active status {}: {}", 
                    active, error.getMessage(), error));
    }

    @Override
    public Mono<Long> countUsersByRoleId(UUID roleId) {
        logger.debug("Counting users for roleId: {}", roleId);
        
        String sql = "SELECT COUNT(*) FROM prsnl p WHERE p.role_id = :roleId";
        
        return databaseClient.sql(sql)
                .bind("roleId", roleId)
                .map((row, metadata) -> row.get(0, Long.class))
                .one()
                .doOnSuccess(count -> logger.debug("User count for roleId {}: {}", roleId, count))
                .doOnError(error -> logger.error("Error counting users for roleId {}: {}", 
                    roleId, error.getMessage(), error));
    }

    @Override
    public Flux<Roles> findAllWithPermissionsCount() {
        logger.debug("Finding all Roles with permissions count");
        
        String sql = "SELECT r.*, COUNT(p.id) as permissions_count " +
                    "FROM roles r LEFT JOIN role_permissions rp ON r.id = rp.role_id " +
                    "LEFT JOIN permissions p ON rp.permission_id = p.id " +
                    "GROUP BY r.id";
        
        return databaseClient.sql(sql)
                .map((row, metadata) -> {
                    Roles role = new Roles();
                    role.setId(row.get("id", UUID.class));
                    role.setName(row.get("name", String.class));
                    role.setDescription(row.get("description", String.class));
                    role.setActive(row.get("active", Boolean.class));
                    role.setOwnerCode(row.get("owner_code", UUID.class));
                    role.setRestoCode(row.get("resto_code", UUID.class));
                    
                    // Récupérer le count des permissions
                    Long permissionsCount = row.get("permissions_count", Long.class);
                    // Vous pouvez stocker cette information dans un champ temporaire si nécessaire
                    // ou créer un DTO spécifique
                    
                    return role;
                })
                .all()
                .doOnComplete(() -> logger.debug("Completed finding all Roles with permissions count"))
                .doOnError(error -> logger.error("Error finding Roles with permissions count: {}", 
                    error.getMessage(), error));
    }

    @Override
    public Mono<Roles> findDefaultRole() {
        logger.debug("Finding default Role (alternative method)");
        
        // Utilisation de requête native pour plus de précision
        String sql = "SELECT r.* FROM roles r WHERE r.is_default = true";
        
        return databaseClient.sql(sql)
                .map((row, metadata) -> {
                    Roles role = new Roles();
                    role.setId(row.get("id", UUID.class));
                    role.setName(row.get("name", String.class));
                    role.setDescription(row.get("description", String.class));
                    role.setActive(row.get("active", Boolean.class));
                    role.setOwnerCode(row.get("owner_code", UUID.class));
                    role.setRestoCode(row.get("resto_code", UUID.class));
                    return role;
                })
                .one()
                .doOnSuccess(role -> {
                    if (role != null) {
                        logger.debug("Found default Role: {}", role.getName());
                    } else {
                        logger.debug("No default Role found");
                    }
                })
                .doOnError(error -> logger.error("Error finding default Role: {}", error.getMessage(), error));
    }

    // Méthode utilitaire pour trouver les rôles par nom avec like
    public Flux<Roles> findByNameContaining(String name) {
        logger.debug("Finding Roles by name containing: {}", name);
        
        return applyGlobalFilter(Query.query(Criteria.where("name").like("%" + name + "%")),EnumFilter.ALL)
                .flatMapMany(q -> template.select(q, Roles.class))
                .doOnComplete(() -> logger.debug("Completed finding Roles by name containing: {}", name))
                .doOnError(error -> logger.error("Error finding Roles by name containing {}: {}", 
                    name, error.getMessage(), error));
    }

    // Méthode utilitaire pour compter tous les rôles actifs
    public Mono<Long> countActiveRoles() {
        logger.debug("Counting active Roles");
        
        return applyGlobalFilter(Query.query(Criteria.where("active").is(true)),EnumFilter.ALL)
                .flatMap(q -> template.select(q, Roles.class).count())
                .doOnSuccess(count -> logger.debug("Active Roles count: {}", count))
                .doOnError(error -> logger.error("Error counting active Roles: {}", error.getMessage(), error));
    }

    @Override
    public Flux<Roles> findByResto(UUID resto) {
        logger.debug("Finding roles for restaurant: {}", resto);
        
        // Récupère les rôles spécifiques au restaurant + les rôles par défaut globaux
        String sql = "SELECT r.* FROM roles r WHERE r.resto_code = :restoCode ";
        
        return databaseClient.sql(sql)
                .bind("restoCode", resto)
                .map((row, metadata) -> {
                    Roles role = new Roles();
                    role.setId(row.get("id", UUID.class));
                    role.setName(row.get("name", String.class));
                    role.setDescription(row.get("description", String.class));
                    role.setActive(row.get("active", Boolean.class));
                    role.setRestoCode(row.get("resto_code", UUID.class));
                    role.setIsDefault(row.get("is_default", Boolean.class));
                    return role;
                })
                .all()
                .doOnNext(role -> {
                    logger.debug("Found role: {} for restaurant: {}", role.getName(), resto);
                })
                .doOnComplete(() -> {
                    logger.debug("Completed finding roles for restaurant: {}", resto);
                })
                .doOnError(error -> logger.error("Error finding roles for restaurant {}: {}", resto, error.getMessage(), error));
    }

	@Override
	public Flux<Roles> search(Map<String, Object> filters) {
		// TODO Auto-generated method stub
		return null;
	}
}