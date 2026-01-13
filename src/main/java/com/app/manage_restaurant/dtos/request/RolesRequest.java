package com.app.manage_restaurant.dtos.request;
import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RolesRequest {
    
	private UUID id;
    @NotBlank(message = "Le nom du rôle est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String name;
    
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;
    
    private Boolean isDefault = false;
    
    private Boolean active = true;
	private UUID restoCode;

	private Set<UUID> permissionIds;

    
	// Getters et Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
    
    public Set<UUID> getPermissionIds() { return permissionIds; }
    public void setPermissionIds(Set<UUID> permissionIds) { this.permissionIds = permissionIds; }
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	
	public UUID getRestoCode() {
		return restoCode;
	}
	public void setRestoCode(UUID restoCode) {
		this.restoCode = restoCode;
	}
	
	@Override
	public String toString() {
		return "RolesRequest [id=" + id + ", name=" + name + ", description=" + description + ", isDefault=" + isDefault
				+ ", active=" + active + ", permissionIds=" + permissionIds + "]";
	}
    
    
}