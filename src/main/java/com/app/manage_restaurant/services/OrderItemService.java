package com.app.manage_restaurant.services;

import java.util.UUID;

import com.app.manage_restaurant.cores.BaseService;
import com.app.manage_restaurant.dtos.request.OrderRequest.OrderItemRequest;
import com.app.manage_restaurant.dtos.response.orderResponse.OrderItemResponse;
import com.app.manage_restaurant.entities.OrderItem;

public interface OrderItemService extends BaseService<OrderItem, OrderItemRequest, OrderItemResponse, UUID>{

}

