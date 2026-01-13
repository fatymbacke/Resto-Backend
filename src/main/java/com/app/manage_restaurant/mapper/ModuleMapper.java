package com.app.manage_restaurant.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.app.manage_restaurant.dtos.request.ModuleRequest;
import com.app.manage_restaurant.dtos.response.ModuleResponse;
import com.app.manage_restaurant.dtos.response.PermissionResponse;
import com.app.manage_restaurant.entities.Module;
import com.app.manage_restaurant.entities.Permission;

public class ModuleMapper {
    
    public static Module toEntity(ModuleRequest request) {
        if (request == null) {
            return null;
        }
        
        Module module = new Module();
        module.setName(request.getName());
        module.setDescription(request.getDescription());
        return module;
    }
    
    public static ModuleResponse toResponse(Module entity) {
        if (entity == null) {
            return null;
        }      
        
        ModuleResponse response = new ModuleResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setModifiedBy(entity.getModifiedBy());
        response.setModifiedDate(entity.getModifiedDate());
        response.setPermissionsCount((long) entity.getPermissions().size());
        response.setActive(entity.isActive());
        return response;
    }
    
    public static Set<PermissionResponse> permissionsToResponses(Set<Permission> permissions) {
        if (permissions == null) {
            return null;
        }
        
        return permissions.stream()
                .map(PermissionMapper::toResponse)
                .collect(Collectors.toSet());
    }
    public static Set<PermissionResponse> permissionsToResponses(List<Permission> permissions) {
        if (permissions == null) {
            return null;
        }
        
        return permissions.stream()
                .map(PermissionMapper::toResponse)
                .collect(Collectors.toSet());
    }
    
    
    public static void updateEntityFromRequest(ModuleRequest request, Module entity) {
        if (request == null || entity == null) {
            return;
        }
        
        entity.setName(request.getName());
        entity.setActive(request.isActive());

        entity.setDescription(request.getDescription());
    }
}