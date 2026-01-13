package com.app.manage_restaurant.repositories;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import com.app.manage_restaurant.cores.BaseRepository;
import com.app.manage_restaurant.entities.Module;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@NoRepositoryBean
public interface ModuleRepository extends BaseRepository<Module, UUID> {
	
    
    
    Mono<Module> findByName(String name);
    
    Mono<Boolean> existsByName(String name);
    
  
    
    
    @Query("SELECT m.* FROM modules m WHERE m.name ILIKE :name")
    Flux<Module> findByNameContaining(String name);
    
    
   
}