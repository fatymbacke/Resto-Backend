package com.app.manage_restaurant.repositories.impl;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import com.app.manage_restaurant.cores.BaseRepositoryImpl;
import com.app.manage_restaurant.entities.MenuIngredient;
import com.app.manage_restaurant.repositories.MenuIngredientRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class MenuIngredientRepositoryImpl extends BaseRepositoryImpl<MenuIngredient, UUID> implements MenuIngredientRepository {
    
    public MenuIngredientRepositoryImpl(R2dbcEntityTemplate template) {
        super(template, MenuIngredient.class);
         super.excludeMethodFromFiltering("findByMenuId");

    }

    @Override
    public Flux<MenuIngredient> findByMenuIdOrderByOrder(UUID menuId) {
        Query query = Query.query(
            Criteria.where("menu_id").is(menuId)
                    .and("active").is(true)
        ).sort(org.springframework.data.domain.Sort.by("order_no").ascending());
        
        return template.select(query, MenuIngredient.class);
    }

    @Override
    public Mono<Void> deleteByMenuId(UUID menuId) {
        Query query = Query.query(Criteria.where("menu_id").is(menuId));
        return template.delete(query, MenuIngredient.class)
                .then();
    }

    @Override
    public Flux<MenuIngredient> findByMenuId(UUID menuId) {
        Query query = Query.query(
            Criteria.where("menu_id").is(menuId)
                    .and("active").is(true)
        );
        return template.select(query, MenuIngredient.class);
    }

    public Mono<Long> countByMenuId(UUID menuId) {
        Query query = Query.query(
            Criteria.where("menu_id").is(menuId)
                    .and("active").is(true)
        );
        return template.count(query, MenuIngredient.class);
    }

    public Mono<Boolean> existsByMenuIdAndIngredient(UUID menuId, String ingredient) {
        Query query = Query.query(
            Criteria.where("menu_id").is(menuId)
                    .and("ingredient").is(ingredient)
                    .and("active").is(true)
        );
        return template.exists(query, MenuIngredient.class);
    }

    public Mono<Void> deleteByMenuIdAndIngredient(UUID menuId, String ingredient) {
        Query query = Query.query(
            Criteria.where("menu_id").is(menuId)
                    .and("ingredient").is(ingredient)
        );
        return template.delete(query, MenuIngredient.class)
                .then();
    }

    public Flux<MenuIngredient> findByMenuIdAndIngredientIn(UUID menuId, java.util.List<String> ingredients) {
        Query query = Query.query(
            Criteria.where("menu_id").is(menuId)
                    .and("ingredient").in(ingredients)
                    .and("active").is(true)
        );
        return template.select(query, MenuIngredient.class);
    }

	@Override
	public Flux<MenuIngredient> search(Map<String, Object> filters) {
		// TODO Auto-generated method stub
		return null;
	}

    
}