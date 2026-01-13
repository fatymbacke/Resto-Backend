package com.app.manage_restaurant.services.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;

import com.app.manage_restaurant.cores.BaseServiceImpl;
import com.app.manage_restaurant.cores.EnumFilter;
import com.app.manage_restaurant.dtos.request.ModuleRequest;
import com.app.manage_restaurant.dtos.response.ModuleResponse;
import com.app.manage_restaurant.entities.Module;
import com.app.manage_restaurant.entities.Permission;
import com.app.manage_restaurant.exceptions.entities.EntityNotFoundException;
import com.app.manage_restaurant.mapper.ModuleMapper;
import com.app.manage_restaurant.repositories.ModuleRepository;
import com.app.manage_restaurant.repositories.PermissionRepository;
import com.app.manage_restaurant.services.GenericDuplicateChecker;
import com.app.manage_restaurant.services.ModuleService;
import com.app.manage_restaurant.utilities.FileStorageUtil;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ModuleServiceImpl extends BaseServiceImpl<Module, ModuleRequest, ModuleResponse, UUID> implements ModuleService {

    private final ModuleRepository repository;
    private final PermissionRepository permissionRepository;
    private final Logger logger;
    protected final R2dbcEntityTemplate template; // 🔥 Pour la recherche dynamique

    public ModuleServiceImpl(ModuleRepository repository,
    		             R2dbcEntityTemplate template,
    		             FileStorageUtil fileStorageUtil,
                         PermissionRepository permissionRepository,
                         ReactiveExceptionHandler exceptionHandler,
                         GenericDuplicateChecker duplicateChecker) {
        super(repository,fileStorageUtil,template, ModuleMapper::toEntity, ModuleMapper::toResponse,
              Module.class, "Module", exceptionHandler, duplicateChecker);
        this.repository = repository;
        this.permissionRepository = permissionRepository;
        this.template = template;
        this.logger = LoggerFactory.getLogger(ModuleService.class);
    }

    // ==============================
    // SUPPRESSION AVEC CONTRAINTE PERMISSIONS
    // ==============================
    @Override
    public Mono<ModuleResponse> delete(UUID id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Module", id)))
                .flatMap(module -> permissionRepository.countByModuleId(module.getId())
                        .flatMap(count -> {
                            if (count > 0) {
                                return Mono.error(new RuntimeException(
                                        "Impossible de supprimer le module car il contient des permissions. Supprimez d'abord les permissions associées."));
                            }
                            return repository.delete(module).thenReturn(module);
                        }))
                .map(ModuleMapper::toResponse);
    }

    // ==============================
    // VALIDATION
    // ==============================   
    @Override
    public Mono<Void> validate(ModuleRequest request) {
        return Mono.fromRunnable(() -> {
        	if (request.getName() == null || request.getName().trim().isEmpty())
                throw new IllegalArgumentException("Le nom du module est obligatoire");
            if (request.getName().length() < 2 || request.getName().length() > 100)
                throw new IllegalArgumentException("Le nom doit contenir entre 2 et 100 caractères");
            if (request.getDescription() != null && request.getDescription().length() > 500)
                throw new IllegalArgumentException("La description ne peut pas dépasser 500 caractères");;
        });
    }


    // ==============================
    // CHAMPS UNIQUES
    // ==============================
    @Override
	public Map<String, Object> extractUniqueFields(ModuleRequest request) {
        return Map.of(
            "name", request.getName()
        );
    }

    // ==============================
    // FIND ALL AVEC PERMISSIONS
    // ==============================
    @Override
    public Flux<ModuleResponse> findAllWithPermissions() {
    	

        return repository.findAll(EnumFilter.NOTHING)
                .flatMap(module -> permissionRepository.findByModuleId(module.getId())
                        .collectList()
                        .map(perms -> {
                            ModuleResponse response = ModuleMapper.toResponse(module);
                            response.setPermissions(ModuleMapper.permissionsToResponses(perms));
                            response.setPermissionsCount((long) response.getPermissions().size());
                            return response;
                        }));
    }

    // ==============================
    // MISE À JOUR DES PERMISSIONS
    // ==============================
    @Override
    public Mono<ModuleResponse> updateModulePermissions(UUID moduleId, List<UUID> permissionIds) {
        return repository.findById(moduleId)
            .switchIfEmpty(Mono.error(new EntityNotFoundException("Module", moduleId)))
            .flatMap(module -> permissionRepository.findAllById(permissionIds)
                    .collectList()
                    .flatMap(perms -> {
                        module.setPermissions((Set<Permission>) perms);
                        return repository.save(module);
                    }))
            .map(ModuleMapper::toResponse);
    }

	@Override
	protected String getFileField(Module entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setFileField(Module entity, String filePath) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public Mono<ModuleResponse> changeState(UUID id) {
	    return repository.findById(id)
	            .switchIfEmpty(Mono.error(new EntityNotFoundException("Module non trouvé avec l'id: " + id)))
	            .flatMap(module -> {
	               
	                boolean newState = !module.isActive();
	                module.setActive(newState);	   

	                return repository.save(module)
	                        .flatMap(savedModule -> {
	                            // Si on désactive le module, désactiver aussi toutes ses permissions
	                            if (!newState) {
	                                return permissionRepository.findByModuleId(id)
	                                        .flatMap(permission -> {
	                        	                boolean permissionState = !permission.isActive();
	                                            permission.setActive(permissionState);
	                                            return permissionRepository.save(permission);
	                                        })
	                                        .then(Mono.just(savedModule));
	                            }
	                            return Mono.just(savedModule);
	                        })
	                        .flatMap(savedModule -> permissionRepository.findByModuleId(id)
	                                .collectList()
	                                .map(permissions -> {
	                                    Set<Permission> permissionSet = new HashSet<>(permissions);
	                                    savedModule.setPermissions(permissionSet);	                                    
	                                    ModuleResponse response = ModuleMapper.toResponse(savedModule);
	                                    response.setPermissionsCount((long) permissions.size());
	                                    return response;
	                                }));
	            });
	}

	@Override
	public Mono<ModuleResponse> updateActive(UUID id, boolean active) {
	    return repository.findById(id)
	            .switchIfEmpty(Mono.error(new EntityNotFoundException("Module non trouvé avec l'id: " + id)))
	            .flatMap(module -> {
	               module.setActive(active);  

	                return repository.save(module)
	                        .flatMap(savedModule -> {
	                            // Si on désactive le module, désactiver aussi toutes ses permissions
	                                return permissionRepository.findByModuleId(id)
	                                        .flatMap(permission -> {
	                        	                boolean permissionState = !permission.isActive();
	                                            permission.setActive(permissionState);
	                                            return permissionRepository.save(permission);
	                                        })
	                                        .then(Mono.just(savedModule));
	                            
	                        })
	                        .flatMap(savedModule -> permissionRepository.findByModuleId(id)
	                                .collectList()
	                                .map(permissions -> {
	                                    Set<Permission> permissionSet = new HashSet<>(permissions);
	                                    savedModule.setPermissions(permissionSet);	                                    
	                                    ModuleResponse response = ModuleMapper.toResponse(savedModule);
	                                    response.setPermissionsCount((long) permissions.size());
	                                    return response;
	                                }));
	            });
	}
	
}
