package com.app.manage_restaurant.entities;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.app.manage_restaurant.cores.HasOwnerAndResto;

//Entité pour les tags
@Table("menu_tags")
public class MenuTag   implements HasOwnerAndResto<UUID>{
	 @Id
	 private UUID id;
 @Column("menu_id")
 private UUID menuId;
 private boolean active = true;

 private String tag;
 @Column("resto_code")
 private UUID restoCode;
 
 @Column("owner_code")
 private UUID ownerCode;

public UUID getMenuId() {
	return menuId;
}

public void setMenuId(UUID menuId) {
	this.menuId = menuId;
}

public String getTag() {
	return tag;
}

public void setTag(String tag) {
	this.tag = tag;
}

public UUID getId() {
	return id;
}

public void setId(UUID id) {
	this.id = id;
}

public UUID getRestoCode() {
	return restoCode;
}

public void setRestoCode(UUID restoCode) {
	this.restoCode = restoCode;
}

public UUID getOwnerCode() {
	return ownerCode;
}

public void setOwnerCode(UUID ownerCode) {
	this.ownerCode = ownerCode;
}

public boolean isActive() {
	return active;
}

public void setActive(boolean active) {
	this.active = active;
}

 
 
}