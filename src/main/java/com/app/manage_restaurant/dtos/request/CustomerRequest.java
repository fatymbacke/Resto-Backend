package com.app.manage_restaurant.dtos.request;

public class CustomerRequest  extends PersonRequest{

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
			return "CustomerRequest [address=" + address + ", city=" + city + ", id=" + id + ", firstname=" + firstname
					+ ", lastname=" + lastname + ", email=" + email + ", phone=" + phone + ", password=" + password
					+ ", role=" + role + ", active=" + active + "]";
		}
	
	
	

	
}
