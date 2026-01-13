package com.app.manage_restaurant.repositories;

import java.util.UUID;

import org.springframework.data.repository.NoRepositoryBean;

import com.app.manage_restaurant.cores.BaseRepository;
import com.app.manage_restaurant.dtos.response.MenusResponse;
import com.app.manage_restaurant.entities.MenuTag;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@NoRepositoryBean
public interface MenuTagRepository extends BaseRepository<MenuTag, UUID> {
    Flux<MenuTag> findByMenuId(UUID menuId);
    Mono<Void> deleteByMenuId(UUID menuId);
}