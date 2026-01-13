package com.app.manage_restaurant.entities;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.app.manage_restaurant.cores.AuditableEntity;

@Table("roles")
public class Roles extends AuditableEntity<UUID>{
    
  
    @Column("name")
    private String name;
    
    @Column("description")
    private String description;
    
    @Column("is_default")
    private Boolean isDefault = false;
    
    @Transient
    private Set<Permission> permissions = new HashSet<>();
    
    @Transient
    private Set<Prsnl> users = new HashSet<>();
  
    // Constructeurs
    public Roles() {}
    
    public Roles(String name, String description, UUID restoCode) {
        this.name = name;
        this.description = description;
        if(restoCode != null) 
        this.setRestoCode(restoCode); // Définition directe

    }
    public Roles(String name, String description) {
        this.name = name;
        this.description = description;
        if(restoCode != null) ;

    }

    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
    
    public Set<Permission> getPermissions() { return permissions; }
    public void setPermissions(Set<Permission> permissions) { this.permissions = permissions; }
    
    public Set<Prsnl> getUsers() { return users; }
    public void setUsers(Set<Prsnl> users) { this.users = users; }
    
    
    // Méthodes utilitaires
    public Integer getUserCount() {
        return this.users != null ? this.users.size() : 0;
    }
    
    public Integer getPermissionsCount() {
        return this.permissions != null ? this.permissions.size() : 0;
    }
}