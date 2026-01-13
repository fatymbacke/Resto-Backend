package com.app.manage_restaurant.dtos.request;

import java.util.UUID;

public abstract class PersonRequest  {
	protected UUID id;
	protected String firstname;
	protected String lastname;
	protected String email;
	protected String phone;
	protected String password;
	protected String role; 
	protected Boolean active = true;
	private UUID restoCode;
    private UUID roleId;
	private UUID ownerCode;
    
    
	public UUID getOwnerCode() {
		return ownerCode;
	}
	public void setOwnerCode(UUID ownerCode) {
		this.ownerCode = ownerCode;
	}
	public UUID getRoleId() {
		return roleId;
	}
	public void setRoleId(UUID roleId) {
		this.roleId = roleId;
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() {
        return this.role;
    }

    public void setRole(String enumRole) {
        this.role = enumRole;
    }
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public UUID getRestoCode() {
		return restoCode;
	}
	public void setRestoCode(UUID restoCode) {
		this.restoCode = restoCode;
	}
	
    
}
