package com.app.manage_restaurant.repositories;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.repository.NoRepositoryBean;

import com.app.manage_restaurant.cores.BaseRepository;
import com.app.manage_restaurant.cores.EnumFilter;
import com.app.manage_restaurant.cores.BaseRepositoryImpl.PageResponse;
import com.app.manage_restaurant.entities.Customer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@NoRepositoryBean
public interface CustomerRepository extends BaseRepository<Customer, UUID> {
    Mono<Customer> findByPhone(String phone);
    Mono<Customer> findByPhoneAndRestoCode(String phone, UUID restoCode);
    public Flux<Customer> search(UUID restoCode,Map<String, Object> filters, EnumFilter type);
    public Mono<Long> count(UUID restoCode,Map<String, Object> filters, EnumFilter type);
    public Mono<PageResponse<Customer>> searchWithPagination(UUID restoCode,Map<String, Object> filters, EnumFilter type);
}