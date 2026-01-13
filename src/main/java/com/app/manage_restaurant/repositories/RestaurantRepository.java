package com.app.manage_restaurant.repositories;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.repository.NoRepositoryBean;

import com.app.manage_restaurant.cores.BaseRepository;
import com.app.manage_restaurant.cores.BaseRepositoryImpl.PageResponse;
import com.app.manage_restaurant.entities.Restaurant;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@NoRepositoryBean
public interface RestaurantRepository extends BaseRepository<Restaurant, UUID> {

	Flux<Restaurant> findAllRestaurants(boolean active);

    
    public Mono<PageResponse<Restaurant>> searchWithPagination(Map<String, Object> filters) ;

   
   
}