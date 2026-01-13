package com.app.manage_restaurant.repositories;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import com.app.manage_restaurant.cores.BaseRepository;
import com.app.manage_restaurant.entities.Reservation;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@NoRepositoryBean
public interface ReservationRepository extends BaseRepository<Reservation, UUID> {
    
    // Custom queries for orders
    Flux<Reservation> findByRestoCode(UUID restoCode);
    Flux<Reservation> findByCustomerId(UUID customerId);
    Flux<Reservation> findByStatus(String status);
    Flux<Reservation> findByRestoCodeAndStatus(UUID restoCode, String status);
    
    @Query("SELECT * FROM orders WHERE resto_code = :restoCode AND DATE(order_date) = CURRENT_DATE")
    Flux<Reservation> findTodayOrders(@Param("restoCode") UUID restoCode);
    
    @Query("SELECT * FROM orders WHERE resto_code = :restoCode AND order_date BETWEEN :startDate AND :endDate")
    Flux<Reservation> findByRestoCodeAndDateRange(@Param("restoCode") UUID restoCode, 
                                           @Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    Mono<Long> countByRestoCode(UUID restoCode);
    
    @Query("SELECT COUNT(*) FROM orders WHERE resto_code = :restoCode AND status = :status")
    Mono<Long> countByRestoCodeAndStatus(@Param("restoCode") UUID restoCode, @Param("status") String status);
    
    // Active orders
    Flux<Reservation> findByActive(Boolean active);
    
    // Find orders by customer phone or email
    @Query("SELECT * FROM orders WHERE customer_phone LIKE :phone OR customer_email LIKE :email")
    Flux<Reservation> findByCustomerPhoneOrEmail(@Param("phone") String phone, @Param("email") String email);
    
    // Find orders with total price greater than
    @Query("SELECT * FROM orders WHERE total_price >= :minPrice")
    Flux<Reservation> findByTotalPriceGreaterThanEqual(@Param("minPrice") Double minPrice);
    
    // Update order status
    @Query("UPDATE orders SET status = :status, last_modified_date = :modifiedDate WHERE id = :orderId")
    Mono<Integer> updateOrderStatus(@Param("orderId") UUID orderId, 
                                   @Param("status") String status, 
                                   @Param("modifiedDate") LocalDateTime modifiedDate);
    
    // Find orders by multiple statuses
    @Query("SELECT * FROM orders WHERE status IN (:statuses)")
    Flux<Reservation> findByStatusIn(@Param("statuses") Iterable<String> statuses);
    
    // Find orders with items count
    @Query("SELECT o.* FROM orders o WHERE o.total_items >= :minItems")
    Flux<Reservation> findByTotalItemsGreaterThanEqual(@Param("minItems") Integer minItems);
}