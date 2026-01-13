package com.app.manage_restaurant.mapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.app.manage_restaurant.dtos.request.OrderRequest.OrderItemRequest;
import com.app.manage_restaurant.dtos.response.orderResponse.OrderItemResponse;
import com.app.manage_restaurant.entities.OrderItem;

@Component
public class OrderItemMapper {

    /**
     * Convert CartItemRequest to OrderItem entity
     */
    public OrderItem toEntity(OrderItemRequest cartItem, UUID orderId, UUID restoCode) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(orderId);
        orderItem.setMenuItemId(cartItem.getMenuItemId());
        orderItem.setMenuItemName(cartItem.getMenuItemName());
        orderItem.setMenuItemPrice(cartItem.getMenuItemPrice());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setSpecialInstructions(cartItem.getSpecialInstructions());
        orderItem.setRestoCode(restoCode);
        return orderItem;
    }

    /**
     * Convert list of CartItemRequest to list of OrderItem entities
     */
    public List<OrderItem> toEntities(List<OrderItemRequest> cartItems, UUID orderId, UUID restoCode) {
        return cartItems.stream()
                .map(cartItem -> toEntity(cartItem, orderId, restoCode))
                .collect(Collectors.toList());
    }

    /**
     * Convert OrderItem entity to OrderItemResponse
     */
    public OrderItemResponse toResponse(OrderItem orderItem) {
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
     * Convert list of OrderItem entities to list of OrderItemResponse
     */
    public List<OrderItemResponse> toResponses(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update OrderItem entity from CartItemRequest
     */
    public OrderItem updateFromCartItem(OrderItem orderItem, OrderItemRequest cartItem) {
        if (cartItem.getMenuItemName() != null) {
            orderItem.setMenuItemName(cartItem.getMenuItemName());
        }
        if (cartItem.getMenuItemPrice() != null) {
            orderItem.setMenuItemPrice(cartItem.getMenuItemPrice());
        }
        if (cartItem.getQuantity() != null) {
            orderItem.setQuantity(cartItem.getQuantity());
        }
        if (cartItem.getSpecialInstructions() != null) {
            orderItem.setSpecialInstructions(cartItem.getSpecialInstructions());
        }
        return orderItem;
    }

    /**
     * Create OrderItem from menu details
     */
    public OrderItem fromMenuDetails(String menuItemId, String menuItemName, Double price, 
                                   Integer quantity, UUID orderId, UUID restoCode) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(orderId);
        orderItem.setMenuItemId(menuItemId);
        orderItem.setMenuItemName(menuItemName);
        orderItem.setMenuItemPrice(price);
        orderItem.setQuantity(quantity);
        orderItem.setRestoCode(restoCode);
        return orderItem;
    }
}