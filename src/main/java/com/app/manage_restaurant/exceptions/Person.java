package com.app.manage_restaurant.exceptions;
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
    private String identifiant;
    private int role = EnumRole.CUSTOMER.ordinal();
    
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
	public String getIdentifiant() {
		return identifiant;
	}
	public void setIdentifiant(String identifiant) {
		this.identifiant = identifiant;
	}
	
	
	public EnumRole getRole() {
        return EnumRole.values()[role];
    }

    public void setRole(EnumRole enumRole) {
        this.role = enumRole.ordinal();
    }
	
	
	@Override
	public String toString() {
		return "Person [firstname=" + firstname + ", lastname=" + lastname + ", password=" + password + ", email="
				+ email + ", phone=" + phone + ", restoCode=" + restoCode + ", identifiant=" + identifiant + ", role="
				+ role + ", id=" + id + ", active=" + active + ", createdBy=" + createdBy + ", createdDate="
				+ createdDate + ", modifiedBy=" + modifiedBy + ", modifiedDate=" + modifiedDate 
				+ "]";
	}
	
	
	
   

}
