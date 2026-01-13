package com.app.manage_restaurant.dtos.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class AssignDeliveryOrder {
    
    @NotNull(message = "Le livreur est obligatoire")
    private UUID deliverInfo;
    
    
    
    
    public AssignDeliveryOrder() {
    }
    
    public AssignDeliveryOrder(UUID deliverInfo) {
        this.deliverInfo = deliverInfo;
    }

	public UUID getDeliverInfo() {
		return deliverInfo;
	}

	public void setDeliverInfo(UUID deliverInfo) {
		this.deliverInfo = deliverInfo;
	}
    
    
}
