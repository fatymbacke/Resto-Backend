package com.app.manage_restaurant.dtos.request;

import java.util.List;

import com.app.manage_restaurant.entities.Permission;

import jakarta.validation.constraints.NotNull;

public class ChangePermissionsRequest {
	 @NotNull(message = "Les permissions sont obligatoires") 
    private List<Permission> permissions;

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	
	
}
