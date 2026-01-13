package com.app.manage_restaurant.dtos.response;
import java.util.Set;
import java.util.UUID;

public class ModuleResponse {
    private UUID id;
    private String name;
    private String description;
    private Set<PermissionResponse> permissions;
    private boolean active;
    private UUID createdBy;
    private long createdDate;
    private long modifiedDate;
    private UUID modifiedBy;
    private Integer version;
    private Long permissionsCount;

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
	public Long getPermissionsCount() {
		return permissionsCount;
	}
	public void setPermissionsCount(Long permissionsCount) {
		this.permissionsCount = permissionsCount;
	}
	@Override
	public String toString() {
		return "ModuleResponse [id=" + id + ", name=" + name + ", description=" + description + ", permissions="
				+ permissions + ", active=" + active + ", createdBy=" + createdBy + ", createdDate=" + createdDate
				+ ", modifiedDate=" + modifiedDate + ", modifiedBy=" + modifiedBy + ", version=" + version
				+ ", permissionsCount=" + permissionsCount + "]";
	}
    
    
}