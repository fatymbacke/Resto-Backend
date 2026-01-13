package com.app.manage_restaurant.entities;

import java.util.UUID;

import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import com.app.manage_restaurant.cores.AuditableEntity;

@Table("tables")
public class Tables extends AuditableEntity<UUID> {
    
    @Column("name")
    private String name;
    
    @Column("capacity")
    private Integer capacity;
    
    @Column("position_x")
    private Integer positionX;
    
    @Column("position_y")
    private Integer positionY;
    
    @Column("shape")
    private String shape; // 'rectangle' | 'circle' | 'square'
    
    @Column("status")
    private String status; // 'available' | 'occupied' | 'reserved' | 'cleaning'
    
    @Transient
    private Restaurant restaurant;

    // Constructeurs
    public Tables() {}

    public Tables(String name, Integer capacity, Integer positionX, Integer positionY, 
                 String shape, String status) {
        this.name = name;
        this.capacity = capacity;
        this.positionX = positionX;
        this.positionY = positionY;
        this.shape = shape;
        this.status = status;
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
    
    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }

    // Méthodes utilitaires pour les enum-like properties
    public enum TableShape {
        RECTANGLE("rectangle"),
        CIRCLE("circle"),
        SQUARE("square");

        private final String value;

        TableShape(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static TableShape fromValue(String value) {
            for (TableShape shape : TableShape.values()) {
                if (shape.value.equals(value)) {
                    return shape;
                }
            }
            throw new IllegalArgumentException("Unknown shape: " + value);
        }
    }

    public enum TableStatus {
        AVAILABLE("available"),
        OCCUPIED("occupied"),
        RESERVED("reserved"),
        CLEANING("cleaning");

        private final String value;

        TableStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static TableStatus fromValue(String value) {
            for (TableStatus status : TableStatus.values()) {
                if (status.value.equals(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown status: " + value);
        }
    }

    // Méthodes pratiques pour travailler avec les enums
    public TableShape getShapeAsEnum() {
        return shape != null ? TableShape.fromValue(shape) : null;
    }

    public void setShapeFromEnum(TableShape shape) {
        this.shape = shape != null ? shape.getValue() : null;
    }

    public TableStatus getStatusAsEnum() {
        return status != null ? TableStatus.fromValue(status) : null;
    }

    public void setStatusFromEnum(TableStatus status) {
        this.status = status != null ? status.getValue() : null;
    }

    @Override
    public String toString() {
        return "Table{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", positionX=" + positionX +
                ", positionY=" + positionY +
                ", shape='" + shape + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}