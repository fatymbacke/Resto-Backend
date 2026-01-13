package com.app.manage_restaurant.dtos.request;

import com.app.manage_restaurant.entities.EnumReservation;

import jakarta.validation.constraints.NotNull;

public class UpdateReservationStatusRequest {
    
    @NotNull(message = "Le statut est obligatoire")
    private EnumReservation status;
    
    // Vous pouvez ajouter d'autres champs si nécessaire, comme :
    // - reason (pour les annulations)
    // - deliveryPersonId (pour l'assignation d'un livreur)
    // - notes
    
    public UpdateReservationStatusRequest() {
    }
    
    public UpdateReservationStatusRequest(EnumReservation status) {
        this.status = status;
    }
    
    public EnumReservation getStatus() {
        return status;
    }
    
    public void setStatus(EnumReservation status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "UpdateOrderStatusRequest{" +
                "status=" + status +
                '}';
    }
}