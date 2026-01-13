package com.app.manage_restaurant.dtos.response;
import java.util.Set;
import java.util.UUID;

public class MenusResponse {
 private UUID id;
 private String name;
 private String description;
 private Double price;
 private String category;
 private String imageUrl;
 private Set<String> ingredients;
 private Boolean isAvailable;
 private Boolean isVegetarian;
 private Boolean isVegan;
 private Boolean isGlutenFree;
 private Integer calories;
 private Integer preparationTime;
 private Set<String> tags;
 private boolean active;
 private UUID createdBy;
 private long createdDate;
 private long modifiedDate;
 private UUID modifiedBy;
 private Integer version;
	private UUID restoCode;
	private UUID ownerCode;
	

public UUID getOwnerCode() {
		return ownerCode;
	}
	public void setOwnerCode(UUID ownerCode) {
		this.ownerCode = ownerCode;
	}
public UUID getRestoCode() {
		return restoCode;
	}
	public void setRestoCode(UUID restoCode) {
		this.restoCode = restoCode;
	}
public UUID getId() {
	return id;
}
public void setId(UUID id) {
	this.id = id;
}
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
public boolean isActive() {
	return active;
}
public void setActive(boolean active) {
	this.active = active;
}
public UUID getCreatedBy() {
	return createdBy;
}
public void setCreatedBy(UUID createdBy) {
	this.createdBy = createdBy;
}
public long getCreatedDate() {
	return createdDate;
}
public void setCreatedDate(long createdDate) {
	this.createdDate = createdDate;
}
public long getModifiedDate() {
	return modifiedDate;
}
public void setModifiedDate(long modifiedDate) {
	this.modifiedDate = modifiedDate;
}
public UUID getModifiedBy() {
	return modifiedBy;
}
public void setModifiedBy(UUID modifiedBy) {
	this.modifiedBy = modifiedBy;
}
public Integer getVersion() {
	return version;
}
public void setVersion(Integer version) {
	this.version = version;
}

 
 
 
}