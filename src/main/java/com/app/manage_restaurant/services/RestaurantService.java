package com.app.manage_restaurant.services;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.codec.multipart.FilePart;

import com.app.manage_restaurant.cores.BaseService;
import com.app.manage_restaurant.cores.BaseServiceImpl.PageResponse;
import com.app.manage_restaurant.dtos.request.OpeningHourRequest;
import com.app.manage_restaurant.dtos.request.RestaurantRequest;
import com.app.manage_restaurant.dtos.response.RestaurantHomeResponse;
import com.app.manage_restaurant.dtos.response.RestaurantResponse;
import com.app.manage_restaurant.dtos.response.RestaurantSpecialResponse;
import com.app.manage_restaurant.entities.Restaurant;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

public interface RestaurantService extends BaseService<Restaurant, RestaurantRequest, RestaurantResponse, UUID> {

    /**
     * Traitement FLEXIBLE des fichiers avec gestion d'absence
     */
	public Mono<Tuple2<String, String>> processFilesFlexible(Mono<FilePart> logoMono, Mono<FilePart> coverMono) ;

    

    public Mono<Void> deleteRestaurant(UUID id) ;
    public Flux<RestaurantSpecialResponse> findAllRestaurants();

    public Flux<RestaurantResponse> findAllRestaurants(boolean active);
 // Recherche avec pagination
    public Mono<PageResponse<RestaurantHomeResponse>> searchHome(Map<String, Object> filters);    
    
    public Mono<Void> updateRestaurantOpeningHours(UUID restaurantId, Set<OpeningHourRequest> openingHours);
}