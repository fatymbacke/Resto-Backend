package com.app.manage_restaurant.dtos.response;

public class CustomerResponse extends PersonResponse{
	private String address;
    
    private String city;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public String toString() {
		return "CustomerResponse [address=" + address + ", city=" + city + "]";
	}
	
	
}
