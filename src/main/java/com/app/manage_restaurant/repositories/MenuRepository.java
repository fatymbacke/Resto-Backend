package com.app.manage_restaurant.repositories;

import java.util.UUID;

import org.springframework.data.repository.NoRepositoryBean;

import com.app.manage_restaurant.cores.BaseRepository;
import com.app.manage_restaurant.entities.Menus;

import reactor.core.publisher.Mono;
@NoRepositoryBean
public interface MenuRepository extends BaseRepository<Menus, UUID> {

	Mono<Menus> toggleMenuAvailability(Menus request);
    
    
}
