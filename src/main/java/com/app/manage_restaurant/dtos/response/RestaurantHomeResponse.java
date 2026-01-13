package com.app.manage_restaurant.dtos.response;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RestaurantHomeResponse {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String description;
    private Integer capacity;
    private String currency;
    private String logo;
    private String coverImage;
    private Boolean active;
    private UUID createdBy;
    private long createdDate;
    private long modifiedDate;
    private UUID modifiedBy;
    private Integer version;
    private String cuisine;
    // ✅ Liste des horaires d’ouverture
    private Set<OpeningHourResponse> openingHours;
    private Set<MenuCategoryResponse> categories = new HashSet<MenuCategoryResponse>();
    private Set<MenusResponse> menus= new HashSet<MenusResponse>();

    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    
	public Set<MenuCategoryResponse> getCategories() {
		return categories;
	}
	public void setCategories(Set<MenuCategoryResponse> categories) {
		this.categories = categories;
	}
	public Set<MenusResponse> getMenus() {
		return menus;
	}
	public void setMenus(Set<MenusResponse> menus) {
		this.menus = menus;
	}
	public Set<OpeningHourResponse> getOpeningHours() {
		return openingHours;
	}
	public void setOpeningHours(Set<OpeningHourResponse> openingHours) {
		this.openingHours = openingHours;
	}
	public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }
    
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }
    
    public long getCreatedDate() { return createdDate; }
    public void setCreatedDate(long createdDate) { this.createdDate = createdDate; }
    
    public long getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(long modifiedDate) { this.modifiedDate = modifiedDate; }
    
    public UUID getModifiedBy() { return modifiedBy; }
    public void setModifiedBy(UUID modifiedBy) { this.modifiedBy = modifiedBy; }
    
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
	public String getCuisine() {
		return cuisine;
	}
	public void setCuisine(String cuisine) {
		this.cuisine = cuisine;
	}
	@Override
	public String toString() {
		return "RestaurantHomeResponse [id=" + id + ", name=" + name + ", email=" + email + ", phone=" + phone
				+ ", address=" + address + ", city=" + city + ", description=" + description + ", capacity=" + capacity
				+ ", currency=" + currency + ", logo=" + logo + ", coverImage=" + coverImage + ", active=" + active
				+ ", createdBy=" + createdBy + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate
				+ ", modifiedBy=" + modifiedBy + ", version=" + version + ", cuisine=" + cuisine + ", openingHours="
				+ openingHours + ", categories=" + categories + ", menus=" + menus + "]";
	}
    
}
