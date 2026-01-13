package com.app.manage_restaurant.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.app.manage_restaurant.dtos.response.OpeningHourResponse;
import com.app.manage_restaurant.entities.OpeningHour;

public class OpeningHourMapper {
    
    
    
    public static OpeningHourResponse toResponse(OpeningHour entity) {
    	OpeningHourResponse response = new OpeningHourResponse(); 	
        response.setId(entity.getId());
        response.setClose(entity.getClose());
        response.setDays(entity.getDays());
        response.setIsClosed(entity.isClosed());
        response.setOpen(entity.getOpen());       
        return response;
    }
    
    
    /**
     * Convertit un flux d'entités en flux de DTOs Response (pour WebFlux)
     */
    public static Set<OpeningHourResponse> toResponses(Set<OpeningHour> entities) {
        return entities.stream().map(tab->toResponse(tab)).collect(Collectors.toSet());
    }
}