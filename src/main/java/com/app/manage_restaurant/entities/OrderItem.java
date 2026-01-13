package com.app.manage_restaurant.entities;
import java.util.UUID;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.app.manage_restaurant.cores.AuditableEntity;

@Table("order_items")
public class OrderItem  extends AuditableEntity<UUID>{
   
    @Column("order_id")
    private UUID orderId;
    
    @Column("menu_item_id")
    private String menuItemId;
    
    @Column("menu_item_name")
    private String menuItemName;
    
    @Column("menu_item_price")
    private Double menuItemPrice;
    
    @Column("quantity")
    private Integer quantity;
    
    @Column("category")
    private String category;
    
    
    @Column("special_instructions")
    private String specialInstructions;

    
    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
    
    public String getMenuItemId() { return menuItemId; }
    public void setMenuItemId(String menuItemId) { this.menuItemId = menuItemId; }
    
    public String getMenuItemName() { return menuItemName; }
    public void setMenuItemName(String menuItemName) { this.menuItemName = menuItemName; }
    
    public Double getMenuItemPrice() { return menuItemPrice; }
    public void setMenuItemPrice(Double menuItemPrice) { this.menuItemPrice = menuItemPrice; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
  
    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    // Méthode utilitaire pour calculer le prix total de l'item
    public Double getTotalPrice() {
        return menuItemPrice * quantity;
    }

    @Override
    public String toString() {
        return "OrderItem{" + "id=" + id + ", menuItemName='" + menuItemName + "', quantity=" + quantity + 
               ", totalPrice=" + getTotalPrice() + '}';
    }
}