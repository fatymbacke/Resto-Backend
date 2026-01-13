package com.app.manage_restaurant.dtos.request;
import java.util.UUID;

public class ReservationRequest {
         private CustomerInfo customerInfo;
	   	 private Integer capacity;
	   	 private long date;
	   	 private String time;	 
	   	 private String commentaire;     
         private UUID restoCode;      

		public UUID getRestoCode() {
			return restoCode;
		}
		public void setRestoCode(UUID restoCode) {
			this.restoCode = restoCode;
		}
		
    

    public CustomerInfo getCustomerInfo() {
			return customerInfo;
		}
		public void setCustomerInfo(CustomerInfo customerInfo) {
			this.customerInfo = customerInfo;
		}
		public Integer getCapacity() {
			return capacity;
		}
		public void setCapacity(Integer capacity) {
			this.capacity = capacity;
		}
		public long getDate() {
			return date;
		}
		public void setDate(long date) {
			this.date = date;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		
		public String getCommentaire() {
			return commentaire;
		}
		public void setCommentaire(String commentaire) {
			this.commentaire = commentaire;
		}




	public static class CustomerInfo {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String address;
        private String postalCode;
        private String city;
        private String deliveryInstructions;

        public CustomerInfo() {}

        public CustomerInfo(String firstName, String lastName, String email, String phone, 
                           String address, String postalCode, String city, String deliveryInstructions) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.phone = phone;
            this.address = address;
            this.postalCode = postalCode;
            this.city = city;
            this.deliveryInstructions = deliveryInstructions;
        }

        // Getters et Setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getDeliveryInstructions() { return deliveryInstructions; }
        public void setDeliveryInstructions(String deliveryInstructions) { this.deliveryInstructions = deliveryInstructions; }

        @Override
        public String toString() {
            return "CustomerInfo{" + "firstName='" + firstName + "', lastName='" + lastName + 
                   "', email='" + email + "', phone='" + phone + "', address='" + address + 
                   "', postalCode='" + postalCode + "', city='" + city + 
                   "', deliveryInstructions='" + deliveryInstructions + "'}";
        }
    }

    
}