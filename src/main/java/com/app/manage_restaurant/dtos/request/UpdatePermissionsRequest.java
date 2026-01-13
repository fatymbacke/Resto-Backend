package com.app.manage_restaurant.dtos.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class UpdatePermissionsRequest {
	 @NotNull(message = "Les permissions sont obligatoires") 
    private List<UUID> permissions;

	public List<UUID> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<UUID> permissions) {
		this.permissions = permissions;
	}

	
	
}
