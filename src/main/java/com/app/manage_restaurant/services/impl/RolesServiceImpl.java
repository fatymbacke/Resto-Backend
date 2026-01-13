package com.app.manage_restaurant.services.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.manage_restaurant.cores.BaseServiceImpl;
import com.app.manage_restaurant.cores.EnumFilter;
import com.app.manage_restaurant.dtos.request.RolesRequest;
import com.app.manage_restaurant.dtos.response.RolesResponse;
import com.app.manage_restaurant.entities.Roles;
import com.app.manage_restaurant.exceptions.entities.EntityNotFoundException;
import com.app.manage_restaurant.mapper.RolesMapper;
import com.app.manage_restaurant.repositories.PermissionRepository;
import com.app.manage_restaurant.repositories.RolesRepository;
import com.app.manage_restaurant.services.GenericDuplicateChecker;
import com.app.manage_restaurant.services.RolesService;
import com.app.manage_restaurant.utilities.FileStorageUtil;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RolesServiceImpl extends BaseServiceImpl<Roles, RolesRequest, RolesResponse, UUID> implements RolesService {

    private final RolesRepository rolesRepository;
    private final PermissionRepository permissionRepository;
    private final DatabaseClient databaseClient;
    private final Logger logger;
    protected final R2dbcEntityTemplate template;

    public RolesServiceImpl(RolesRepository rolesRepository,
                           R2dbcEntityTemplate template,
         		             FileStorageUtil fileStorageUtil,
                           PermissionRepository permissionRepository,
                           DatabaseClient databaseClient,
                           ReactiveExceptionHandler exceptionHandler,
                           GenericDuplicateChecker duplicateChecker) {
        super(rolesRepository,fileStorageUtil, template, RolesMapper::toEntity, RolesMapper::toResponse,
              Roles.class, "Roles", exceptionHandler, duplicateChecker);

        this.rolesRepository = rolesRepository;
        this.permissionRepository = permissionRepository;
        this.databaseClient = databaseClient;
        this.logger = LoggerFactory.getLogger(RolesServiceImpl.class);
        this.template = template;
        
        logger.info("RolesServiceImpl initialized successfully");
    }

    // ==============================
    // UPDATE ROLE PERMISSIONS
    // ==============================
    @Transactional
    @Override
    public Mono<RolesResponse> updateRolePermissions(UUID roleId, List<UUID> permissionIds) {
        logger.info("🚀 Starting updateRolePermissions - Role ID: {}, Permissions count: {}", 
                   roleId, permissionIds != null ? permissionIds.size() : 0);
        
        if (roleId == null) {
            logger.error("❌ Role ID cannot be null for updateRolePermissions");
            return Mono.error(new IllegalArgumentException("Role ID cannot be null"));
        }
        
        if (permissionIds == null) {
            logger.warn("⚠️ Permission IDs list is null, will clear all permissions for role: {}", roleId);
        } else {
            logger.debug("📋 Permissions to assign: {}", permissionIds);
        }

        return databaseClient.sql("DELETE FROM role_permissions WHERE role_id = :roleId")
                .bind("roleId", roleId)
                .fetch()
                .rowsUpdated()
                .doOnSuccess(deletedCount -> 
                    logger.info("🗑️ Cleared {} existing permissions for role: {}", deletedCount, roleId))
                .doOnError(error -> 
                    logger.error("❌ Failed to clear existing permissions for role {}: {}", roleId, error.getMessage()))
                .thenMany(Flux.fromIterable(permissionIds != null ? permissionIds : List.of()))
                .flatMap(pid -> {
                    logger.debug("➕ Adding permission ID: {} to role: {}", pid, roleId);
                    return databaseClient.sql(
                        "INSERT INTO role_permissions (role_id, permission_id) VALUES (:roleId, :pid)")
                        .bind("roleId", roleId)
                        .bind("pid", pid)
                        .fetch()
                        .rowsUpdated()
                        .doOnSuccess(inserted -> {
                            if (inserted > 0) {
                                logger.debug("✅ Successfully added permission {} to role {}", pid, roleId);
                            }
                        })
                        .doOnError(error -> 
                            logger.error("❌ Failed to add permission {} to role {}: {}", pid, roleId, error.getMessage()));
                })
                .collectList()
                .doOnSuccess(insertResults -> 
                    logger.info("📊 Successfully added {} permissions to role: {}", insertResults.size(), roleId))
                .then()
                .then(rolesRepository.findById(roleId)
                        .switchIfEmpty(Mono.defer(() -> {
                            logger.error("❌ Role not found after permissions update: {}", roleId);
                            return Mono.error(new EntityNotFoundException("Rôle", roleId));
                        }))
                        .doOnSuccess(role -> 
                            logger.info("✅ Successfully retrieved updated role: {}", roleId))
                )
                .map(role -> {
                    RolesResponse response = RolesMapper.toResponse(role);
                    logger.info("🎉 Successfully completed updateRolePermissions for role: {}", roleId);
                    return response;
                })
                .doOnError(error -> 
                    logger.error("💥 Critical error in updateRolePermissions for role {}: {}", 
                               roleId, error.getMessage(), error));
    }

    // ==============================
    // VALIDATION
    // ==============================
    @Override
    public Mono<Void> validate(RolesRequest request) {
        logger.debug("🔍 Starting validation for RolesRequest: name='{}'", 
                    request != null ? request.getName() : "null");
        
        return Mono.fromRunnable(() -> {
            if (request == null) {
                logger.error("❌ RolesRequest cannot be null");
                throw new IllegalArgumentException("La requête du rôle ne peut pas être nulle");
            }
            
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                logger.error("❌ Role name is required but was: '{}'", request.getName());
                throw new IllegalArgumentException("Le nom du rôle est obligatoire");
            }
            
            String name = request.getName().trim();
            if (name.length() < 2 || name.length() > 100) {
                logger.error("❌ Role name length invalid: {} (must be between 2-100)", name.length());
                throw new IllegalArgumentException("Le nom doit contenir entre 2 et 100 caractères");
            }
            
            if (request.getDescription() != null && request.getDescription().length() > 500) {
                logger.error("❌ Role description too long: {} characters (max 500)", 
                           request.getDescription().length());
                throw new IllegalArgumentException("La description ne peut pas dépasser 500 caractères");
            }
            
            logger.debug("✅ RolesRequest validation successful for: '{}'", name);
        });
    }

    // ==============================
    // CHAMPS UNIQUES
    // ==============================
    @Override
    public Map<String, Object> extractUniqueFields(RolesRequest request) {
        logger.debug("🔍 Extracting unique fields for role: '{}'", 
                    request != null ? request.getName() : "null");
        
        Map<String, Object> uniqueFields = Map.of("name", request.getName());
        
        logger.debug("📋 Unique fields extracted: {}", uniqueFields);
        return uniqueFields;
    }

    // ==============================
    // FIND ALL
    // ==============================
    @Override
    public Flux<RolesResponse> findAll() {
        logger.info("🔍 Starting to findAll roles");
        
        return rolesRepository.findAll()
                .doOnSubscribe(subscription -> 
                    logger.debug("📡 Subscribed to findAll roles stream"))
                .map(role -> {
                    logger.debug("📝 Mapping role to response: {}", role.getName());
                    return RolesMapper.toResponse(role);
                })
                .doOnComplete(() -> 
                    logger.info("✅ Successfully completed findAll roles"))
                .doOnError(error -> 
                    logger.error("❌ Error in findAll roles: {}", error.getMessage(), error));
    }
    
    // ==============================
    // FIND ALL ROLES WITH PERMISSIONS
    // ==============================
    public Flux<RolesResponse> findAllRolesWithPermissions(boolean active) {
        logger.info("🔍 Starting to findAllRolesWithPermissions with active status: {}", active);
        
        return rolesRepository.findByActive(active)
                .doOnSubscribe(subscription -> 
                    logger.debug("📡 Subscribed to findAllRolesWithPermissions stream, active: {}", active))
                .flatMap(role -> {
                    logger.debug("🔄 Fetching permissions for role: {}", role.getName());
                    return permissionRepository.findPermissionsWithRole(role.getId())
                            .collectList()
                            .map(permissions -> {
                                     logger.debug("📋 Found {} permissions for role: {}", 
                                           permissions.size(), role.getName());
                                role.setPermissions(new HashSet<>(permissions));
                                return role;
                            })
                            .doOnError(error -> 
                                logger.error("❌ Error fetching permissions for role {}: {}", 
                                           role.getId(), error.getMessage()));
                })
                .map(role -> {
                    RolesResponse response = RolesMapper.toResponse(role);
                    logger.debug("✅ Mapped role with permissions: {}", role.getName());

                    return response;
                })
                .doOnComplete(() -> 
                    logger.info("✅ Successfully completed findAllRolesWithPermissions, active: {}", active))
                .doOnError(error -> 
                    logger.error("❌ Error in findAllRolesWithPermissions with active {}: {}", 
                               active, error.getMessage(), error));
    }

    // ==============================
    // OVERRIDE ADDITIONAL METHODS FOR BETTER LOGGING
    // ==============================
    
    @Override
    public Mono<RolesResponse> findById(UUID id) {
        logger.info("🔍 Finding role by ID: {}", id);
        return super.findById(id)
                .doOnSuccess(response -> 
                    logger.info("✅ Successfully found role by ID: {}", id))
                .doOnError(error -> 
                    logger.error("❌ Error finding role by ID {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<RolesResponse> save(RolesRequest request) {
        logger.info("💾 Saving new role: '{}'", request != null ? request.getName() : "null");
        return super.save(request)
                .doOnSuccess(response -> 
                    logger.info("✅ Successfully saved new role: '{}' with ID: {}", 
                               response.getName(), response.getId()))
                .doOnError(error -> 
                    logger.error("❌ Error saving role '{}': {}", 
                               request != null ? request.getName() : "null", error.getMessage()));
    }

    @Override
    public Mono<RolesResponse> update(UUID id, RolesRequest request) {
        logger.info("🔄 Updating role ID: {} with name: '{}'", 
                   id, request != null ? request.getName() : "null");
        request.setId(id);
        return super.update(id,request)
                .doOnSuccess(response -> 
                    logger.info("✅ Successfully updated role: '{}' with ID: {}", 
                               response.getName(), response.getId()))
                .doOnError(error -> 
                    logger.error("❌ Error updating role ID {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<RolesResponse> delete(UUID id) {
        logger.info("🗑️ Deleting role with ID: {}", id);
        return super.delete(id)
                .doOnSuccess(response -> 
                    logger.info("✅ Successfully deleted role: '{}' with ID: {}", 
                               response.getName(), response.getId()))
                .doOnError(error -> 
                    logger.error("❌ Error deleting role ID {}: {}", id, error.getMessage()));
    }

    @Override
    public Mono<RolesResponse> changeState(UUID id) {
        logger.info("🔄 Changing state for role with ID: {}", id);
        return super.changeState(id)
                .doOnSuccess(response -> 
                    logger.info("✅ Successfully changed state for role: '{}' with ID: {}", 
                               response.getName(), response.getId()))
                .doOnError(error -> 
                    logger.error("❌ Error changing state for role ID {}: {}", id, error.getMessage()));
    }

    @Override
    public Flux<RolesResponse> findAllActive(Boolean active,EnumFilter type) {
        logger.info("🔍 Finding all roles with active status: {}", active);
        return super.findAllActive(active,type)
                .doOnComplete(() -> 
                    logger.info("✅ Successfully found all roles with active status: {}", active))
                .doOnError(error -> 
                    logger.error("❌ Error finding roles with active status {}: {}", 
                               active, error.getMessage()));
    }

	@Override
	protected String getFileField(Roles entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setFileField(Roles entity, String filePath) {
		// TODO Auto-generated method stub
		
	}
}