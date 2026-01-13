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
import com.app.manage_restaurant.dtos.request.ReservationRequest;
import com.app.manage_restaurant.dtos.request.UpdateReservationStatusRequest;
import com.app.manage_restaurant.dtos.response.Response;
import com.app.manage_restaurant.dtos.response.reservationResponse.ReservationResponse;
import com.app.manage_restaurant.entities.Reservation;
import com.app.manage_restaurant.services.ReservationService;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;
@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController extends BaseController<Reservation, ReservationRequest, ReservationResponse, UUID> {    
    private ReservationService reservationService;
    private final ReactiveExceptionHandler exceptionHandler;
    public ReservationController( ReservationService reservationService, ReactiveExceptionHandler exceptionHandler) {
        super(reservationService, exceptionHandler, "Reservation");
        this.reservationService = reservationService;
        this.exceptionHandler = exceptionHandler;
    }   
    
 
    @Override
    @PostMapping("/home")
    public Mono<ResponseEntity<Response>> create(@Valid ReservationRequest dto) {
    	// TODO Auto-generated method stub
    	 return exceptionHandler.handleMono(reservationService.createReservation(dto));
    }
    
    @Override
    @PostMapping("/search")
    public Mono<ResponseEntity<Response>> search(@RequestBody Map<String, Object> filters) {
        logger.info("🔍 Recherche {} avec pagination - filtres: {}", entityName, filters);
        return exceptionHandler.handleMono(reservationService.search(filters,EnumFilter.BYRESTO));
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
            @Valid @RequestBody UpdateReservationStatusRequest request) {
        
        logger.info("🔄 Mise à jour du statut de la Reservation {} vers: {}", id, request.getStatus());
        return exceptionHandler.handleMono(reservationService.updateReservationStatus(id, request.getStatus()));
    }
    
    
    
    
}

