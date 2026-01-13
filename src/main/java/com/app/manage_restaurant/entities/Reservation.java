package com.app.manage_restaurant.entities;

import java.util.UUID;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.app.manage_restaurant.cores.AuditableEntity;
@Table("reservation")
public class Reservation extends AuditableEntity<UUID>{
	 @Column("customer_id")
	 private UUID customerId;	    
	 @Column("reservation_number")
	 private String reservationNumber;
	 @Column("capacity")
	 private Integer capacity;
	 @Column("reservation_date")
	 private long date;
	 @Column("status")
	 private EnumReservation status;	 
	 @Column("firstname")
	 private String firstName;	    
	 @Column("lastname")
	 private String lastName;		 
	 @Column("customer_phone")
	 private String phone;	    
	 @Column("customer_email")
	 private String email;
	 @Column("reservation_time")
	 private String time;	 
	 @Column("commentaire")
	 private String commentaire;
	     

	 
	
	public UUID getCustomerId() {
		return customerId;
	}
	public void setCustomerId(UUID customerId) {
		this.customerId = customerId;
	}
	public String getReservationNumber() {
		return reservationNumber;
	}
	public void setReservationNumber(String reservationNumber) {
		this.reservationNumber = reservationNumber;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	public Integer getCapacity() {
		return capacity;
	}
	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}
	
	public String getCommentaire() {
		return commentaire;
	}
	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}
	public EnumReservation getStatus() {
		return status;
	}
	public void setStatus(EnumReservation status) {
		this.status = status;
	}
	 
	 
   
}
