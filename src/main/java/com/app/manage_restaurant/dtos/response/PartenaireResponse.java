package com.app.manage_restaurant.dtos.response;

public class PartenaireResponse {
	private String phone;    
    private boolean active;
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public PartenaireResponse(String phone, boolean active) {
		super();
		this.phone = phone;
		this.active = active;
	}
    
	
}
