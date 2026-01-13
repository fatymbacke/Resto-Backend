package com.app.manage_restaurant.repositories;

import java.util.UUID;

import org.springframework.data.repository.NoRepositoryBean;

import com.app.manage_restaurant.cores.BaseRepository;
import com.app.manage_restaurant.entities.MenuIngredient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@NoRepositoryBean
public interface MenuIngredientRepository extends BaseRepository<MenuIngredient, UUID> {
    
    Flux<MenuIngredient> findByMenuIdOrderByOrder(UUID menuId);
    
    Flux<MenuIngredient> findByMenuId(UUID menuId);
    
    Mono<Void> deleteByMenuId(UUID menuId);
    
    Mono<Long> countByMenuId(UUID menuId);
    
    Mono<Boolean> existsByMenuIdAndIngredient(UUID menuId, String ingredient);
    
    Mono<Void> deleteByMenuIdAndIngredient(UUID menuId, String ingredient);
    
    Flux<MenuIngredient> findByMenuIdAndIngredientIn(UUID menuId, java.util.List<String> ingredients);
}