package com.app.manage_restaurant.dtos.request;

public class PrsnlRequest  extends PersonRequest{
     
	
	@Override
	public String toString() {
		return "PrsnlRequest [id=" + id + ", firstname=" + firstname + ", lastname=" + lastname + ", email=" + email
				+ ", phone=" + phone + ", password=" + password + ", role=" + role + ", active=" + active + ", getId()="
				+ getId() + ", getFirstname()=" + getFirstname() + ", getLastname()=" + getLastname() + ", getEmail()="
				+ getEmail() + ", getPhone()=" + getPhone() + ", getPassword()=" + getPassword() + ", getRole()="
				+ getRole() + ", getActive()=" + getActive() + ", getRestoCode()=" + getRestoCode() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
	

	
	

	
}
