package com.app.manage_restaurant.mapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.app.manage_restaurant.dtos.request.OrderRequest;
import com.app.manage_restaurant.dtos.request.OrderRequest.CustomerInfo;
import com.app.manage_restaurant.dtos.request.OrderRequest.OrderItemRequest;
import com.app.manage_restaurant.dtos.response.PrsnlResponse;
import com.app.manage_restaurant.dtos.response.orderResponse.CustomerInfoResponse;
import com.app.manage_restaurant.dtos.response.orderResponse.OrderItemResponse;
import com.app.manage_restaurant.dtos.response.orderResponse.OrderResponse;
import com.app.manage_restaurant.dtos.response.orderResponse.OrderSummaryResponse;
import com.app.manage_restaurant.entities.EnumOrder;
import com.app.manage_restaurant.entities.Order;
import com.app.manage_restaurant.entities.OrderItem;
import com.app.manage_restaurant.entities.Prsnl;

@Component
public class OrderMapper {

    // === REQUEST MAPPING ===

    /**
     * Convert OrderRequest to Order entity
     */
    public Order toEntity(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setTotalPrice(orderRequest.getTotalPrice());
        order.setTotalItems(orderRequest.getTotalItems());
        order.setOrderDate(orderRequest.getOrderDate());
        order.setStatus(EnumOrder.PENDING);
        order.setRestoCode(orderRequest.getRestoCode());

        // Map customer info
        OrderRequest.CustomerInfo customerInfo = orderRequest.getCustomerInfo();
        if (customerInfo != null) {
            order.setFirstname(customerInfo.getFirstName());
            order.setLastname(customerInfo.getLastName());
            order.setCustomerPhone(customerInfo.getPhone());
            order.setCustomerEmail(customerInfo.getEmail());
            order.setDeliveryAddress(customerInfo.getAddress());
            order.setDeliveryCity(customerInfo.getCity());
            order.setDeliveryInstructions(customerInfo.getDeliveryInstructions());
        }

        return order;
    }
    public static PrsnlResponse toResponse(Prsnl entity) {
        if (entity == null) return null;

        PrsnlResponse response = new PrsnlResponse();
        response.setId(entity.getId());
        response.setFirstname(entity.getFirstname());
        response.setLastname(entity.getLastname());
        response.setEmail(entity.getEmail());
        response.setPassword(entity.getPassword());
        response.setPhone(entity.getPhone());
        response.setRole(entity.getRole());
        response.setRestoCode(entity.getRestoCode());
        response.setActive(entity.isActive());
        response.setCreatedBy(entity.getCreatedBy());
        response.setCreatedDate(entity.getCreatedDate());
        response.setModifiedBy(entity.getModifiedBy());
        response.setModifiedDate(entity.getModifiedDate());
        response.setRoleId(entity.getRoleId());

        return response;
    }


    /**
     * Convert CartItemRequest to OrderItem entity
     */
    public OrderItem toOrderItemEntity(OrderItemRequest cartItem, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getId());
        orderItem.setMenuItemId(cartItem.getMenuItemId());
        orderItem.setMenuItemName(cartItem.getMenuItemName());
        orderItem.setMenuItemPrice(cartItem.getMenuItemPrice());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setSpecialInstructions(cartItem.getSpecialInstructions());
        orderItem.setRestoCode(order.getRestoCode());
        orderItem.setOwnerCode(order.getCustomerId());
        return orderItem;
    }

    /**
     * Convert list of CartItemRequest to list of OrderItem entities
     */
    public List<OrderItem> toOrderItemEntities(List<OrderItemRequest> cartItems, Order order) {
        return cartItems.stream()
                .map(cartItem -> toOrderItemEntity(cartItem, order))
                .collect(Collectors.toList());
    }

    // === RESPONSE MAPPING ===

    /**
     * Convert Order entity to OrderResponse
     */
    public OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setTotalPrice(order.getTotalPrice());
        response.setTotalItems(order.getTotalItems());
        response.setStatus(order.getStatus());
        response.setOrderDate(order.getOrderDate());
        response.setNotes(order.getDeliveryInstructions());
        response.setOrderItems(response.getOrderItems());
        response.setDeliveryInfo(toResponse(order.getDeliveryInfo()));
        // Map customer info
        response.setCustomerInfo(toCustomerInfoResponse(order));

        return response;
    }

    /**
     * Convert Order entity to OrderResponse with order items
     */
    public OrderResponse toResponseWithItems(Order order, List<OrderItem> orderItems) {
        OrderResponse response = toResponse(order);
        
        if (orderItems != null && !orderItems.isEmpty()) {
            List<OrderItemResponse> itemResponses = orderItems.stream()
                    .map(this::toOrderItemResponse)
                    .collect(Collectors.toList());
            response.setOrderItems(itemResponses);
        }

        return response;
    }

    /**
     * Convert Order entity to CustomerInfoResponse
     */
    public CustomerInfoResponse toCustomerInfoResponse(Order order) {
        CustomerInfoResponse customerInfo = new CustomerInfoResponse();
        
       
        customerInfo.setFirstName(order.getFirstname());
        customerInfo.setLastName(order.getLastname());
        customerInfo.setEmail(order.getCustomerEmail());
        customerInfo.setPhone(order.getCustomerPhone());
        customerInfo.setAddress(order.getDeliveryAddress());
        customerInfo.setCity(order.getDeliveryCity());
        customerInfo.setPostalCode(""); // Vous pouvez ajouter ce champ si nécessaire
        customerInfo.setDeliveryInstructions(order.getDeliveryInstructions());

        return customerInfo;
    }

    /**
     * Convert OrderItem entity to OrderItemResponse
     */
    public OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(orderItem.getId());
        response.setMenuItemName(orderItem.getMenuItemName());
        response.setUnitPrice(orderItem.getMenuItemPrice());
        response.setQuantity(orderItem.getQuantity());
        response.setTotalPrice(orderItem.getTotalPrice());
        response.setSpecialInstructions(orderItem.getSpecialInstructions());
        return response;
    }

    /**
     * Convert list of Order entities to list of OrderSummaryResponse
     */
    public OrderSummaryResponse toSummaryResponse(Order order) {
        OrderSummaryResponse summary = new OrderSummaryResponse();
        summary.setId(order.getId());
        summary.setOrderNumber(order.getOrderNumber());
        summary.setFirstname(order.getFirstname());
        summary.setLastname(order.getLastname());
        summary.setTotalPrice(order.getTotalPrice());
        summary.setStatus(order.getStatus());
        summary.setOrderDate(order.getOrderDate());
        summary.setTotalItems(order.getTotalItems());
        return summary;
    }

    /**
     * Convert list of Order entities to list of OrderSummaryResponse
     */
    public List<OrderSummaryResponse> toSummaryResponses(List<Order> orders) {
        return orders.stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }

    // === UPDATE MAPPING ===

    /**
     * Update Order entity from OrderRequest (for partial updates)
     */
    public Order updateOrderFromRequest(Order order, OrderRequest orderRequest) {
        if (orderRequest.getTotalPrice() != null) {
            order.setTotalPrice(orderRequest.getTotalPrice());
        }
        if (orderRequest.getTotalItems() != null) {
            order.setTotalItems(orderRequest.getTotalItems());
        }

        // Update customer info if provided
        if (orderRequest.getCustomerInfo() != null) {
            CustomerInfo customerInfo = orderRequest.getCustomerInfo();
            if (customerInfo.getFirstName() != null ) {
                order.setFirstname(customerInfo.getFirstName());
            }
            if (customerInfo.getLastName() != null) {
                order.setLastname(customerInfo.getLastName());
            }
            if (customerInfo.getPhone() != null) {
                order.setCustomerPhone(customerInfo.getPhone());
            }
            if (customerInfo.getEmail() != null) {
                order.setCustomerEmail(customerInfo.getEmail());
            }
            if (customerInfo.getAddress() != null) {
                order.setDeliveryAddress(customerInfo.getAddress());
            }
            if (customerInfo.getCity() != null) {
                order.setDeliveryCity(customerInfo.getCity());
            }
            if (customerInfo.getDeliveryInstructions() != null) {
                order.setDeliveryInstructions(customerInfo.getDeliveryInstructions());
            }
        }

        return order;
    }

    // === UTILITY METHODS ===

    private String generateOrderNumber() {
        return "CMD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

   

    /**
     * Calculate total price from order items
     */
    public Double calculateTotalPrice(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();
    }

    /**
     * Calculate total items count from order items
     */
    public Integer calculateTotalItems(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

}