package com.app.manage_restaurant.dtos.request;

import java.util.Set;
import java.util.UUID;

import com.app.manage_restaurant.dtos.response.PermissionResponse;

public class ModuleRequest {
    private UUID id;
    private String name;
    private String description;
    private Set<PermissionResponse> permissions;
    private boolean active;
    

    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Set<PermissionResponse> getPermissions() { return permissions; }
    public void setPermissions(Set<PermissionResponse> permissions) { this.permissions = permissions; }
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	
}