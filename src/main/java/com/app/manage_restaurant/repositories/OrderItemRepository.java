package com.app.manage_restaurant.repositories;

import java.util.UUID;

import org.springframework.data.repository.NoRepositoryBean;

import com.app.manage_restaurant.cores.BaseRepository;
import com.app.manage_restaurant.entities.OrderItem;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@NoRepositoryBean
public interface OrderItemRepository extends BaseRepository<OrderItem, UUID> {
    
    
    Flux<OrderItem> findByOrderId(UUID orderId);
    
    Mono<Void> deleteByOrderId(UUID orderId);
    
    Mono<Long> countByOrderId(UUID orderId);
    
    // Autres méthodes si nécessaires...
    Mono<OrderItem> findById(UUID id);
    
    Flux<OrderItem> findAll();
    
    Mono<Void> deleteById(UUID id);
}
