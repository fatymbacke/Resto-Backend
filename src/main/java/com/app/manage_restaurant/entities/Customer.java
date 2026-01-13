package com.app.manage_restaurant.entities;

import java.util.Collection;
import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;

@Table("customer")
public class Customer extends Person{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	  
	  
	    @Column("address")
	    private String address;
	    
	    @Column("city")
	    private String city;
	    @Transient
	    private List<Order> orders;
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}
	

	public List<Order> getOrders() {
		return orders;
	}


	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}


	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}

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

	

	
}
