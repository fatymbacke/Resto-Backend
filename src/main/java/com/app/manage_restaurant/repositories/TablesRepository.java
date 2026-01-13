package com.app.manage_restaurant.repositories;

import java.util.UUID;

import org.springframework.data.repository.NoRepositoryBean;

import com.app.manage_restaurant.cores.BaseRepository;
import com.app.manage_restaurant.entities.Tables;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@NoRepositoryBean
public interface TablesRepository extends BaseRepository<Tables, UUID> {
   
    Flux<Tables> findByResto(UUID menuId);
    Flux<Tables> findByRestoAndStatus(UUID menuId,String status);
    Mono<Tables> findByRestoAndCapacity(UUID restoCode,Integer capacity);

}