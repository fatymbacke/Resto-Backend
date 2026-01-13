package com.app.manage_restaurant.dtos.request;

import java.util.UUID;

public class TablesRequest {
    private UUID id;
    private String name;
    private Integer capacity;
    private Integer positionX;
    private Integer positionY;
    private String shape; // 'rectangle' | 'circle' | 'square'
    private String status; // 'available' | 'occupied' | 'reserved' | 'cleaning'
    private Boolean active;
	private UUID restoCode;

    // Constructeurs
    public TablesRequest() {}

   

    public TablesRequest(UUID id, String name, Integer capacity, Integer positionX, Integer positionY, String shape,
			String status, Boolean active, UUID restoCode) {
		super();
		this.id = id;
		this.name = name;
		this.capacity = capacity;
		this.positionX = positionX;
		this.positionY = positionY;
		this.shape = shape;
		this.status = status;
		this.active = active;
		this.restoCode = restoCode;
	}



	// Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    
    public Integer getPositionX() { return positionX; }
    public void setPositionX(Integer positionX) { this.positionX = positionX; }
    
    public Integer getPositionY() { return positionY; }
    public void setPositionY(Integer positionY) { this.positionY = positionY; }
    
    public String getShape() { return shape; }
    public void setShape(String shape) { this.shape = shape; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    

	public UUID getRestoCode() {
		return restoCode;
	}

	public void setRestoCode(UUID restoCode) {
		this.restoCode = restoCode;
	}

	@Override
	public String toString() {
		return "TablesRequest [id=" + id + ", name=" + name + ", capacity=" + capacity + ", positionX=" + positionX
				+ ", positionY=" + positionY + ", shape=" + shape + ", status=" + status + ", active=" + active
				+ ", restoCode=" + restoCode + "]";
	}

    
}