package com.app.manage_restaurant.services;

import java.util.UUID;

import com.app.manage_restaurant.cores.BaseService;
import com.app.manage_restaurant.dtos.request.ReservationRequest;
import com.app.manage_restaurant.dtos.response.reservationResponse.ReservationResponse;
import com.app.manage_restaurant.entities.EnumReservation;
import com.app.manage_restaurant.entities.Reservation;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReservationService extends BaseService<Reservation, ReservationRequest, ReservationResponse, UUID>{

	// Core Order Operations
    Mono<ReservationResponse> createReservation(ReservationRequest reservationRequest);
    Mono<ReservationResponse> getReservationById(UUID id);
    Flux<ReservationResponse> getAllReservations();
    Mono<Void> cancelReservation(UUID reservationId);
    
    // Query Operations
    Flux<ReservationResponse> getReservationsByResto(UUID restoCode);
    Flux<ReservationResponse> getReservationsByCustomer(UUID customerId);
    Flux<ReservationResponse> getReservationsByStatus(String status);
    
    
    // Utility Methods
    Mono<Long> countByRestoAndStatus(UUID restoCode, String status);
    Flux<ReservationResponse> getTodayReservations(UUID restoCode);
    Mono<ReservationResponse> updateReservationStatus(UUID id, EnumReservation status);
    Mono<ReservationResponse> assignDeliveryReservation(UUID id, UUID deliverInfo);
}

