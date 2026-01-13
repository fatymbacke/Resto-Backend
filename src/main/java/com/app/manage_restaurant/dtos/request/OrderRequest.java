package com.app.manage_restaurant.dtos.request;
import java.util.List;
import java.util.UUID;

public class OrderRequest {

        private CustomerInfo customerInfo;
        private List<OrderItemRequest> orderItems;
        private Double totalPrice;
        private Integer totalItems;
        private long orderDate;
    	private UUID restoCode;      

		public UUID getRestoCode() {
			return restoCode;
		}
		public void setRestoCode(UUID restoCode) {
			this.restoCode = restoCode;
		}
		// Getters et Setters
        public CustomerInfo getCustomerInfo() { return customerInfo; }
        public void setCustomerInfo(CustomerInfo customerInfo) { this.customerInfo = customerInfo; }
        public List<OrderItemRequest> getOrderItems() { return orderItems; }
        public void setOrderItems(List<OrderItemRequest> cartItems) { this.orderItems = cartItems; }
        public Double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
        public Integer getTotalItems() { return totalItems; }
        public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }
        public long getOrderDate() { return orderDate; }
        public void setOrderDate(long orderDate) { this.orderDate = orderDate; }

        @Override
        public String toString() {
            return "OrderRequest{" + "customerInfo=" + customerInfo + ", cartItems=" + orderItems + 
                   ", totalPrice=" + totalPrice + ", totalItems=" + totalItems + ", orderDate='" + orderDate + "'}";
        }
    

    public static class CustomerInfo {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String address;
        private String postalCode;
        private String city;
        private String deliveryInstructions;

        public CustomerInfo() {}

        public CustomerInfo(String firstName, String lastName, String email, String phone, 
                           String address, String postalCode, String city, String deliveryInstructions) {
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
            return "CustomerInfo{" + "firstName='" + firstName + "', lastName='" + lastName + 
                   "', email='" + email + "', phone='" + phone + "', address='" + address + 
                   "', postalCode='" + postalCode + "', city='" + city + 
                   "', deliveryInstructions='" + deliveryInstructions + "'}";
        }
    }

    public static class OrderItemRequest {
        private String menuItemId;
        private String menuItemName;
        private Double menuItemPrice;
        private String menuItemImageUrl;
        private Integer quantity;
        private String specialInstructions;
    	private UUID restoCode;       

        // Getters et Setters
        public String getMenuItemId() { return menuItemId; }
        public void setMenuItemId(String menuItemId) { this.menuItemId = menuItemId; }
        public String getMenuItemName() { return menuItemName; }
        public void setMenuItemName(String menuItemName) { this.menuItemName = menuItemName; }
        public Double getMenuItemPrice() { return menuItemPrice; }
        public void setMenuItemPrice(Double menuItemPrice) { this.menuItemPrice = menuItemPrice; }
        public String getMenuItemImageUrl() { return menuItemImageUrl; }
        public void setMenuItemImageUrl(String menuItemImageUrl) { this.menuItemImageUrl = menuItemImageUrl; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public String getSpecialInstructions() { return specialInstructions; }
        public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

        public UUID getRestoCode() {
			return restoCode;
		}



		public void setRestoCode(UUID restoCode) {
			this.restoCode = restoCode;
		}
		@Override
		public String toString() {
			return "OrderItemRequest [menuItemId=" + menuItemId + ", menuItemName=" + menuItemName + ", menuItemPrice="
					+ menuItemPrice + ", menuItemImageUrl=" + menuItemImageUrl + ", quantity=" + quantity
					+ ", specialInstructions=" + specialInstructions + ", restoCode=" + restoCode + "]";
		}



    }
}