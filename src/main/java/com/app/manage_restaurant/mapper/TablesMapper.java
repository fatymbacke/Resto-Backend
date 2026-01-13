package com.app.manage_restaurant.mapper;

import java.util.List;

import com.app.manage_restaurant.dtos.request.TablesRequest;
import com.app.manage_restaurant.dtos.response.TablesResponse;
import com.app.manage_restaurant.entities.Tables;

public class TablesMapper {
    
    public static Tables toEntity(TablesRequest request) {
        Tables Tables = new Tables();
        Tables.setId(request.getId());
        Tables.setName(request.getName());
        Tables.setCapacity(request.getCapacity());
        Tables.setPositionX(request.getPositionX());
        Tables.setPositionY(request.getPositionY());
        Tables.setShape(request.getShape());
        Tables.setStatus(request.getStatus());
        Tables.setRestoCode(request.getRestoCode());
        Tables.setActive(request.getActive() != null ? request.getActive() : true);
        return Tables;
    }
    
    public static TablesResponse toResponse(Tables entity) {
        TablesResponse response = new TablesResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setCapacity(entity.getCapacity());
        response.setPosition(new TablesResponse.Position(entity.getPositionX(), entity.getPositionY()));
        response.setShape(entity.getShape());
        response.setStatus(entity.getStatus());
        response.setActive(entity.isActive());
        response.setCreatedDate(entity.getCreatedDate());
        response.setModifiedDate(entity.getModifiedDate());
        response.setRestoCode(entity.getRestoCode());
        return response;
    }
    
    public static void updateEntityFromRequest(Tables entity, TablesRequest request) {
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getCapacity() != null) {
            entity.setCapacity(request.getCapacity());
        }
        if (request.getPositionX() != null) {
            entity.setPositionX(request.getPositionX());
        }
        if (request.getPositionY() != null) {
            entity.setPositionY(request.getPositionY());
        }
        if (request.getShape() != null) {
            entity.setShape(request.getShape());
        }
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }
        if (request.getActive()  != null) {
            entity.setActive(request.getActive());
        }
       
    }
    /**
     * Convertit un flux d'entités en flux de DTOs Response (pour WebFlux)
     */
    public static List<TablesResponse> toResponses(List<Tables> entities) {
        return entities.stream().map(tab->toResponse(tab)).toList();
    }
}