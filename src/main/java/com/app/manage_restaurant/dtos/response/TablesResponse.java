package com.app.manage_restaurant.dtos.response;

import java.util.UUID;

public class TablesResponse {
    private UUID id;
    private String name;
    private Integer capacity;
    private Position position;
    private String shape;
    private String status;
    private UUID restaurantId;
    private boolean active;
    private UUID createdBy;
    private long createdDate;
    private long modifiedDate;
    private UUID modifiedBy;
    private Integer version;
	private UUID restoCode;

    // Record pour la position
    public record Position(Integer x, Integer y) {}
   
 // Constructeurs
    public TablesResponse() {}

    
    public TablesResponse(UUID id, String name, Integer capacity, Position position, String shape, String status,
		UUID restaurantId, boolean active, UUID createdBy, long createdDate, long modifiedDate, UUID modifiedBy,
		Integer version, UUID restoCode) {
	super();
	this.id = id;
	this.name = name;
	this.capacity = capacity;
	this.position = position;
	this.shape = shape;
	this.status = status;
	this.restaurantId = restaurantId;
	this.active = active;
	this.createdBy = createdBy;
	this.createdDate = createdDate;
	this.modifiedDate = modifiedDate;
	this.modifiedBy = modifiedBy;
	this.version = version;
	this.restoCode = restoCode;
}


	// Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    
    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }
    
    public String getShape() { return shape; }
    public void setShape(String shape) { this.shape = shape; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
	public UUID getRestaurantId() {
		return restaurantId;
	}
	public void setRestaurantId(UUID restaurantId) {
		this.restaurantId = restaurantId;
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

	public UUID getRestoCode() {
		return restoCode;
	}

	public void setRestoCode(UUID restoCode) {
		this.restoCode = restoCode;
	}
    
    
}