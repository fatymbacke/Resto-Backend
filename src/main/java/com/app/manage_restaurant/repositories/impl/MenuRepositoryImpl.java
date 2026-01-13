package com.app.manage_restaurant.repositories.impl;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import com.app.manage_restaurant.cores.BaseRepositoryImpl;
import com.app.manage_restaurant.entities.Menus;
import com.app.manage_restaurant.repositories.MenuRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Repository
public class MenuRepositoryImpl extends BaseRepositoryImpl<Menus, UUID> implements MenuRepository  {
	public MenuRepositoryImpl(R2dbcEntityTemplate template) {
        // ✅ CORRECTION : Passer explicitement Restaurant.class
        super(template, Menus.class);        
       // super.excludeMethodFromFiltering("findAllRestaurants");

    }

	@Override
	public Mono<Menus> toggleMenuAvailability(Menus dto) {
	    return super.template.select(Query.query(Criteria.where("id").is(dto.getId())), Menus.class)
	            .single()
	            .flatMap(menu -> {
	                menu.setIsAvailable(dto.getIsAvailable());	                
	                return super.save(menu);
	            })
	            .doOnSuccess(updated -> 
	                logger.info("Disponibilité du menu {} mise à jour: {}", updated.getId(), updated.getIsAvailable())
	            )
	            .onErrorResume(error -> {
	                logger.error("Erreur toggle disponibilité: {}", error.getMessage());
	                return Mono.error(new RuntimeException("Erreur lors du changement de disponibilité: " + error.getMessage()));
	            });
	}

	@Override
	public Flux<Menus> search(Map<String, Object> filters) {
		// TODO Auto-generated method stub
		return null;
	}
}
