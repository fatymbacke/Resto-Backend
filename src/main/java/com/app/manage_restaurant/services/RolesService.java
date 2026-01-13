package com.app.manage_restaurant.services;

import java.util.List;
import java.util.UUID;

import com.app.manage_restaurant.cores.BaseService;
import com.app.manage_restaurant.dtos.request.RolesRequest;
import com.app.manage_restaurant.dtos.response.RolesResponse;
import com.app.manage_restaurant.entities.Roles;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RolesService extends BaseService<Roles, RolesRequest, RolesResponse, UUID> {
    
    public Mono<RolesResponse> updateRolePermissions(UUID roleId, List<UUID> permissionIds) ;      
    
    public Flux<RolesResponse> findAllRolesWithPermissions(boolean active);
}
