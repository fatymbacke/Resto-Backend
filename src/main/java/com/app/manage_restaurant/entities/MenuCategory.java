package com.app.manage_restaurant.entities;

import java.util.UUID;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.app.manage_restaurant.cores.AuditableEntity;

@Table("menucategory")
public class MenuCategory extends AuditableEntity<UUID>{
	@Column("name")
    private String name;
    private String description;
    private Integer orderNo;
    private Long menuCount;

    // Constructeurs
    public MenuCategory() {}

    public MenuCategory(String name, String description, Integer orderNo) {
        this.name = name;
        this.description = description;
        this.orderNo = orderNo;
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

    public Integer getOrder() {
        return orderNo;
    }

    public void setOrder(Integer order) {
        this.orderNo = order;
    }

    public Long getMenuCount() {
        return menuCount;
    }

    public void setMenuCount(Long menuCount) {
        this.menuCount = menuCount;
    }

    @Override
    public String toString() {
        return "MenuCategory{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", order=" + orderNo +
                ", menuCount=" + menuCount +
                '}';
    }
}
