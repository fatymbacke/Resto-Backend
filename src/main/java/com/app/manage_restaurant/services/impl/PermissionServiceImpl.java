package com.app.manage_restaurant.services.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;

import com.app.manage_restaurant.cores.BaseServiceImpl;
import com.app.manage_restaurant.dtos.request.ChangePermissionsRequest;
import com.app.manage_restaurant.dtos.request.PermissionRequest;
import com.app.manage_restaurant.dtos.response.PermissionHomeResponse;
import com.app.manage_restaurant.dtos.response.PermissionResponse;
import com.app.manage_restaurant.entities.Permission;
import com.app.manage_restaurant.exceptions.entities.EntityNotFoundException;
import com.app.manage_restaurant.mapper.PermissionMapper;
import com.app.manage_restaurant.repositories.ModuleRepository;
import com.app.manage_restaurant.repositories.PermissionRepository;
import com.app.manage_restaurant.services.GenericDuplicateChecker;
import com.app.manage_restaurant.services.PermissionService;
import com.app.manage_restaurant.utilities.FileStorageUtil;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PermissionServiceImpl extends BaseServiceImpl<Permission, PermissionRequest, PermissionResponse, UUID> implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final ModuleRepository moduleRepository;
    private final Logger logger;
    protected final R2dbcEntityTemplate template; // 🔥 Pour la recherche dynamique

    public PermissionServiceImpl(PermissionRepository repository,
    		                 R2dbcEntityTemplate template,
        		             FileStorageUtil fileStorageUtil,
                             ModuleRepository moduleRepository,
                             ReactiveExceptionHandler exceptionHandler,
                             GenericDuplicateChecker duplicateChecker) {
        super(repository,fileStorageUtil,template, PermissionMapper::toEntity, PermissionMapper::toResponse,
              Permission.class, "Permission", exceptionHandler, duplicateChecker);
        this.permissionRepository = repository;
        this.moduleRepository = moduleRepository;
        this.template = template;
        this.logger = LoggerFactory.getLogger(PermissionService.class);
    }   

    // ==============================
    // VALIDATION DES DONNÉES
    // ==============================
    
    
    @Override
    public Mono<Void> validate(PermissionRequest request) {
        return Mono.fromRunnable(() -> {
        	if (request.getName() == null || request.getName().trim().isEmpty())
                throw new IllegalArgumentException("Le nom de la permission est obligatoire");
            if (request.getCode() == null || request.getCode().trim().isEmpty())
                throw new IllegalArgumentException("Le code de la permission est obligatoire");
            if (request.getModuleId() == null)
                throw new IllegalArgumentException("Le module est obligatoire");
        });
    }


    // ==============================
    // CHAMPS UNIQUES
    // ==============================
    @Override
	public Map<String, Object> extractUniqueFields(PermissionRequest request) {
        return Map.of(
            "name", request.getName(),
            "code", request.getCode()
        );
    }

    @Override
    public Mono<Integer> changeState(ChangePermissionsRequest dto) {
        return Flux.fromIterable(dto.getPermissions())
                .filter(p -> p.getId() != null)
                .collect(Collectors.toMap(
                    Permission::getId,
                    Permission::isActive
                ))
                .flatMap(activeMap -> {
                    if (activeMap.isEmpty()) {
                        return Mono.error(new IllegalArgumentException("No valid permissions provided"));
                    }
                    
                    return repository.findAllById(activeMap.keySet())
                            .switchIfEmpty(Mono.error(new EntityNotFoundException(
                                "No permissions found with the provided IDs")))
                            .map(permission -> {
                                Boolean newState = activeMap.get(permission.getId());
                                if (newState != null) {
                                    permission.setActive(newState);
                                }
                                return permission;
                            })
                            .collectList()
                            .flatMap(updatedPermissions -> 
                                repository.saveAll(updatedPermissions)
                                        .count()
                                        .map(Long::intValue)
                            );
                });
    }

    // ==============================
    // FIND ALL
    // ==============================
    @Override
    public Flux<PermissionResponse> findAll() {
        return permissionRepository.findAll()
                                   .map(PermissionMapper::toResponse);
    }

	@Override
	protected String getFileField(Permission entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setFileField(Permission entity, String filePath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Flux<PermissionHomeResponse> findPermissionsWithRoles(List<UUID> roleId) {
	    return permissionRepository.findPermissionsWithRoles(roleId)
	            .flatMap(permission -> {
	            	PermissionHomeResponse dto = PermissionMapper.toHomeResponse(permission);	                
	                if (permission.getModuleId() == null) {
	                    return Mono.just(dto); // Retour direct si pas de moduleId
	                }

	                return moduleRepository.findById(permission.getModuleId())
	                        .map(module -> {

	                            dto.setModule(module.getName());
	                            dto.setModuleActive(module.isActive());
	                            return dto;
	                        })
	                        .defaultIfEmpty(dto); // Retourne le DTO sans info module si non trouvé
	            });
	}

	@Override
	public Flux<PermissionHomeResponse> findPermissionsActive() {
	    return permissionRepository.findActivePermissions()
	            .flatMap(permission -> {
	                PermissionHomeResponse dto = PermissionMapper.toHomeResponse(permission);
	                
	                
	                if (permission.getModuleId() == null) {
	                    return Mono.just(dto); // Retour direct si pas de moduleId
	                }
	                
	                return moduleRepository.findById(permission.getModuleId())
	                        .map(module -> {
	                            dto.setModule(module.getName());
	                            dto.setModuleActive(module.isActive());
	                            return dto;
	                        })
	                        .defaultIfEmpty(dto); // Retourne le DTO sans info module si non trouvé
	            });
	}

}
