package com.app.manage_restaurant.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.app.manage_restaurant.dtos.request.ReservationRequest;
import com.app.manage_restaurant.dtos.request.ReservationRequest.CustomerInfo;
import com.app.manage_restaurant.dtos.response.reservationResponse.CustomerInfoResponse;
import com.app.manage_restaurant.dtos.response.reservationResponse.ReservationResponse;
import com.app.manage_restaurant.entities.EnumReservation;
import com.app.manage_restaurant.entities.Reservation;

@Component
public class ReservationMapper {

    // === REQUEST MAPPING ===

    /**
     * Convert OrderRequest to Order entity
     */
    public Reservation toEntity(ReservationRequest orderRequest) {
    	Reservation reservation = new Reservation();
    	reservation.setReservationNumber(generateOrderNumber());
        reservation.setDate(orderRequest.getDate());
        reservation.setCapacity(orderRequest.getCapacity());
        reservation.setCommentaire(orderRequest.getCommentaire());        
        reservation.setStatus(EnumReservation.PENDING);
        reservation.setRestoCode(orderRequest.getRestoCode());
        reservation.setTime(orderRequest.getTime());
        // Map customer info
        ReservationRequest.CustomerInfo customerInfo = orderRequest.getCustomerInfo();
        if (customerInfo != null) {
        	reservation.setFirstName(customerInfo.getFirstName());
        	reservation.setLastName(customerInfo.getLastName());
        	reservation.setPhone(customerInfo.getPhone());
        	reservation.setEmail(customerInfo.getEmail());
        }

        return reservation;
    }
    


    // === RESPONSE MAPPING ===

    /**
     * Convert Order entity to OrderResponse
     */
    public ReservationResponse toResponse(Reservation order) {
    	ReservationResponse response = new ReservationResponse();
        response.setId(order.getId());
        response.setReservationNumber(generateOrderNumber());
        response.setDate(order.getDate());
        response.setCapacity(order.getCapacity());
        response.setCommentaire(order.getCommentaire());        
        response.setStatus(order.getStatus());
        response.setTime(order.getTime()); 
        // Map customer info
        response.setCustomerInfo(toCustomerInfoResponse( order));
        return response;
    }

   

    /**
     * Convert Order entity to CustomerInfoResponse
     */
    public CustomerInfoResponse toCustomerInfoResponse(Reservation order) {
        CustomerInfoResponse customerInfo = new CustomerInfoResponse();      
        customerInfo.setFirstName(order.getFirstName());
        customerInfo.setLastName(order.getFirstName());
        customerInfo.setEmail(order.getEmail());
        customerInfo.setPhone(order.getPhone());
        return customerInfo;
    }


    

    // === UPDATE MAPPING ===

    /**
     * Update Order entity from OrderRequest (for partial updates)
     */
    public Reservation updateReservationFromRequest(Reservation order, ReservationRequest orderRequest) {
        if (orderRequest.getDate() != 0) {
            order.setDate(orderRequest.getDate());
        }
        if (orderRequest.getCapacity() != null) {
            order.setCapacity(orderRequest.getCapacity());
        }
        if (orderRequest.getCommentaire() != null) {
            order.setCommentaire(orderRequest.getCommentaire());
        }
        if (orderRequest.getTime() != null) {
            order.setTime(orderRequest.getTime());
        }
       

        // Update customer info if provided
        if (orderRequest.getCustomerInfo() != null) {
            CustomerInfo customerInfo = orderRequest.getCustomerInfo();
            if (customerInfo.getFirstName() != null ) {
                order.setFirstName(customerInfo.getFirstName());
            }
            if (customerInfo.getLastName() != null) {
                order.setLastName(customerInfo.getLastName());
            }
            if (customerInfo.getPhone() != null) {
                order.setPhone(customerInfo.getPhone());
            }
            if (customerInfo.getEmail() != null) {
                order.setEmail(customerInfo.getEmail());
            }
            
        }

        return order;
    }

    // === UTILITY METHODS ===

    private String generateOrderNumber() {
        return "RES-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

   

    

}