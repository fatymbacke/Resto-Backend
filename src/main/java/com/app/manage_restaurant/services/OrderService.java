package com.app.manage_restaurant.services;

import java.util.UUID;

import com.app.manage_restaurant.cores.BaseService;
import com.app.manage_restaurant.dtos.request.OrderRequest;
import com.app.manage_restaurant.dtos.response.orderResponse.OrderResponse;
import com.app.manage_restaurant.dtos.response.orderResponse.OrderSummaryResponse;
import com.app.manage_restaurant.entities.EnumOrder;
import com.app.manage_restaurant.entities.Order;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService extends BaseService<Order, OrderRequest, OrderResponse, UUID>{

	// Core Order Operations
    Mono<OrderResponse> createOrder(OrderRequest orderRequest);
    Mono<OrderResponse> getOrderById(UUID id);
    Flux<OrderResponse> getAllOrders();
    Mono<OrderResponse> updateOrderStatus(UUID orderId, String status);
    Mono<Void> cancelOrder(UUID orderId);
    
    // Query Operations
    Flux<OrderResponse> getOrdersByResto(UUID restoCode);
    Flux<OrderResponse> getOrdersByCustomer(UUID customerId);
    Flux<OrderResponse> getOrdersByStatus(String status);
    Flux<OrderSummaryResponse> getOrderSummariesByResto(UUID restoCode);
    
    // Order Items Management
    Mono<OrderResponse> addOrderItem(UUID orderId, OrderRequest.OrderItemRequest itemRequest);
    Mono<OrderResponse> removeOrderItem(UUID orderId, UUID itemId);
    
    // Utility Methods
    Mono<Long> countByRestoAndStatus(UUID restoCode, String status);
    Flux<OrderResponse> getTodayOrders(UUID restoCode);
    Mono<OrderResponse> updateOrderStatus(UUID id, EnumOrder status);
    Mono<OrderResponse> assignDeliveryOrder(UUID id, UUID deliverInfo);
}

