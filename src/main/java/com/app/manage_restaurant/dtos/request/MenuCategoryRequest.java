package com.app.manage_restaurant.dtos.request;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MenuCategoryRequest {
	 private UUID id;
 @NotBlank(message = "Le nom de la catégorie est obligatoire")
 @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
 private String name;

 @Size(max = 200, message = "La description ne peut pas dépasser 200 caractères")
 private String description;

 private Integer order;
	private UUID restoCode;

	
 // Getters et Setters
 public String getName() {
     return name;
 }

 public void setName(String name) {
     this.name = name;
 }
 

 public UUID getRestoCode() {
	return restoCode;
}

public void setRestoCode(UUID restoCode) {
	this.restoCode = restoCode;
}

public String getDescription() {
     return description;
 }

 public void setDescription(String description) {
     this.description = description;
 }

 public Integer getOrder() {
     return order;
 }

 public void setOrder(Integer order) {
     this.order = order;
 }

public UUID getId() {
	return id;
}

public void setId(UUID id) {
	this.id = id;
}
 
 
}