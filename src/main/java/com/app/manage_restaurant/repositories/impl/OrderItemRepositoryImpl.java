package com.app.manage_restaurant.repositories.impl;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import com.app.manage_restaurant.cores.BaseRepositoryImpl;
import com.app.manage_restaurant.cores.EnumFilter;
import com.app.manage_restaurant.entities.OrderItem;
import com.app.manage_restaurant.repositories.OrderItemRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class OrderItemRepositoryImpl extends BaseRepositoryImpl<OrderItem, UUID> implements OrderItemRepository {
    
    public OrderItemRepositoryImpl(R2dbcEntityTemplate template) {
        super(template, OrderItem.class);
        // Exclure certaines méthodes du filtrage global si nécessaire
        excludeMethodFromFiltering("findByOrderId");
    }

    @Override
    public Mono<OrderItem> save(OrderItem entity) {  
    	 logger.debug("ORDER ITEM TO SAVE - MenuItemName: {}, Quantity: {}", 
            entity.getMenuItemName(), entity.getQuantity());   
        
        // Si l'entity a déjà un ID, c'est une mise à jour
        if (entity.getId() != null) {
            return template.update(entity)
                .doOnSuccess(updated -> 
                    logger.debug("ORDER ITEM UPDATED - ID: {}", updated.getId()))
                .doOnError(error -> 
                    logger.error("ERROR UPDATING ORDER ITEM: {}", error.getMessage()));
        }
        
        // Sinon, c'est une création
        return template.insert(entity)
            .doOnSuccess(created -> 
                logger.debug("ORDER ITEM CREATED - ID: {}", created.getId()))
            .doOnError(error -> 
                logger.error("ERROR CREATING ORDER ITEM: {}", error.getMessage()));
    }

    @Override
    public Flux<OrderItem> findByOrderId(UUID orderId) {
        Criteria criteria = Criteria.where("order_id").is(orderId);
        return applyGlobalFilter(Query.query(criteria), "findByOrderId",EnumFilter.ALL)
                .flatMapMany(query -> template.select(query, OrderItem.class));
    }

   

    @Override
    public Mono<Void> deleteByOrderId(UUID orderId) {
        Criteria criteria = Criteria.where("order_id").is(orderId);
        return applyGlobalFilter(Query.query(criteria), "deleteByOrderId",EnumFilter.ALL)
                .flatMap(query -> template.delete(query, OrderItem.class))
                .then()
                .doOnSuccess(v -> 
                    logger.debug("ORDER ITEMS DELETED FOR ORDER ID: {}", orderId))
                .doOnError(error -> 
                    logger.error("ERROR DELETING ORDER ITEMS: {}", error.getMessage()));
    }

    @Override
    public Mono<Long> countByOrderId(UUID orderId) {
        Criteria criteria = Criteria.where("order_id").is(orderId);
        return applyGlobalFilter(Query.query(criteria), "countByOrderId",EnumFilter.ALL)
                .flatMap(query -> template.count(query, OrderItem.class))
                .doOnSuccess(count -> 
                    logger.debug("ORDER ITEMS COUNT FOR ORDER {}: {}", orderId, count))
                .doOnError(error -> 
                    logger.error("ERROR COUNTING ORDER ITEMS: {}", error.getMessage()));
    }

	@Override
	public Flux<OrderItem> search(Map<String, Object> filters) {
		// TODO Auto-generated method stub
		return null;
	}
}