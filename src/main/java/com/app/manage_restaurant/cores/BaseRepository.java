package com.app.manage_restaurant.cores;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.app.manage_restaurant.cores.BaseRepositoryImpl.PageResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@NoRepositoryBean  // ← Important : empêche Spring Data de créer une implémentation
public interface BaseRepository<T, ID> extends ReactiveCrudRepository<T, ID> {
        
	    Mono<T> findByIdAndOwnerCodeAndRestoCode(ID id, UUID ownerCode, UUID restoCode);

	    Flux<T> findAllByOwnerCodeAndRestoCode(UUID ownerCode, UUID restoCode);
	    Flux<T> findAllByRestoCode(UUID restoCode,Boolean available);

	    Mono<Boolean> existsByIdAndOwnerCodeAndRestoCode(ID id, UUID ownerCode, UUID restoCode);
	    Mono<PageResponse<T>> searchWithPagination(Map<String, Object> filters,EnumFilter type);
	    Flux<T> findByActive(Boolean active,EnumFilter type);
	     Flux<T> search(Map<String, Object> filters);
	     
	     public Flux<T> findAll(EnumFilter type);
	   
}
