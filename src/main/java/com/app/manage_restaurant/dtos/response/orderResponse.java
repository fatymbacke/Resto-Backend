package com.app.manage_restaurant.dtos.response;

import java.util.List;
import java.util.UUID;

import com.app.manage_restaurant.entities.EnumOrder;

public class orderResponse {

    public static class OrderResponse {
        private UUID id;
        private String orderNumber;
        private CustomerInfoResponse customerInfo;
        private List<OrderItemResponse> orderItems;
        private Double totalPrice;
        private Integer totalItems;
        private EnumOrder status;
        private long orderDate;
        private String notes;
        private PrsnlResponse deliveryInfo;
        public OrderResponse() {}

        public OrderResponse(UUID id, String orderNumber, CustomerInfoResponse customerInfo, 
                            List<OrderItemResponse> orderItems, Double totalPrice, Integer totalItems, 
                            EnumOrder status, long orderDate,  String notes,PrsnlResponse deliveryInfo) {
            this.id = id;
            this.orderNumber = orderNumber;
            this.customerInfo = customerInfo;
            this.orderItems = orderItems;
            this.totalPrice = totalPrice;
            this.totalItems = totalItems;
            this.status = status;
            this.orderDate = orderDate;
            this.notes = notes;
            this.deliveryInfo = deliveryInfo;

        }

        // Getters et Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getOrderNumber() { return orderNumber; }
        public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
        public CustomerInfoResponse getCustomerInfo() { return customerInfo; }
        public void setCustomerInfo(CustomerInfoResponse customerInfo) { this.customerInfo = customerInfo; }
        public List<OrderItemResponse> getOrderItems() { return orderItems; }
        public void setOrderItems(List<OrderItemResponse> orderItems) { this.orderItems = orderItems; }
        public Double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
        public Integer getTotalItems() { return totalItems; }
        public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }
        public EnumOrder getStatus() { return status; }
        public void setStatus(EnumOrder status) { this.status = status; }
        public long getOrderDate() { return orderDate; }
        public void setOrderDate(long orderDate) { this.orderDate = orderDate; }
         public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }

        public PrsnlResponse getDeliveryInfo() {
			return deliveryInfo;
		}

		public void setDeliveryInfo(PrsnlResponse deliveryInfo) {
			this.deliveryInfo = deliveryInfo;
		}

		@Override
        public String toString() {
            return "OrderResponse{" + "id=" + id + ", orderNumber='" + orderNumber + 
                   "', customerInfo=" + customerInfo + ", orderItems=" + orderItems + 
                   ", totalPrice=" + totalPrice + ", totalItems=" + totalItems + 
                   ", status='" + status + "', orderDate=" + orderDate + 
                   ", notes='" + notes + "'}";
        }
    }

    public static class CustomerInfoResponse {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String address;
        private String postalCode;
        private String city;
        private String deliveryInstructions;

        public CustomerInfoResponse() {}

        

        public CustomerInfoResponse(String firstName, String lastName, String email, String phone, String address,
				String postalCode, String city, String deliveryInstructions) {
			super();
			this.firstName = firstName;
			this.lastName = lastName;
			this.email = email;
			this.phone = phone;
			this.address = address;
			this.postalCode = postalCode;
			this.city = city;
			this.deliveryInstructions = deliveryInstructions;
		}



		// Getters et Setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getDeliveryInstructions() { return deliveryInstructions; }
        public void setDeliveryInstructions(String deliveryInstructions) { this.deliveryInstructions = deliveryInstructions; }

        @Override
        public String toString() {
            return "CustomerInfoResponse{" + "firstName='" + firstName + "', lastName='" + lastName + 
                   "', email='" + email + "', phone='" + phone + "', address='" + address + 
                   "', postalCode='" + postalCode + "', city='" + city + 
                   "', deliveryInstructions='" + deliveryInstructions + "'}";
        }
    }

    public static class OrderItemResponse {
        private UUID id;
        private String menuItemName;
        private Double unitPrice;
        private Integer quantity;
        private Double totalPrice;
        private String specialInstructions;

        public OrderItemResponse() {}

        public OrderItemResponse(UUID id, String menuItemName, Double unitPrice, 
                                Integer quantity, Double totalPrice, String specialInstructions) {
            this.id = id;
            this.menuItemName = menuItemName;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
            this.totalPrice = totalPrice;
            this.specialInstructions = specialInstructions;
        }

        // Getters et Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getMenuItemName() { return menuItemName; }
        public void setMenuItemName(String menuItemName) { this.menuItemName = menuItemName; }
        public Double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public Double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
        public String getSpecialInstructions() { return specialInstructions; }
        public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

        @Override
        public String toString() {
            return "OrderItemResponse{" + "id=" + id + ", menuItemName='" + menuItemName + 
                   "', unitPrice=" + unitPrice + ", quantity=" + quantity + 
                   ", totalPrice=" + totalPrice + ", specialInstructions='" + specialInstructions + "'}";
        }
    }

    public static class OrderSummaryResponse {
        private UUID id;
        private String orderNumber;
        private String customerName;
        private String firstname;
        private String lastname;
        private Double totalPrice;
        private EnumOrder status;
        private long orderDate;
        private Integer totalItems;

        public OrderSummaryResponse() {}

        

        public OrderSummaryResponse(UUID id, String orderNumber, String customerName, String firstname, String lastname,
				Double totalPrice, EnumOrder status, long orderDate, Integer totalItems) {
			super();
			this.id = id;
			this.orderNumber = orderNumber;
			this.customerName = customerName;
			this.firstname = firstname;
			this.lastname = lastname;
			this.totalPrice = totalPrice;
			this.status = status;
			this.orderDate = orderDate;
			this.totalItems = totalItems;
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



		// Getters et Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getOrderNumber() { return orderNumber; }
        public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public Double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
        public EnumOrder getStatus() { return status; }
        public void setStatus(EnumOrder status) { this.status = status; }
        public long getOrderDate() { return orderDate; }
        public void setOrderDate(long orderDate) { this.orderDate = orderDate; }
        public Integer getTotalItems() { return totalItems; }
        public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }

        @Override
        public String toString() {
            return "OrderSummaryResponse{" + "id=" + id + ", orderNumber='" + orderNumber + 
                   "', customerName='" + customerName + "', totalPrice=" + totalPrice + 
                   ", status='" + status + "', orderDate=" + orderDate + 
                   ", totalItems=" + totalItems + '}';
        }
    }
}