package com.app.manage_restaurant.repositories;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.repository.NoRepositoryBean;

import com.app.manage_restaurant.cores.BaseRepository;
import com.app.manage_restaurant.entities.Permission;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@NoRepositoryBean
public interface PermissionRepository extends BaseRepository<Permission, UUID> {
    
    Mono<Permission> findByCode(String code);
    
    Mono<Boolean> existsByCode(String code);
    
    Mono<Boolean> existsByName(String name);
    
    Flux<Permission> findByCodeIn(Set<String> codes);
    
    Flux<Permission> findByModuleId(UUID moduleId);
    
    Flux<Permission> findByIds(Set<UUID> ids);
    
    Mono<Long> countByModuleId(UUID moduleId);
    
   
    Mono<Permission> findByIdWithModule(UUID id);
    
     Flux<Permission> findPermissionsWithRole(UUID id);
     Flux<Permission> findPermissionsWithRoles(List<UUID> id);

      Flux<Permission> findActivePermissions();
     
}