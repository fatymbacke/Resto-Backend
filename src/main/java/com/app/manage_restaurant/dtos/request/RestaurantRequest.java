package com.app.manage_restaurant.dtos.request;

import java.util.Set;
import java.util.UUID;

import org.springframework.http.codec.multipart.FilePart;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class RestaurantRequest {
	private UUID id;

    @NotBlank(message = "Le nom du restaurant est obligatoire")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @NotBlank(message = "Le téléphone est obligatoire")
    private String phone;

    @NotBlank(message = "L'adresse est obligatoire")
    private String address;

    @NotBlank(message = "La ville est obligatoire")
    private String city;
    private String cuisine;

    private String description;

    @NotNull @Positive
    private Integer capacity;

    @NotBlank @Size(min = 3, max = 3)
    private String currency;

    private Boolean active = true;

    // fichiers multipart WebFlux
    private FilePart logoFile;
    private FilePart coverImageFile;

    // URLs des fichiers stockés
    private String logo;
    private String coverImage;
 // ✅ Liste des horaires d’ouverture
    private Set<OpeningHourRequest> openingHours;
    public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	
	public String getCuisine() {
		return cuisine;
	}
	public void setCuisine(String cuisine) {
		this.cuisine = cuisine;
	}
	// Getters et Setters
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
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public FilePart getLogoFile() { return logoFile; }
    public void setLogoFile(FilePart logoFile) { this.logoFile = logoFile; }
    public FilePart getCoverImageFile() { return coverImageFile; }
    public void setCoverImageFile(FilePart coverImageFile) { this.coverImageFile = coverImageFile; }
    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
	public Set<OpeningHourRequest> getOpeningHours() {
		return openingHours;
	}
	public void setOpeningHours(Set<OpeningHourRequest> openingHours) {
		this.openingHours = openingHours;
	}
    
}
