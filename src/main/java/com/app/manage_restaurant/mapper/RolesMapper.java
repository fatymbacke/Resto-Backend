package com.app.manage_restaurant.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.app.manage_restaurant.dtos.request.RolesRequest;
import com.app.manage_restaurant.dtos.response.RolesResponse;
import com.app.manage_restaurant.entities.Roles;

public class RolesMapper {
    
    public static Roles toEntity(RolesRequest request) {
        if (request == null) {
            return null;
        }
        
        Roles role = new Roles();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setIsDefault(request.getIsDefault());
        role.setActive(request.getActive());
        role.setRestoCode(request.getRestoCode());
        role.setId(request.getId());
        return role;
    }
    
    public static RolesResponse toResponse(Roles entity) {
        if (entity == null) {
            return null;
        }
        
        RolesResponse response = new RolesResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setIsDefault(entity.getIsDefault());
        response.setUserCount(entity.getUserCount());
        response.setPermissionsCount(entity.getPermissionsCount());
        response.setActive(entity.isActive());

        
        // Mapper les permissions
        if (entity.getPermissions() != null) {
            response.setPermissions(
                entity.getPermissions().stream()
                    .map(PermissionMapper::toResponse)
                    .collect(Collectors.toSet())
            );
        }

        return response;
    }
    
    public static Set<RolesResponse> toResponseSet(Set<Roles> entities) {
        if (entities == null) {
            return null;
        }
        
        return entities.stream()
                .map(RolesMapper::toResponse)
                .collect(Collectors.toSet());
    }
    
    public static void updateEntityFromRequest(RolesRequest request, Roles entity) {
        if (request == null || entity == null) {
            return;
        }
        entity.setActive(request.getActive());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setIsDefault(request.getIsDefault());
    }
}