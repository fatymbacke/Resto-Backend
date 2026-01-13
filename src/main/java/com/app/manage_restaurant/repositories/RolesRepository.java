package com.app.manage_restaurant.repositories;

import java.util.UUID;

import org.springframework.data.repository.NoRepositoryBean;

import com.app.manage_restaurant.cores.BaseRepository;
import com.app.manage_restaurant.entities.Roles;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@NoRepositoryBean
public interface RolesRepository extends BaseRepository<Roles, UUID> {
    
    Mono<Roles> findByName(String name);
    
    Mono<Boolean> existsByName(String name);
    
    Mono<Roles> findByIsDefaultTrue();
    Flux<Roles> findByActive(Boolean active);
    Mono<Long> countUsersByRoleId(UUID roleId);
    Flux<Roles> findByResto(UUID resto);

    Flux<Roles> findAllWithPermissionsCount();
        Mono<Roles> findDefaultRole();
    
    
}