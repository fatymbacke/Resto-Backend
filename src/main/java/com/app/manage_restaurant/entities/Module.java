package com.app.manage_restaurant.entities;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.app.manage_restaurant.cores.AuditableEntity;

@Table("modules")
public class Module extends AuditableEntity<UUID>{  
      
    @Column("name")
    private String name;    
    @Column("description")
    private String description;    
    @Transient
    private Set<Permission> permissions = new HashSet<>();
    @Transient
    @Column("permissions_count")
    private Long permissionsCount;
   
    // Constructeurs
    public Module() {}
    
    public Module(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Set<Permission> getPermissions() { return permissions; }
    public void setPermissions(Set<Permission> permissions) { this.permissions = permissions; }

	public Long getPermissionsCount() {
		return permissionsCount;
	}

	public void setPermissionsCount(Long permissionsCount) {
		this.permissionsCount = permissionsCount;
	}

   
}