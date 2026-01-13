package com.app.manage_restaurant.mapper;

import com.app.manage_restaurant.dtos.request.PrsnlRequest;
import com.app.manage_restaurant.dtos.response.PrsnlResponse;
import com.app.manage_restaurant.entities.Prsnl;

public class PrsnlMapper {

    // ==========================
    // CONVERSION: DTO -> ENTITY
    // ==========================
    public static Prsnl toEntity(PrsnlRequest dto) {
        if (dto == null) return null;
        Prsnl entity = new Prsnl();
        entity.setId(dto.getId());
        entity.setFirstname(dto.getFirstname());
        entity.setLastname(dto.getLastname());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setPhone(dto.getPhone());
        entity.setRole(dto.getRole());
        entity.setActive(dto.getActive());
        entity.setRestoCode(dto.getRestoCode());
        entity.setOwnerCode(dto.getOwnerCode());    
        entity.setRoleId(dto.getRoleId());
        return entity;
    }

    public static Prsnl toEntity(PrsnlResponse dto) {
        if (dto == null) return null;
        Prsnl entity = new Prsnl();
        entity.setId(dto.getId());
        entity.setFirstname(dto.getFirstname());
        entity.setLastname(dto.getLastname());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setPhone(dto.getPhone());
        entity.setRole(dto.getRole());
        entity.setRestoCode(dto.getRestoCode());
        entity.setOwnerCode(dto.getOwnerCode());    
        entity.setActive(dto.isActive());
        entity.setRoleId(dto.getRoleId());
        return entity;
    }

    // ==========================
    // CONVERSION: ENTITY -> RESPONSE
    // ==========================
    public static PrsnlResponse toResponse(Prsnl entity) {
        if (entity == null) return null;

        PrsnlResponse response = new PrsnlResponse();
        response.setId(entity.getId());
        response.setFirstname(entity.getFirstname());
        response.setLastname(entity.getLastname());
        response.setEmail(entity.getEmail());
        response.setPassword(entity.getPassword());
        response.setPhone(entity.getPhone());
        response.setRole(entity.getRole());
        response.setRestoCode(entity.getRestoCode());
        response.setActive(entity.isActive());
        response.setCreatedBy(entity.getCreatedBy());
        response.setCreatedDate(entity.getCreatedDate());
        response.setModifiedBy(entity.getModifiedBy());
        response.setModifiedDate(entity.getModifiedDate());
        response.setRoleId(entity.getRoleId());

        return response;
    }
    

    // ==========================
    // UPDATE EXISTING ENTITY (SAFE)
    // ==========================
    public static void updateEntityFromRequest(PrsnlRequest request, Prsnl entity) {
    	   if (request == null || entity == null) return;

        if (isValidString(request.getFirstname())) {
            entity.setFirstname(request.getFirstname().trim());
        }

        if (isValidString(request.getLastname())) {
            entity.setLastname(request.getLastname().trim());
        }

        if (isValidEmail(request.getEmail())) {
            entity.setEmail(request.getEmail().trim());
        }

        if (isValidString(request.getPassword())) {
            entity.setPassword(request.getPassword().trim());
        }

        if (isValidPhone(request.getPhone())) {
            entity.setPhone(request.getPhone().trim());
        }
        if (request.getRoleId() !=null) {
            entity.setRoleId(request.getRoleId());
        }

        if (isValidString(request.getRole())) {
            entity.setRole(request.getRole().trim());
        }        

        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }

    }

    // ==========================
    // VALIDATION UTILS
    // ==========================
    private static boolean isValidString(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static boolean isValidEmail(String email) {
        return isValidString(email) && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    private static boolean isValidPhone(String phone) {
        return isValidString(phone) && phone.matches("^[0-9+()\\-\\s]{6,20}$");
    }
}
