package com.app.manage_restaurant.repositories;

import java.util.UUID;

import org.springframework.data.repository.NoRepositoryBean;

import com.app.manage_restaurant.cores.BaseRepository;
import com.app.manage_restaurant.entities.OpeningHour;

import reactor.core.publisher.Flux;

@NoRepositoryBean
public interface OpeningHourRepository extends BaseRepository<OpeningHour, UUID> {
    
     Flux<OpeningHour> findOpeningHoursWithRestaurantId(UUID id);
}