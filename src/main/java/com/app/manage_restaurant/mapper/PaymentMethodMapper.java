package com.app.manage_restaurant.mapper;

import com.app.manage_restaurant.dtos.request.PaymentMethodRequest;
import com.app.manage_restaurant.dtos.response.PaymentMethodResponse;
import com.app.manage_restaurant.entities.PaymentMethod;


public class PaymentMethodMapper {
    
    public static PaymentMethod toEntity(PaymentMethodRequest request) {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(request.getId());
        paymentMethod.setName(request.getName());
        paymentMethod.setType(request.getType());
        paymentMethod.setIsEnabled(request.getIsEnabled() != null ? request.getIsEnabled() : true);
        paymentMethod.setProcessingFee(request.getProcessingFee() != null ? request.getProcessingFee() : 0.0);
        paymentMethod.setRequiresAuthentication(request.getRequiresAuthentication() != null ? request.getRequiresAuthentication() : false);
        paymentMethod.setDescription(request.getDescription());
        paymentMethod.setRestoCode(request.getRestoCode());
        paymentMethod.setOwnerCode(request.getOwnerCode());
        paymentMethod.setConfiguration(request.getConfiguration());
        paymentMethod.setActive(request.getActive() != null ? request.getActive() : true);
        return paymentMethod;
    }
    
    public static PaymentMethodResponse toResponse(PaymentMethod entity) {
        PaymentMethodResponse response = new PaymentMethodResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setType(entity.getType());
        response.setIsEnabled(entity.getIsEnabled());
        response.setProcessingFee(entity.getProcessingFee());
        response.setRequiresAuthentication(entity.getRequiresAuthentication());
        response.setDescription(entity.getDescription());
        response.setRestoCode(entity.getRestoCode());
        response.setOwnerCode(entity.getOwnerCode());
        response.setConfiguration(entity.getConfiguration());
        response.setActive(entity.isActive());
        response.setCreatedBy(entity.getCreatedBy());
        response.setCreatedDate(entity.getCreatedDate());
        response.setModifiedBy(entity.getModifiedBy());
        response.setModifiedDate(entity.getModifiedDate());
        return response;
    }
    
    public static void updateEntityFromRequest(PaymentMethodRequest request, PaymentMethod entity) {
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getType() != null) {
            entity.setType(request.getType());
        }
        if (request.getIsEnabled() != null) {
            entity.setIsEnabled(request.getIsEnabled());
        }
        if (request.getProcessingFee() != null) {
            entity.setProcessingFee(request.getProcessingFee());
        }
        if (request.getRequiresAuthentication() != null) {
            entity.setRequiresAuthentication(request.getRequiresAuthentication());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
        if (request.getConfiguration() != null) {
            entity.setConfiguration(request.getConfiguration());
        }
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
        if (request.getRestoCode() != null) {
            entity.setRestoCode(request.getRestoCode());
        }
        if (request.getOwnerCode() != null) {
            entity.setOwnerCode(request.getOwnerCode());
        }
    }
}