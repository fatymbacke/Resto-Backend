package com.app.manage_restaurant.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.app.manage_restaurant.cores.AuditableEntity;

@Table("orders")
public class Order  extends AuditableEntity<UUID>{   
    
    @Column("customer_id")
    private UUID customerId;
    
    @Column("order_number")
    private String orderNumber;
    
    @Column("total_price")
    private Double totalPrice;
    
    @Column("total_items")
    private Integer totalItems;
    
    @Column("order_date")
    private long orderDate;
    
    @Column("status")
    private EnumOrder status;
    
    @Column("firstname")
    private String firstname;
    
    @Column("lastname")
    private String lastname;
    
    @Column("customer_phone")
    private String customerPhone;
    
    @Column("customer_email")
    private String customerEmail;
    
    @Column("delivery_address")
    private String deliveryAddress;
    
    @Column("delivery_city")
    private String deliveryCity;    
    
    @Column("delivery_instructions")
    private String deliveryInstructions;    

    @Column("deliver_info")
    private UUID deliver_info;
    
    @Transient
    private List<OrderItem> orderItems = new ArrayList<OrderItem>();
    @Transient
    private Prsnl deliveryInfo;

    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    
    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }
    
    public long getOrderDate() { return orderDate; }
    public void setOrderDate(long orderDate) { this.orderDate = orderDate; }
    
    public EnumOrder getStatus() { return status; }
    public void setStatus(EnumOrder status) { this.status = status; }
    
   
    
    public UUID getDeliver_info() {
		return deliver_info;
	}
	public void setDeliver_info(UUID deliver_info) {
		this.deliver_info = deliver_info;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    
    public String getDeliveryCity() { return deliveryCity; }
    public void setDeliveryCity(String deliveryCity) { this.deliveryCity = deliveryCity; }
    
    public List<OrderItem> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}
	
	
	public Prsnl getDeliveryInfo() {
		return deliveryInfo;
	}
	public void setDeliveryInfo(Prsnl deliveryInfo) {
		this.deliveryInfo = deliveryInfo;
	}
	public String getDeliveryInstructions() { return deliveryInstructions; }
    public void setDeliveryInstructions(String deliveryInstructions) { this.deliveryInstructions = deliveryInstructions; }
	@Override
	public String toString() {
		return "Order [customerId=" + customerId + ", orderNumber=" + orderNumber + ", totalPrice=" + totalPrice
				+ ", totalItems=" + totalItems + ", orderDate=" + orderDate + ", status=" + status + ", firstname="
				+ firstname + ", lastname=" + lastname + ", customerPhone=" + customerPhone + ", customerEmail="
				+ customerEmail + ", deliveryAddress=" + deliveryAddress + ", deliveryCity=" + deliveryCity
				+ ", deliveryInstructions=" + deliveryInstructions + ", id=" + id + ", active=" + active
				+ ", createdBy=" + createdBy + ", createdDate=" + createdDate + ", modifiedBy=" + modifiedBy
				+ ", modifiedDate=" + modifiedDate + ", restoCode=" + restoCode + ", ownerCode=" + ownerCode + "]";
	}
    
  
   
	

   
}