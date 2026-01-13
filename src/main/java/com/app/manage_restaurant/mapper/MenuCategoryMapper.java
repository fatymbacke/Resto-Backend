package com.app.manage_restaurant.mapper;

import com.app.manage_restaurant.dtos.request.MenuCategoryRequest;
import com.app.manage_restaurant.dtos.response.MenuCategoryResponse;
import com.app.manage_restaurant.entities.MenuCategory;

public class MenuCategoryMapper {
    
    // ==========================
    // CREATE NEW ENTITY
    // ==========================
    public static MenuCategory toEntity(MenuCategoryRequest request) {
        if (request == null) { return null; }        
        MenuCategory menuCategory = new MenuCategory();
        menuCategory.setName(request.getName());
        menuCategory.setDescription(request.getDescription());
        menuCategory.setOrder(request.getOrder());
        menuCategory.setId(request.getId());
        menuCategory.setRestoCode(request.getRestoCode());
        return menuCategory;
    }
    
    // ==========================
    // UPDATE EXISTING ENTITY (SAFE)
    // ==========================
    public static void updateEntityFromRequest(MenuCategoryRequest request, MenuCategory entity) {
        if (request == null || entity == null) return;
        if (isValidString(request.getName())) entity.setName(request.getName().trim());
        if (isValidString(request.getDescription())) entity.setDescription(request.getDescription().trim());
        if (request.getOrder() != null && request.getOrder() >= 0) entity.setOrder(request.getOrder());
    }

    // ==========================
    // VALIDATION
    // ==========================
    private static boolean isValidString(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    // ==========================
    // TO RESPONSE
    // ==========================
    public static MenuCategoryResponse toResponse(MenuCategory menuCategory) {
        if (menuCategory == null) {
            return null;
        }
        
        MenuCategoryResponse response = new MenuCategoryResponse();
        response.setId(menuCategory.getId());
        response.setName(menuCategory.getName());
        response.setDescription(menuCategory.getDescription());
        response.setOrder(menuCategory.getOrder());
        response.setActive(menuCategory.isActive());
        response.setCreatedBy(menuCategory.getCreatedBy());
        response.setCreatedDate(menuCategory.getCreatedDate());
        response.setModifiedBy(menuCategory.getModifiedBy());
        response.setModifiedDate(menuCategory.getModifiedDate());
        response.setRestoCode(menuCategory.getRestoCode());

        return response;
    }
}