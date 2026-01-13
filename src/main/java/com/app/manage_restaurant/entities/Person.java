package com.app.manage_restaurant.entities;
import java.util.UUID;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.security.core.userdetails.UserDetails;

import com.app.manage_restaurant.cores.AuditableEntity;
public  abstract class Person extends AuditableEntity<UUID> implements UserDetails {	
	private String firstname;
	private String lastname;
	private String password;
	private String email;
	@Column("phone")
	private String phone;
    private String role ;
    @Column("role_id")
    private UUID roleId;
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
		
	public String getRole() {
        return this.role;
    }

    public void setRole(String enumRole) {
        this.role =enumRole;
    }
    
	
	public UUID getRoleId() {
		return roleId;
	}
	public void setRoleId(UUID roleId) {
		this.roleId = roleId;
	}
	@Override
	public String toString() {
		return "Person [firstname=" + firstname + ", lastname=" + lastname + ", password=" + password + ", email="
				+ email + ", phone=" + phone + ", role=" + role + ", roleId=" + roleId + "]";
	}
	
	
	
	
   

}
