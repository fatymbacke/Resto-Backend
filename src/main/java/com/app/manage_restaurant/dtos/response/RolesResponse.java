package com.app.manage_restaurant.dtos.response;
import java.util.Set;
import java.util.UUID;

public class RolesResponse {
    private UUID id;
    private String name;
    private String description;
    private Boolean isDefault;
    private Integer userCount;
    private Integer permissionsCount;
    private Set<PermissionResponse> permissions;
    private boolean active;
    private UUID createdBy;
    private long createdDate;
    private long modifiedDate;
    private UUID modifiedBy;
    private Integer version;
    private UUID roleId;

    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
    
    public Integer getUserCount() { return userCount; }
    public void setUserCount(Integer userCount) { this.userCount = userCount; }
    
    public Integer getPermissionsCount() { return permissionsCount; }
    public void setPermissionsCount(Integer permissionsCount) { this.permissionsCount = permissionsCount; }
    
    public Set<PermissionResponse> getPermissions() { return permissions; }
    public void setPermissions(Set<PermissionResponse> permissions) { this.permissions = permissions; }
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public UUID getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(UUID createdBy) {
		this.createdBy = createdBy;
	}
	public long getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
	}
	public long getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(long modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public UUID getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(UUID modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public UUID getRoleId() {
		return roleId;
	}
	public void setRoleId(UUID roleId) {
		this.roleId = roleId;
	}
	
    
   
}