package com.app.manage_restaurant.controllers;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.app.manage_restaurant.cores.BaseController;
import com.app.manage_restaurant.cores.EnumFilter;
import com.app.manage_restaurant.dtos.request.RestaurantRequest;
import com.app.manage_restaurant.dtos.response.Response;
import com.app.manage_restaurant.dtos.response.RestaurantResponse;
import com.app.manage_restaurant.entities.Restaurant;
import com.app.manage_restaurant.services.RestaurantService;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = "*")
public class RestaurantController extends BaseController<Restaurant, RestaurantRequest, RestaurantResponse, UUID> {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService,
                                ReactiveExceptionHandler exceptionHandler) {
        super(restaurantService, exceptionHandler, "Restaurant");
        this.restaurantService = restaurantService;
    }

    // ==============================
    // CREATE / UPDATE avec fichiers
    // ==============================
    @PostMapping("/setting")
    public Mono<ResponseEntity<Response>> createRestaurant(
            @RequestPart("data") @Valid RestaurantRequest request,
            @RequestPart(value = "logoFile", required = false) Mono<FilePart> logoMono,
            @RequestPart(value = "coverImageFile", required = false) Mono<FilePart> coverMono) {

        return exceptionHandler.handleMono(
                restaurantService.createWithFiles(request, logoMono, coverMono)
        );
    }

    @PutMapping("/setting/{id}")
    @Transactional
    public Mono<ResponseEntity<Response>> updateRestaurant(
            @PathVariable UUID id,
            @RequestPart("data") @Valid RestaurantRequest request,
            @RequestPart(value = "logoFile", required = false) Mono<FilePart> logoMono,
            @RequestPart(value = "coverImageFile", required = false) Mono<FilePart> coverMono) {

        if(id != null) request.setId(id);

        return exceptionHandler.handleMono(restaurantService.updateWithFiles(id, request, logoMono, coverMono));
    }

    // ==============================
    // CRUD standard via BaseController
    // ==============================
    @GetMapping("/setting")
    public Mono<ResponseEntity<Response>> findAllRestaurants() { 	
    	        return exceptionHandler.handleFlux(restaurantService.findAll());
    }
 
    @GetMapping("/home")
    public Mono<ResponseEntity<Response>> Restaurants( @RequestParam(defaultValue = "true",required = false) boolean active) {
    	return exceptionHandler.handleFlux(restaurantService.findAllRestaurants(active));
    }
    
    @GetMapping("/specials")
    public Mono<ResponseEntity<Response>> RestaurantSpecials( @RequestParam(defaultValue = "true",required = false) boolean active) {
    	return exceptionHandler.handleFlux(restaurantService.findAllRestaurants());
    }
    @GetMapping("/all")
    public Mono<ResponseEntity<Response>> OwnerRestaurants( @RequestParam(defaultValue = "true",required = false) boolean active) {
        	
    	return exceptionHandler.handleFlux(restaurantService.findAllActive(active,EnumFilter.BYOWNER));
    }

    @GetMapping("/setting/{id}")
    public Mono<ResponseEntity<Response>> getRestaurantById(@PathVariable UUID id) {
        return exceptionHandler.handleMono(restaurantService.findById(id));
    }
   
    @Override
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Response>> findById(@PathVariable UUID id) {
    	
        return exceptionHandler.handleMono(restaurantService.findById(id));
    }
    
 // ==============================
    // SEARCH AVEC PAGINATION
    // ==============================
    @PostMapping("/home/search")
    public Mono<ResponseEntity<Response>> searchHome(@RequestBody Map<String, Object> filters) {
        logger.info("🔍 Recherche {} avec pagination - filtres: {}", entityName, filters);
        return exceptionHandler.handleMono(restaurantService.searchHome(filters));
    }
    @DeleteMapping("/setting/{id}")
    public Mono<ResponseEntity<Response>> deleteRestaurant(@PathVariable UUID id) {
        return super.delete(id);
    }
    @Override
    @PostMapping("/search")
    public Mono<ResponseEntity<Response>> search(@RequestBody Map<String, Object> filters) {
        logger.info("🔍 Recherche {} avec pagination - filtres: {}", entityName, filters);
         return exceptionHandler.handleMono(service.search(filters,EnumFilter.ALL));
    }
}
