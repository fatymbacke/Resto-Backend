package com.app.manage_restaurant.entities;

import java.util.Collection;

import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;

@Table("prsnl")
public class Prsnl extends Person{
	

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	public Prsnl() {
		super();
	}

	

	
}
