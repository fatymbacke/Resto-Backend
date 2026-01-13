package com.app.manage_restaurant.mapper;

import com.app.manage_restaurant.dtos.request.CustomerRequest;
import com.app.manage_restaurant.dtos.response.CustomerResponse;
import com.app.manage_restaurant.entities.Customer;

public class CustomerMapper {

    // ==========================
    // CONVERSION: DTO -> ENTITY
    // ==========================
    public static Customer toEntity(CustomerRequest dto) {
        if (dto == null) return null;
        Customer entity = new Customer();
        entity.setId(dto.getId());
        entity.setFirstname(dto.getFirstname());
        entity.setLastname(dto.getLastname());
        entity.setCity(dto.getCity());
        entity.setAddress(dto.getAddress());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setPhone(dto.getPhone());
        entity.setRole(dto.getRole());
        entity.setActive(dto.getActive());
        entity.setRestoCode(dto.getRestoCode());
        entity.setRoleId(dto.getRoleId());
        return entity;
    }

    public static Customer toEntity(CustomerResponse dto) {
        if (dto == null) return null;
        Customer entity = new Customer();
        entity.setId(dto.getId());
        entity.setFirstname(dto.getFirstname());
        entity.setLastname(dto.getLastname());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setPhone(dto.getPhone());
        entity.setRole(dto.getRole());
        entity.setRestoCode(dto.getRestoCode());
        entity.setActive(dto.isActive());
        entity.setRoleId(dto.getRoleId());
        entity.setCity(dto.getCity());
        entity.setAddress(dto.getAddress());
        return entity;
    }

    // ==========================
    // CONVERSION: ENTITY -> RESPONSE
    // ==========================
    public static CustomerResponse toResponse(Customer entity) {
        if (entity == null) return null;

        CustomerResponse response = new CustomerResponse();
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
        response.setCity(entity.getCity());
        response.setAddress(entity.getAddress());
        return response;
    }

    // ==========================
    // UPDATE EXISTING ENTITY (SAFE)
    // ==========================
    public static void updateEntityFromRequest(CustomerRequest request, Customer entity) {
    	   if (request == null || entity == null) return;

        if (isValidString(request.getFirstname())) {
            entity.setFirstname(request.getFirstname().trim());
        }

        if (isValidString(request.getLastname())) {
            entity.setLastname(request.getLastname().trim());
        }
        if (isValidString(request.getCity())) {
            entity.setCity(request.getCity().trim());
        }
        if (isValidString(request.getAddress())) {
            entity.setAddress(request.getAddress().trim());
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
    public static void updateEntity(Customer request, Customer entity) {
 	   if (request == null || entity == null) return;

     if (isValidString(request.getFirstname())) {
         entity.setFirstname(request.getFirstname().trim());
     }

     if (isValidString(request.getLastname())) {
         entity.setLastname(request.getLastname().trim());
     }
     if (isValidString(request.getCity())) {
         entity.setCity(request.getCity().trim());
     }
     if (isValidString(request.getAddress())) {
         entity.setAddress(request.getAddress().trim());
     }

     if (isValidEmail(request.getEmail())) {
         entity.setEmail(request.getEmail().trim());
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
