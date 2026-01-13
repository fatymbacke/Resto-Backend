package com.app.manage_restaurant.dtos.request;
import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class MenusRequest {
 private UUID id;

 @NotBlank(message = "Le nom est obligatoire")
 @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
 private String name;

 @NotBlank(message = "La description est obligatoire")
 @Size(min = 10, max = 500, message = "La description doit contenir entre 10 et 500 caractères")
 private String description;

 @NotNull(message = "Le prix est obligatoire")
 @Positive(message = "Le prix doit être positif")
 private Double price;

 @NotBlank(message = "La catégorie est obligatoire")
 private String category;

 private String imageUrl;

 @NotNull(message = "Les ingrédients sont obligatoires")
 private Set<String> ingredients;
 
 @NotNull(message = "Les tags sont obligatoires") 
 private Set<String> tags;
 private boolean isAvailable ;
 private Boolean isVegetarian = false;

 private Boolean isVegan = false;

 private Boolean isGlutenFree = false;

 @PositiveOrZero(message = "Les calories doivent être positives ou zéro")
 private Integer calories;

 @Positive(message = "Le temps de préparation doit être positif")
 private Integer preparationTime;
 private boolean active=true;
	private UUID restoCode;


 public boolean isActive() {
	return active;
}

public void setActive(boolean active) {
	this.active = active;
}


public UUID getRestoCode() {
	return restoCode;
}

public void setRestoCode(UUID restoCode) {
	this.restoCode = restoCode;
}

// Getters et Setters
 public String getName() {
     return name;
 }

 public void setName(String name) {
     this.name = name;
 }

 public String getDescription() {
     return description;
 }

 public void setDescription(String description) {
     this.description = description;
 }

 public Double getPrice() {
     return price;
 }

 public void setPrice(Double price) {
     this.price = price;
 }

 public String getCategory() {
     return category;
 }

 public void setCategory(String category) {
     this.category = category;
 }

 public String getImageUrl() {
     return imageUrl;
 }

 public void setImageUrl(String imageUrl) {
     this.imageUrl = imageUrl;
 }

 public Set<String> getIngredients() {
     return ingredients;
 }

 public void setIngredients(Set<String> ingredients) {
     this.ingredients = ingredients;
 }

 public Boolean getIsAvailable() {
     return isAvailable;
 }

 public void setIsAvailable(Boolean isAvailable) {
     this.isAvailable = isAvailable;
 }

 public Boolean getIsVegetarian() {
     return isVegetarian;
 }

 public void setIsVegetarian(Boolean isVegetarian) {
     this.isVegetarian = isVegetarian;
 }

 public Boolean getIsVegan() {
     return isVegan;
 }

 public void setIsVegan(Boolean isVegan) {
     this.isVegan = isVegan;
 }

 public Boolean getIsGlutenFree() {
     return isGlutenFree;
 }

 public void setIsGlutenFree(Boolean isGlutenFree) {
     this.isGlutenFree = isGlutenFree;
 }

 public Integer getCalories() {
     return calories;
 }

 public void setCalories(Integer calories) {
     this.calories = calories;
 }

 public Integer getPreparationTime() {
     return preparationTime;
 }

 public void setPreparationTime(Integer preparationTime) {
     this.preparationTime = preparationTime;
 }

 public Set<String> getTags() {
     return tags;
 }

 public void setTags(Set<String> tags) {
     this.tags = tags;
 }

public UUID getId() {
	return id;
}

public void setId(UUID id) {
	this.id = id;
}

@Override
public String toString() {
	return "MenusRequest [id=" + id + ", name=" + name + ", description=" + description + ", price=" + price
			+ ", category=" + category + ", imageUrl=" + imageUrl + ", ingredients=" + ingredients + ", tags=" + tags
			+ ", isAvailable=" + isAvailable + ", isVegetarian=" + isVegetarian + ", isVegan=" + isVegan
			+ ", isGlutenFree=" + isGlutenFree + ", calories=" + calories + ", preparationTime=" + preparationTime
			+ ", active=" + active + ", restoCode=" + restoCode + "]";
}

 
}