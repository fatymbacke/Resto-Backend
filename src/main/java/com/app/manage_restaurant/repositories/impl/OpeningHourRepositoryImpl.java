package com.app.manage_restaurant.repositories.impl;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;

import com.app.manage_restaurant.cores.BaseRepositoryImpl;
import com.app.manage_restaurant.entities.OpeningHour;
import com.app.manage_restaurant.repositories.OpeningHourRepository;

import reactor.core.publisher.Flux;

@Repository
public class OpeningHourRepositoryImpl extends BaseRepositoryImpl<OpeningHour, UUID> implements OpeningHourRepository {
    
	public OpeningHourRepositoryImpl(R2dbcEntityTemplate template) {
        // ✅ CORRECTION : Passer explicitement Restaurant.class
        super(template, OpeningHour.class);
        // super.excludeMethodFromFiltering("findOpeningHoursWithRestaurantId");

        
    }

	
	 @Override
	 public Flux<OpeningHour> findOpeningHoursWithRestaurantId(UUID restaurantId) {
	        logger.debug("Finding opening hours for restaurant ID: {}", restaurantId);
	   
	        
	        String sql = "SELECT r.*  FROM opening_hour r LEFT JOIN restaurant_opening_hour rp ON r.id = rp.opening_hour_id " +
	                    "WHERE restaurant_id  =:restaurantId  ";
	        
	        return template.getDatabaseClient()
	        		.sql(sql)
	                .bind("restaurantId", restaurantId)
	                .map((row, metadata) -> {
	                	OpeningHour openingHour = new OpeningHour();
	                	openingHour.setId(row.get("id", UUID.class));
	                	openingHour.setClose(row.get("close", String.class));
	                	openingHour.setClosed(row.get("is_closed", Boolean.class));
	                	openingHour.setOpen(row.get("open", String.class));
	                	openingHour.setDays(row.get("days", Integer.class));
	                 
	                    return openingHour;
	                })
	                .all()
	                .doOnComplete(() -> logger.debug("Completed finding all Roles with permissions count"))
	                .doOnError(error -> logger.error("Error finding Roles with permissions count: {}", 
	                    error.getMessage(), error));
	    }


	@Override
	public Flux<OpeningHour> search(Map<String, Object> filters) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
	
	
	
	
}