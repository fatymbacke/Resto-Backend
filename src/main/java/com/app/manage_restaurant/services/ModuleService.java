package com.app.manage_restaurant.services;

import java.util.List;
import java.util.UUID;

import com.app.manage_restaurant.cores.BaseService;
import com.app.manage_restaurant.dtos.request.ModuleRequest;
import com.app.manage_restaurant.dtos.response.ModuleResponse;
import com.app.manage_restaurant.entities.Module;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ModuleService extends BaseService<Module, ModuleRequest, ModuleResponse, UUID> {  
   
    // ==============================
    // FIND ALL AVEC PERMISSIONS
    // ==============================
    public Flux<ModuleResponse> findAllWithPermissions();

    // ==============================
    // MISE À JOUR DES PERMISSIONS
    // ==============================
    public Mono<ModuleResponse> updateModulePermissions(UUID moduleId, List<UUID> permissionIds);

	public Mono<ModuleResponse> updateActive(UUID id, boolean active);

	
}
