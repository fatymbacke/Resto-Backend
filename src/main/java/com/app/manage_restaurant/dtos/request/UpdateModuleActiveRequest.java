package com.app.manage_restaurant.dtos.request;

import jakarta.validation.constraints.NotNull;

public class UpdateModuleActiveRequest {
    
    @NotNull(message = "L'active est obligatoire")
    private boolean active;
    
    // Vous pouvez ajouter d'autres champs si nécessaire, comme :
    // - reason (pour les annulations)
    // - deliveryPersonId (pour l'assignation d'un livreur)
    // - notes
    
    public UpdateModuleActiveRequest() {
    }
    
    public UpdateModuleActiveRequest(boolean active) {
        this.active = active;
    }

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
    
    
}