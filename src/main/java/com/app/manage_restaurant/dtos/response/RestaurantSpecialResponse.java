package com.app.manage_restaurant.dtos.response;

import java.util.UUID;

public class RestaurantSpecialResponse {
    private UUID id;
    private String name;
   

    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    
}
