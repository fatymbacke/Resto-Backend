package com.app.manage_restaurant.mapper;

import com.app.manage_restaurant.dtos.request.MenusRequest;
import com.app.manage_restaurant.dtos.response.MenusResponse;
import com.app.manage_restaurant.entities.Menus;

public class MenuMapper {
    
    public static Menus toEntity(MenusRequest request) {
        if (request == null) {
            return null;
        }	        
        Menus menu = new Menus();
        menu.setName(request.getName());
        menu.setActive(request.isActive());
        menu.setDescription(request.getDescription());
        menu.setPrice(request.getPrice());
        menu.setCategory(request.getCategory());
        menu.setImageUrl(request.getImageUrl());
        menu.setIngredients(request.getIngredients());
        menu.setIsAvailable(request.getIsAvailable());
        menu.setIsVegetarian(request.getIsVegetarian());
        menu.setIsVegan(request.getIsVegan());
        menu.setIsGlutenFree(request.getIsGlutenFree());
        menu.setCalories(request.getCalories());
        menu.setPreparationTime(request.getPreparationTime());
        menu.setTags(request.getTags());
        menu.setRestoCode(request.getRestoCode());
        return menu;
    }
    
    // ==========================
    // RESPONSE TO ENTITY
    // ==========================
    public static Menus toEntity(MenusResponse response) {
        if (response == null) {
            return null;
        }
        
        Menus menu = new Menus();
        menu.setId(response.getId());
        menu.setName(response.getName());
        menu.setDescription(response.getDescription());
        menu.setPrice(response.getPrice());
        menu.setCategory(response.getCategory());
        menu.setImageUrl(response.getImageUrl());
        menu.setIngredients(response.getIngredients());
        menu.setIsAvailable(response.getIsAvailable());
        menu.setIsVegetarian(response.getIsVegetarian());
        menu.setIsVegan(response.getIsVegan());
        menu.setIsGlutenFree(response.getIsGlutenFree());
        menu.setCalories(response.getCalories());
        menu.setPreparationTime(response.getPreparationTime());
        menu.setTags(response.getTags());
        menu.setActive(response.isActive());
        menu.setCreatedBy(response.getCreatedBy());
        menu.setCreatedDate(response.getCreatedDate());
        menu.setModifiedBy(response.getModifiedBy());
        menu.setModifiedDate(response.getModifiedDate());
        menu.setRestoCode(response.getRestoCode());

        return menu;
    }
    
    // ==========================
    // UPDATE EXISTING ENTITY (SAFE)
    // ==========================
    public static void updateEntityFromRequest(MenusRequest request, Menus entity) {
        if (request == null || entity == null) return;

        if (isValidString(request.getName())) entity.setName(request.getName().trim());
        if (isValidString(request.getDescription())) entity.setDescription(request.getDescription().trim());
        if (request.getPrice() != null && request.getPrice().doubleValue() > 0) entity.setPrice(request.getPrice());
        if (isValidString(request.getCategory())) entity.setCategory(request.getCategory().trim());
        if (isValidString(request.getImageUrl())) entity.setImageUrl(request.getImageUrl().trim());
        if (request.getIngredients() != null) entity.setIngredients(request.getIngredients());
        if (request.getIsAvailable() != null) entity.setIsAvailable(request.getIsAvailable());
        if (request.getIsVegetarian() != null) entity.setIsVegetarian(request.getIsVegetarian());
        if (request.getIsVegan() != null) entity.setIsVegan(request.getIsVegan());
        if (request.getIsGlutenFree() != null) entity.setIsGlutenFree(request.getIsGlutenFree());
        if (request.getCalories() != null && request.getCalories() > 0) entity.setCalories(request.getCalories());
        if (request.getPreparationTime() != null && request.getPreparationTime() > 0) entity.setPreparationTime(request.getPreparationTime());
        if (request.getTags() != null) entity.setTags(request.getTags());
    }

    // ==========================
    // UPDATE ENTITY FROM RESPONSE
    // ==========================
    public static void updateEntityFromResponse(MenusResponse response, Menus entity) {
        if (response == null || entity == null) return;

        if (isValidString(response.getName())) entity.setName(response.getName().trim());
        if (isValidString(response.getDescription())) entity.setDescription(response.getDescription().trim());
        if (response.getPrice() != null && response.getPrice().doubleValue() > 0) entity.setPrice(response.getPrice());
        if (isValidString(response.getCategory())) entity.setCategory(response.getCategory().trim());
        if (isValidString(response.getImageUrl())) entity.setImageUrl(response.getImageUrl().trim());
        if (response.getIngredients() != null) entity.setIngredients(response.getIngredients());
        if (response.getIsAvailable() != null) entity.setIsAvailable(response.getIsAvailable());
        if (response.getIsVegetarian() != null) entity.setIsVegetarian(response.getIsVegetarian());
        if (response.getIsVegan() != null) entity.setIsVegan(response.getIsVegan());
        if (response.getIsGlutenFree() != null) entity.setIsGlutenFree(response.getIsGlutenFree());
        if (response.getCalories() != null && response.getCalories() > 0) entity.setCalories(response.getCalories());
        if (response.getPreparationTime() != null && response.getPreparationTime() > 0) entity.setPreparationTime(response.getPreparationTime());
        if (response.getTags() != null) entity.setTags(response.getTags());
        if (response.getCreatedBy() != null) entity.setCreatedBy(response.getCreatedBy());
        if (response.getModifiedBy() != null) entity.setModifiedBy(response.getModifiedBy());
    }

    // ==========================
    // VALIDATION
    // ==========================
    private static boolean isValidString(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    public static MenusResponse toResponse(Menus menu) {
        if (menu == null) {
            return null;
        }      
        
        MenusResponse response = new MenusResponse();
        response.setId(menu.getId());
        response.setName(menu.getName());
        response.setDescription(menu.getDescription());
        response.setPrice(menu.getPrice());
        response.setCategory(menu.getCategory());
        response.setImageUrl(menu.getImageUrl());
        response.setIngredients(menu.getIngredients());
        response.setIsAvailable(menu.getIsAvailable());
        response.setIsVegetarian(menu.getIsVegetarian());
        response.setIsVegan(menu.getIsVegan());
        response.setIsGlutenFree(menu.getIsGlutenFree());
        response.setCalories(menu.getCalories());
        response.setPreparationTime(menu.getPreparationTime());
        response.setTags(menu.getTags());        
        response.setActive(menu.isActive());
        response.setCreatedBy(menu.getCreatedBy());
        response.setCreatedDate(menu.getCreatedDate());
        response.setModifiedBy(menu.getModifiedBy());
        response.setModifiedDate(menu.getModifiedDate());
        response.setRestoCode(menu.getRestoCode());
        response.setOwnerCode(menu.getOwnerCode());

        return response;
    }
}