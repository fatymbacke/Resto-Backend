package com.app.manage_restaurant.repositories;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import com.app.manage_restaurant.cores.BaseRepository;
import com.app.manage_restaurant.entities.Order;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@NoRepositoryBean
public interface OrderRepository extends BaseRepository<Order, UUID> {
    
    // Custom queries for orders
    Flux<Order> findByRestoCode(UUID restoCode);
    Flux<Order> findByCustomerId(UUID customerId);
    Flux<Order> findByStatus(String status);
    Flux<Order> findByRestoCodeAndStatus(UUID restoCode, String status);
    
    @Query("SELECT * FROM orders WHERE resto_code = :restoCode AND DATE(order_date) = CURRENT_DATE")
    Flux<Order> findTodayOrders(@Param("restoCode") UUID restoCode);
    
    @Query("SELECT * FROM orders WHERE resto_code = :restoCode AND order_date BETWEEN :startDate AND :endDate")
    Flux<Order> findByRestoCodeAndDateRange(@Param("restoCode") UUID restoCode, 
                                           @Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
    
    Mono<Long> countByRestoCode(UUID restoCode);
    
    @Query("SELECT COUNT(*) FROM orders WHERE resto_code = :restoCode AND status = :status")
    Mono<Long> countByRestoCodeAndStatus(@Param("restoCode") UUID restoCode, @Param("status") String status);
    
    // Active orders
    Flux<Order> findByActive(Boolean active);
    
    // Find orders by customer phone or email
    @Query("SELECT * FROM orders WHERE customer_phone LIKE :phone OR customer_email LIKE :email")
    Flux<Order> findByCustomerPhoneOrEmail(@Param("phone") String phone, @Param("email") String email);
    
    // Find orders with total price greater than
    @Query("SELECT * FROM orders WHERE total_price >= :minPrice")
    Flux<Order> findByTotalPriceGreaterThanEqual(@Param("minPrice") Double minPrice);
    
    // Update order status
    @Query("UPDATE orders SET status = :status, last_modified_date = :modifiedDate WHERE id = :orderId")
    Mono<Integer> updateOrderStatus(@Param("orderId") UUID orderId, 
                                   @Param("status") String status, 
                                   @Param("modifiedDate") LocalDateTime modifiedDate);
    
    // Find orders by multiple statuses
    @Query("SELECT * FROM orders WHERE status IN (:statuses)")
    Flux<Order> findByStatusIn(@Param("statuses") Iterable<String> statuses);
    
    // Find orders with items count
    @Query("SELECT o.* FROM orders o WHERE o.total_items >= :minItems")
    Flux<Order> findByTotalItemsGreaterThanEqual(@Param("minItems") Integer minItems);
}