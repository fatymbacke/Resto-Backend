package com.app.manage_restaurant.dtos.response;

import java.util.UUID;

import com.app.manage_restaurant.entities.EnumReservation;

public class reservationResponse {

    public static class ReservationResponse {
        private UUID id;
        private String reservationNumber;
        private CustomerInfoResponse customerInfo;
        private EnumReservation status;
        private long date;        
	   	private Integer capacity;
	   	private String time;	 
	   	private String commentaire;  
        
        public ReservationResponse(UUID id, String reservationNumber, CustomerInfoResponse customerInfo,
				EnumReservation status, long date, Integer capacity, String time, 
				String commentaire) {
			super();
			this.id = id;
			this.reservationNumber = reservationNumber;
			this.customerInfo = customerInfo;
			this.status = status;
			this.date = date;
			this.capacity = capacity;
			this.time = time;
			this.commentaire = commentaire;
		}
		public ReservationResponse() {
			super();
		}
		// Getters et Setters
		public UUID getId() {
			return id;
		}
		public void setId(UUID id) {
			this.id = id;
		}
		public String getReservationNumber() {
			return reservationNumber;
		}
		public void setReservationNumber(String reservationNumber) {
			this.reservationNumber = reservationNumber;
		}
		public CustomerInfoResponse getCustomerInfo() {
			return customerInfo;
		}
		public void setCustomerInfo(CustomerInfoResponse customerInfo) {
			this.customerInfo = customerInfo;
		}
		public EnumReservation getStatus() {
			return status;
		}
		public void setStatus(EnumReservation status) {
			this.status = status;
		}
		public long getDate() {
			return date;
		}
		public void setDate(long date) {
			this.date = date;
		}
		public Integer getCapacity() {
			return capacity;
		}
		public void setCapacity(Integer capacity) {
			this.capacity = capacity;
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
		@Override
		public String toString() {
			return "ReservationResponse [id=" + id + ", reservationNumber=" + reservationNumber + ", customerInfo="
					+ customerInfo + ", status=" + status + ", date=" + date + ", capacity=" + capacity + ", time="
					+ time + ", commentaire=" + commentaire + "]";
		}
        
    }

    public static class CustomerInfoResponse {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;

        public CustomerInfoResponse() {}

        

        public CustomerInfoResponse(String firstName, String lastName, String email, String phone) {
			super();
			this.firstName = firstName;
			this.lastName = lastName;
			this.email = email;
			this.phone = phone;
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

        @Override
        public String toString() {
            return "CustomerInfoResponse{" + "firstName='" + firstName + "', lastName='" + lastName + 
                   "', email='" + email + "', phone='" + phone + "'}";
        }
    }


}