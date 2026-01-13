package com.app.manage_restaurant.services;

import java.util.List;
import java.util.UUID;

import com.app.manage_restaurant.cores.BaseService;
import com.app.manage_restaurant.dtos.request.ChangePermissionsRequest;
import com.app.manage_restaurant.dtos.request.PermissionRequest;
import com.app.manage_restaurant.dtos.response.PermissionHomeResponse;
import com.app.manage_restaurant.dtos.response.PermissionResponse;
import com.app.manage_restaurant.entities.Permission;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PermissionService extends BaseService<Permission, PermissionRequest, PermissionResponse, UUID> {
    // ==============================
    // CHANGE STATE
    // ==============================
    public Mono<Integer> changeState(ChangePermissionsRequest dto);
    
    public Flux<PermissionHomeResponse> findPermissionsWithRoles(List<UUID> roleId );

	public Flux<PermissionHomeResponse> findPermissionsActive();
    
}
