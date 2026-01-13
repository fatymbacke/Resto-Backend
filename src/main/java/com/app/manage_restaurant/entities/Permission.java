package com.app.manage_restaurant.entities;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.app.manage_restaurant.cores.AuditableEntity;

@Table("permissions")
public class Permission extends AuditableEntity<UUID>{
    
    
    @Column("name")
    private String name;
    
    @Column("code")
    private String code;
    
    @Column("description")
    private String description;
    
    @Column("module_id")
    private UUID moduleId;
    
    @Transient
    private Module module;    
    @Transient
    private Set<Roles> roles = new HashSet<>();    
    

    // Constructeurs
    public Permission() {}
    
    public Permission(String name, String code, String description) {
        this.name = name;
        this.code = code;
        this.description = description;
    }
    
    public Permission(String name, String code, String description, UUID moduleId) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.moduleId = moduleId;
    }

    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public UUID getModuleId() { return moduleId; }
    public void setModuleId(UUID moduleId) { this.moduleId = moduleId; }
    
    public Module getModule() { return module; }
    public void setModule(Module module) { this.module = module; }
    
    public Set<Roles> getRoles() { return roles; }
    public void setRoles(Set<Roles> roles) { this.roles = roles; }
    
    
}