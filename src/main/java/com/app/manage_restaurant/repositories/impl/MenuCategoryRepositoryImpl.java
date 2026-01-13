package com.app.manage_restaurant.repositories.impl;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;

import com.app.manage_restaurant.cores.BaseRepositoryImpl;
import com.app.manage_restaurant.entities.MenuCategory;
import com.app.manage_restaurant.repositories.MenuCategoryRepository;

import reactor.core.publisher.Flux;
@Repository
public class MenuCategoryRepositoryImpl extends BaseRepositoryImpl<MenuCategory, UUID> implements MenuCategoryRepository  {
	public MenuCategoryRepositoryImpl(R2dbcEntityTemplate template) {
        // ✅ CORRECTION : Passer explicitement Restaurant.class
        super(template, MenuCategory.class);        
       // super.excludeMethodFromFiltering("findAllRestaurants");

    }

	@Override
	public Flux<MenuCategory> search(Map<String, Object> filters) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
