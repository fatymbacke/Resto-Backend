package com.app.manage_restaurant.repositories.impl;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.app.manage_restaurant.cores.BaseRepositoryImpl;
import com.app.manage_restaurant.cores.EnumFilter;
import com.app.manage_restaurant.entities.Restaurant;
import com.app.manage_restaurant.repositories.RestaurantRepository;
import com.app.manage_restaurant.security.SecurityUser;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class RestaurantRepositoryImpl extends BaseRepositoryImpl<Restaurant, UUID> implements RestaurantRepository {
    
    public RestaurantRepositoryImpl(R2dbcEntityTemplate template) {
        // ✅ CORRECTION : Passer explicitement Restaurant.class
        super(template, Restaurant.class);        
       // super.excludeMethodFromFiltering("findAllRestaurants");

    }

	@Override
	public Flux<Restaurant> findAllRestaurants(boolean active) {
		  logger.debug("Finding Restaurants by active status: {}", active);
	        return applyGlobalFilter(Query.query(Criteria.where("active").is(active)),EnumFilter.ALL)
	                .flatMapMany(q -> template.select(q, entityClass))
	                .doOnComplete(() -> logger.debug("Completed finding Restaurants by active status: {}", active))
	                .doOnError(error -> logger.error("Error finding Restaurants by active status {}: {}", active, error.getMessage(), error));
	   
	}

	
	@Transactional
    public Mono<Restaurant> save(Restaurant entity) {
        return Mono.deferContextual(ctx -> {
            SecurityUser securityUser = ctx.getOrDefault("CURRENT_USER", null);
                    
            if (securityUser != null) {
                if (entity.getOwnerCode() == null) {
                    entity.setOwnerCode(securityUser.getOwnerCode());
                }
                if (entity.getRestoCode() == null) {
                    entity.setRestoCode(securityUser.getRestoCode());
                }
            }

            if (entity.getOwnerCode() == null && entity.getRestoCode() == null) {
                return Mono.error(new IllegalStateException(
                    "Au moins un code (ownerCode ou restoCode) doit être présent"));
            }

            if (entity.getId() == null) {
                return template.insert(entity);
            } else {
                return updateEntityWithExistenceCheck(entity);
            }
        });
    }
   
    private  Mono<Restaurant> updateEntityWithExistenceCheck(Restaurant entity) {
        logger.debug("UPDATE RESTAURANT WITH EXISTENCE CHECK - ID: {}", entity.getId());
        
        return super.findExistingEntityWithoutRestoCodeAndOwner(entity)
            .flatMap(existing -> {
               super.copyUpdatableFields(existing, entity);                                
                logger.debug("ATTEMPTING RESTAURANT UPDATE...");
                return template.update(existing);
            })
            .flatMap(updated -> {
                if (updated == null) {
                    logger.error("TEMPLATE.UPDATE RETURNED NULL");
                    return Mono.error(new RuntimeException("L'opération de mise à jour a échoué - résultat null"));
                }
                
                logger.debug("UPDATE RESTAURANT SUCCESSFUL - ID: {}", updated.getId());
                logger.debug("UPDATED RESTAURANT ENTITY: {}", updated.toString());
                return Mono.just(updated);
            })
            .onErrorResume(err -> {
                logger.error("UPDATERESTAURANT ERROR: {}", err.getMessage(), err);
                return super.handleUpdateError(err);
            });
    }
    
    // =====================================
    // Méthode de recherche avec résultat paginé
    // =====================================
    @Override
    public Mono<PageResponse<Restaurant>> searchWithPagination(Map<String, Object> filters) {
        return Mono.zip(
            super.search(filters,EnumFilter.NOTHING).collectList(),
            count(filters,EnumFilter.NOTHING)
        ).map(tuple -> {
            java.util.List<Restaurant> content = tuple.getT1();
            long totalElements = tuple.getT2();
            
            int page = (int) filters.getOrDefault("page", 0);
            int size = (int) filters.getOrDefault("size", 20);
            int totalPages = (int) Math.ceil((double) totalElements / size);
            
            return new PageResponse<>(
                content,
                page,
                size,
                totalElements,
                totalPages,
                page > 0,
                page < totalPages - 1
            );
        });
    }

	@Override
	public Flux<Restaurant> search(Map<String, Object> filters) {
		// TODO Auto-generated method stub
		return null;
	}
}