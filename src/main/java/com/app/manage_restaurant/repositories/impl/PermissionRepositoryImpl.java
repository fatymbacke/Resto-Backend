package com.app.manage_restaurant.repositories.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.app.manage_restaurant.cores.BaseRepositoryImpl;
import com.app.manage_restaurant.cores.EnumFilter;
import com.app.manage_restaurant.entities.Permission;
import com.app.manage_restaurant.repositories.PermissionRepository;
import com.app.manage_restaurant.security.SecurityUser;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class PermissionRepositoryImpl extends BaseRepositoryImpl<Permission, UUID> implements PermissionRepository {
    
    private final DatabaseClient databaseClient;
    
    public PermissionRepositoryImpl(R2dbcEntityTemplate template) {
        super(template, Permission.class);
        this.databaseClient = template.getDatabaseClient();
    }

    @Override
    public Mono<Permission> findByCode(String code) {
        logger.debug("Finding Permission by code: {}", code);
        
        return applyGlobalFilter(Query.query(Criteria.where("code").is(code)),EnumFilter.ALL)
                .flatMap(q -> template.selectOne(q, Permission.class))
                .doOnSuccess(permission -> {
                    if (permission != null) {
                        logger.debug("Found Permission by code: {}", code);
                    } else {
                        logger.debug("No Permission found by code: {}", code);
                    }
                })
                .doOnError(error -> logger.error("Error finding Permission by code {}: {}", 
                    code, error.getMessage(), error));
    }

    @Override
    public Mono<Boolean> existsByCode(String code) {
        logger.debug("Checking existence of Permission by code: {}", code);
        
        return applyGlobalFilter(Query.query(Criteria.where("code").is(code)),EnumFilter.ALL)
                .flatMap(q -> template.exists(q, Permission.class))
                .doOnSuccess(exists -> logger.debug("Existence check result for Permission code {}: {}", code, exists))
                .doOnError(error -> logger.error("Error checking existence of Permission by code {}: {}", 
                    code, error.getMessage(), error));
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        logger.debug("Checking existence of Permission by name: {}", name);
        
        return applyGlobalFilter(Query.query(Criteria.where("name").is(name)),EnumFilter.ALL)
                .flatMap(q -> template.exists(q, Permission.class))
                .doOnSuccess(exists -> logger.debug("Existence check result for Permission name {}: {}", name, exists))
                .doOnError(error -> logger.error("Error checking existence of Permission by name {}: {}", 
                    name, error.getMessage(), error));
    }

    @Override
    public Flux<Permission> findByCodeIn(Set<String> codes) {
        logger.debug("Finding Permissions by codes: {}", codes);
        
        if (codes == null || codes.isEmpty()) {
            logger.debug("No codes provided, returning empty Flux");
            return Flux.empty();
        }
        
        return applyGlobalFilter(Query.query(Criteria.where("code").in(codes)),EnumFilter.ALL)
                .flatMapMany(q -> template.select(q, Permission.class))
                .doOnComplete(() -> logger.debug("Completed finding Permissions by codes: {}", codes))
                .doOnError(error -> logger.error("Error finding Permissions by codes {}: {}", 
                    codes, error.getMessage(), error));
    }

    @Override
    public Flux<Permission> findByModuleId(UUID moduleId) {
        logger.debug("Finding Permissions by moduleId: {}", moduleId);
        
        String sql = "SELECT p.* FROM permissions p WHERE p.module_id = :moduleId";
        
        return Flux.deferContextual(ctx -> {
            
            StringBuilder finalSql = new StringBuilder(sql);          
            
            
            var dbClient = databaseClient.sql(finalSql.toString())
            		.bind("moduleId", moduleId);
            
            
            
            return dbClient.map((row, metadata) -> template.getConverter().read(Permission.class, row, metadata))
                    .all();
        })
        .doOnComplete(() -> logger.debug("Completed finding Permissions by moduleId: {}", moduleId))
        .doOnError(error -> logger.error("Error finding Permissions by moduleId {}: {}", 
            moduleId, error.getMessage(), error));
    }

    @Override
    public Flux<Permission> findByIds(Set<UUID> ids) {
        logger.debug("Finding Permissions by ids: {}", ids);
        
        if (ids == null || ids.isEmpty()) {
            logger.debug("No ids provided, returning empty Flux");
            return Flux.empty();
        }
        
        return applyGlobalFilter(Query.query(Criteria.where("id").in(ids)),EnumFilter.ALL)
                .flatMapMany(q -> template.select(q, Permission.class))
                .doOnComplete(() -> logger.debug("Completed finding Permissions by ids: {}", ids))
                .doOnError(error -> logger.error("Error finding Permissions by ids {}: {}", 
                    ids, error.getMessage(), error));
    }

    @Override
    public Mono<Long> countByModuleId(UUID moduleId) {
        logger.debug("Counting Permissions by moduleId: {}", moduleId);
        
        String sql = "SELECT COUNT(*) FROM permissions p WHERE p.module_id = :moduleId";
        
        return Mono.deferContextual(ctx -> {
        	SecurityUser securityUser = ctx.getOrDefault("CURRENT_USER", null);
            
            StringBuilder finalSql = new StringBuilder(sql);
            
            // Ajouter les conditions de sécurité si disponibles
            if (securityUser != null) {
                if (securityUser.getOwnerCode() != null) {
                    finalSql.append(" AND p.owner_code = :ownerCode");
                }
                if (securityUser.getRestoCode() != null) {
                    finalSql.append(" AND p.resto_code = :restoCode");
                }
            }
            
            var dbClient = databaseClient.sql(finalSql.toString())
                    .bind("moduleId", moduleId);
            
            if (securityUser != null) {
                if (securityUser.getOwnerCode() != null) {
                    dbClient = dbClient.bind("ownerCode", securityUser.getOwnerCode());
                }
                if (securityUser.getRestoCode() != null) {
                    dbClient = dbClient.bind("restoCode", securityUser.getRestoCode());
                }
            }
            
            return dbClient.map((row, metadata) -> row.get(0, Long.class))
                    .one();
        })
        .doOnSuccess(count -> logger.debug("Permissions count for moduleId {}: {}", moduleId, count))
        .doOnError(error -> logger.error("Error counting Permissions by moduleId {}: {}", 
            moduleId, error.getMessage(), error));
    }

    @Override
    public Mono<Permission> findByIdWithModule(UUID id) {
        logger.debug("Finding Permission by id with module details: {}", id);
        
        String sql = "SELECT p.*, m.name as module_name, m.description as module_description " +
                    "FROM permissions p LEFT JOIN modules m ON p.module_id = m.id " +
                    "WHERE p.id = :id";
        
        return Mono.deferContextual(ctx -> {
        	SecurityUser securityUser = ctx.getOrDefault("CURRENT_USER", null);
            
            StringBuilder finalSql = new StringBuilder(sql);
            
            // Ajouter les conditions de sécurité si disponibles
            if (securityUser != null) {
                if (securityUser.getOwnerCode() != null) {
                    finalSql.append(" AND p.owner_code = :ownerCode");
                }
                if (securityUser.getRestoCode() != null) {
                    finalSql.append(" AND p.resto_code = :restoCode");
                }
            }
            
            var dbClient = databaseClient.sql(finalSql.toString())
                    .bind("id", id);
            
            if (securityUser != null) {
                if (securityUser.getOwnerCode() != null) {
                    dbClient = dbClient.bind("ownerCode", securityUser.getOwnerCode());
                }
                if (securityUser.getRestoCode() != null) {
                    dbClient = dbClient.bind("restoCode", securityUser.getRestoCode());
                }
            }
            
            return dbClient.map((row, metadata) -> {
                    Permission permission = template.getConverter().read(Permission.class, row, metadata);
                    
                    // Ajouter les informations du module si disponibles
                    if (row.get("module_name", String.class) != null) {
                        // Vous pouvez créer un DTO ou étendre l'entité Permission pour stocker ces infos
                        // Pour l'instant, on les logge simplement
                        logger.debug("Found Permission with module: {} - {}", 
                            row.get("module_name", String.class),
                            row.get("module_description", String.class));
                    }
                    
                    return permission;
                })
                .one();
        })
        .doOnSuccess(permission -> {
            if (permission != null) {
                logger.debug("Found Permission with module details by id: {}", id);
            } else {
                logger.debug("No Permission found by id: {}", id);
            }
        })
        .doOnError(error -> logger.error("Error finding Permission with module by id {}: {}", 
            id, error.getMessage(), error));
    }

    @Override
    public Flux<Permission> findPermissionsWithRole(UUID id) {
        logger.debug("Finding Permissions with role id: {}", id);
        
        
        String sql = "SELECT p.* FROM permissions p LEFT JOIN role_permissions m ON p.id = m.permission_id WHERE m.role_id = :id";
        
        return Flux.deferContextual(ctx -> {
            
            StringBuilder finalSql = new StringBuilder(sql);          
           
            
            var dbClient = databaseClient.sql(finalSql.toString())
                    .bind("id", id);
            
            
            
            return dbClient.map((row, metadata) -> template.getConverter().read(Permission.class, row, metadata))
                    .all();
        })
        .doOnComplete(() -> logger.debug("Completed finding Permissions with role id: {}", id))
        .doOnError(error -> logger.error("Error finding Permissions with role id {}: {}", 
            id, error.getMessage(), error));
    }

    // Méthode utilitaire pour trouver les permissions actives
    @Override
    public Flux<Permission> findActivePermissions() {
        logger.debug("Finding active Permissions");
        
        return applyGlobalFilter(Query.query(Criteria.where("active").is(true)),EnumFilter.NOTHING)
                .flatMapMany(q -> template.select(q, Permission.class))
                .doOnComplete(() -> logger.debug("Completed finding active Permissions"))
                .doOnError(error -> logger.error("Error finding active Permissions: {}", error.getMessage(), error));
    }
//    DELIVERY_ASSIGN_DRIVER
    // Méthode utilitaire pour trouver les permissions par nom contenant
    public Flux<Permission> findByNameContaining(String name) {
        logger.debug("Finding Permissions by name containing: {}", name);
        
        return applyGlobalFilter(Query.query(Criteria.where("name").like("%" + name + "%")),EnumFilter.ALL)
                .flatMapMany(q -> template.select(q, Permission.class))
                .doOnComplete(() -> logger.debug("Completed finding Permissions by name containing: {}", name))
                .doOnError(error -> logger.error("Error finding Permissions by name containing {}: {}", 
                    name, error.getMessage(), error));
    }

	@Override
	public Flux<Permission> search(Map<String, Object> filters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<Permission> findPermissionsWithRoles(List<UUID> roleIds) {
	    logger.debug("Finding Permissions with role ids: {}", roleIds);
	    
	    // Validation des paramètres
	    if (roleIds == null || roleIds.isEmpty()) {
	        logger.warn("Role IDs list is empty or null, returning empty flux");
	        return Flux.empty();
	    }
	    
	    String sql = "SELECT DISTINCT p.* FROM permissions p "
	               + "INNER JOIN role_permissions rp ON p.id = rp.permission_id "
	               + "WHERE rp.role_id IN (:roleIds)";
	    
	    return databaseClient.sql(sql)
	            .bind("roleIds", roleIds)
	            .map((row, metadata) -> template.getConverter().read(Permission.class, row, metadata))
	            .all()
	            .doOnSubscribe(s -> logger.debug("Starting query for role ids: {}", roleIds))
	            .doOnComplete(() -> logger.debug("Completed finding Permissions with role ids: {}", roleIds))
	            .doOnError(error -> logger.error("Error finding Permissions with role ids {}: {}", 
	                roleIds, error.getMessage(), error))
	            .onErrorResume(error -> {
	                logger.error("Failed to fetch permissions for role ids: {}", roleIds, error);
	                return Flux.empty(); // ou lancer une exception selon votre stratégie
	            });
	}

	
}