package com.app.manage_restaurant.mapper;

import com.app.manage_restaurant.dtos.request.PermissionRequest;
import com.app.manage_restaurant.dtos.response.PermissionHomeResponse;
import com.app.manage_restaurant.dtos.response.PermissionResponse;
import com.app.manage_restaurant.entities.Permission;

public class PermissionMapper {
    
    public static Permission toEntity(PermissionRequest request) {
        if (request == null) {
            return null;
        }
        
        Permission permission = new Permission();
        permission.setName(request.getName());
        permission.setCode(request.getCode());
        permission.setDescription(request.getDescription());
        permission.setModuleId(request.getModuleId());
        return permission;
    }
    
    public static PermissionResponse toResponse(Permission entity) {
        if (entity == null) {
            return null;
        }
        
        PermissionResponse response = new PermissionResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setCode(entity.getCode());
        response.setDescription(entity.getDescription());
        response.setModifiedBy(entity.getModifiedBy());
        response.setModifiedDate(entity.getModifiedDate());
        response.setActive(entity.isActive());
        // Mapper le module si présent
        if (entity.getModule() != null) {
            response.setModule(ModuleMapper.toResponse(entity.getModule()));
        }
        
        return response;
    }
    
    public static PermissionHomeResponse toHomeResponse(Permission entity) {
        if (entity == null) {
            return null;
        }
        
        PermissionHomeResponse response = new PermissionHomeResponse();
        response.setName(entity.getName());
        response.setCode(entity.getCode());
        response.setActive(entity.isActive());
        // Mapper le module si présent
        
        
        return response;
    }
    
    public static void updateEntityFromRequest(PermissionRequest request, Permission entity) {
        if (request == null || entity == null) {
            return;
        }
        
        entity.setName(request.getName());
        entity.setCode(request.getCode());
        entity.setDescription(request.getDescription());
        
        if (request.getModuleId() != null) {
            entity.setModuleId(request.getModuleId());
        }
    }
}