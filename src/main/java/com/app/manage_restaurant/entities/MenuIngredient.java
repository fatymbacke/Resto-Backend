package com.app.manage_restaurant.entities;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.app.manage_restaurant.cores.HasOwnerAndResto;

@Table("menu_ingredients")
public class MenuIngredient   implements HasOwnerAndResto<UUID> {
	 @Id
	 private UUID id;
	    private boolean active = true;

    @Column("menu_id")
    private UUID menuId;
    
    private String ingredient;
    
    @Column("order_no")
    private Integer orderNo;
    @Column("resto_code")
    private UUID restoCode;
    
    @Column("owner_code")
    private UUID ownerCode;
    // Constructeurs
    public MenuIngredient() {}
    
    public MenuIngredient(UUID menuId, String ingredient, Integer orderNo) {
        this.menuId = menuId;
        this.ingredient = ingredient;
        this.orderNo = orderNo;
    }
    
    
    public UUID getMenuId() {
        return menuId;
    }
    
    public void setMenuId(UUID menuId) {
        this.menuId = menuId;
    }
    
    public String getIngredient() {
        return ingredient;
    }
    
    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }
    
    public Integer getOrder() {
        return orderNo;
    }
    
    public void setOrder(Integer order) {
        this.orderNo = order;
    }
    
    
    public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Integer getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}

	@Override
    public String toString() {
        return "MenuIngredient{" +
                "id=" + id +
                ", menuId=" + menuId +
                ", ingredient='" + ingredient + '\'' +
                ", order=" + orderNo +
                '}';
    }

	public UUID getRestoCode() {
		return restoCode;
	}

	public void setRestoCode(UUID restoCode) {
		this.restoCode = restoCode;
	}

	public UUID getOwnerCode() {
		return ownerCode;
	}

	public void setOwnerCode(UUID ownerCode) {
		this.ownerCode = ownerCode;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}