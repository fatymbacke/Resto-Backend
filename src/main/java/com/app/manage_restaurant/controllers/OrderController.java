package com.app.manage_restaurant.controllers;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.manage_restaurant.cores.BaseController;
import com.app.manage_restaurant.cores.EnumFilter;
import com.app.manage_restaurant.dtos.request.AssignDeliveryOrder;
import com.app.manage_restaurant.dtos.request.OrderRequest;
import com.app.manage_restaurant.dtos.request.UpdateOrderStatusRequest;
import com.app.manage_restaurant.dtos.response.Response;
import com.app.manage_restaurant.dtos.response.orderResponse.OrderResponse;
import com.app.manage_restaurant.entities.Order;
import com.app.manage_restaurant.services.OrderService;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;
@RestController
@RequestMapping("/api/commands")
@CrossOrigin(origins = "*")
public class OrderController extends BaseController<Order, OrderRequest, OrderResponse, UUID> {    
    private OrderService orderService;
    private final ReactiveExceptionHandler exceptionHandler;
    public OrderController( OrderService orderService, ReactiveExceptionHandler exceptionHandler) {
        super(orderService, exceptionHandler, "Order");
        this.orderService = orderService;
        this.exceptionHandler = exceptionHandler;
    }   
    
 
    @Override
    @PostMapping("/home")
    public Mono<ResponseEntity<Response>> create(@Valid OrderRequest dto) {
    	// TODO Auto-generated method stub
    	 return exceptionHandler.handleMono(orderService.createOrder(dto));
    }
    
    @Override
    @PostMapping("/search")
    public Mono<ResponseEntity<Response>> search(@RequestBody Map<String, Object> filters) {
        logger.info("🔍 Recherche {} avec pagination - filtres: {}", entityName, filters);
        return exceptionHandler.handleMono(orderService.search(filters,EnumFilter.BYRESTO));
    }
    /**
     * Met à jour le statut d'une commande
     * @param id L'ID de la commande
     * @param request La requête contenant le nouveau statut
     * @return La commande mise à jour
     */
    @PatchMapping("/{id}/status")
    public Mono<ResponseEntity<Response>> updateOrderStatus(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        
        logger.info("🔄 Mise à jour du statut de la commande {} vers: {}", id, request.getStatus());
        return exceptionHandler.handleMono(orderService.updateOrderStatus(id, request.getStatus()));
    }
    
    /**
     * Assigner une commande à un livreur 
     * @param id L'ID de la commande
     * @param request La requête contenant le nouveau statut
     * @return La commande mise à jour
     */
    @PatchMapping("/{id}/assign-delivery")
    public Mono<ResponseEntity<Response>> assignDeliveryOrder(
            @PathVariable("id") UUID id,
            @Valid @RequestBody AssignDeliveryOrder request) {
        
        logger.info("🔄 Mise à jour du statut de la commande {} vers: {}", id, request.getDeliverInfo());
        return exceptionHandler.handleMono(orderService.assignDeliveryOrder(id, request.getDeliverInfo()));
    }
    
    
    
}

