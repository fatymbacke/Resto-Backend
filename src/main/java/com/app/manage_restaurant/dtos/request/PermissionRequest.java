package com.app.manage_restaurant.dtos.request;

import java.util.UUID;

public class PermissionRequest {
    private UUID id;
    private String name;
    private String code;
    private String description;
    private UUID ModuleId;
    private boolean active;
    

    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
   
    
	public UUID getModuleId() {
		return ModuleId;
	}
	public void setModuleId(UUID moduleId) {
		ModuleId = moduleId;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	
   
}