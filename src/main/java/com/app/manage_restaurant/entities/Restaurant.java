package com.app.manage_restaurant.entities;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.app.manage_restaurant.cores.AuditableEntity;
@Table("restaurant")
public class Restaurant extends AuditableEntity<UUID>{
    
    @Column("name")
    private String name;
    
    @Column("email")
    private String email;
    
    @Column("phone")
    private String phone;
    
    @Column("address")
    private String address;
    
    @Column("city")
    private String city;
    
    @Column("description")
    private String description;
    
    @Column("capacity")
    private Integer capacity;
    
    @Column("currency")
    private String currency;
    
    @Column("logo")
    private String logo;
    
    @Column("cover_image")
    private String coverImage;  
    @Column("cuisine")
    private String cuisine;
    @Transient
    private Set<OpeningHour> openingHours = new HashSet<>();
    
    // Constructeurs
    public Restaurant() {}
    
    public Restaurant(String name, String email, String phone, String address, String city) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
    }
    

    public Restaurant(String name, String email, String phone, String address, String city, String description,
			Integer capacity, String currency, String logo, String coverImage, String cuisine) {
		super();
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.address = address;
		this.city = city;
		this.description = description;
		this.capacity = capacity;
		this.currency = currency;
		this.logo = logo;
		this.coverImage = coverImage;
		this.cuisine = cuisine;
	}

	// Getters et Setters
    
   
    public String getName() { return name; }
    public String getCuisine() {
		return cuisine;
	}

	public void setCuisine(String cuisine) {
		this.cuisine = cuisine;
	}

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
    
   
    
    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }
    
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }

	public Set<OpeningHour> getOpeningHours() {
		return openingHours;
	}

	public void setOpeningHours(Set<OpeningHour> openingHours) {
		this.openingHours = openingHours;
	}

	@Override
	public String toString() {
		return "Restaurant [name=" + name + ", email=" + email + ", phone=" + phone + ", address=" + address + ", city="
				+ city + ", description=" + description + ", capacity=" + capacity + ", currency=" + currency
				+ ", logo=" + logo + ", coverImage=" + coverImage + ", openingHours=" + openingHours + ", id=" + id
				+ ", active=" + active + ", createdBy=" + createdBy + ", createdDate=" + createdDate + ", modifiedBy="
				+ modifiedBy + ", modifiedDate=" + modifiedDate + ", restoCode=" + restoCode
				+ ", ownerCode=" + ownerCode + "]";
	}

	

	
    
   
}
