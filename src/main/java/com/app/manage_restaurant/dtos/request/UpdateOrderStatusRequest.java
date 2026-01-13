package com.app.manage_restaurant.dtos.request;

import com.app.manage_restaurant.entities.EnumOrder;

import jakarta.validation.constraints.NotNull;

public class UpdateOrderStatusRequest {
    
    @NotNull(message = "Le statut est obligatoire")
    private EnumOrder status;
    
    // Vous pouvez ajouter d'autres champs si nécessaire, comme :
    // - reason (pour les annulations)
    // - deliveryPersonId (pour l'assignation d'un livreur)
    // - notes
    
    public UpdateOrderStatusRequest() {
    }
    
    public UpdateOrderStatusRequest(EnumOrder status) {
        this.status = status;
    }
    
    public EnumOrder getStatus() {
        return status;
    }
    
    public void setStatus(EnumOrder status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "UpdateOrderStatusRequest{" +
                "status=" + status +
                '}';
    }
}