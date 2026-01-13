package com.app.manage_restaurant.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface GenericMapper<E, RQ, RS> {
    
    /**
     * Convertit un DTO Request en Entité
     */
    E toEntity(RQ request);
    
    /**
     * Convertit une Entité en DTO Response
     */
    RS toResponse(E entity);
    
    /**
     * Met à jour une entité existante avec les données du DTO Request
     */
    void updateEntityFromRequest(RQ request, E entity);
    
    /**
     * Convertit une liste d'entités en liste de DTOs Response
     */
    default List<RS> toResponseList(List<E> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Convertit un set d'entités en set de DTOs Response
     */
    default Set<RS> toResponseSet(Set<E> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toSet());
    }
    
    /**
     * Convertit une liste de DTOs Request en liste d'entités
     */
    default List<E> toEntityList(List<RQ> requests) {
        if (requests == null) {
            return null;
        }
        return requests.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Convertit un flux d'entités en flux de DTOs Response (pour WebFlux)
     */
    default reactor.core.publisher.Flux<RS> toResponseFlux(reactor.core.publisher.Flux<E> entityFlux) {
        return entityFlux.map(this::toResponse);
    }
    
    /**
     * Convertit un mono d'entité en mono de DTO Response (pour WebFlux)
     */
    default reactor.core.publisher.Mono<RS> toResponseMono(reactor.core.publisher.Mono<E> entityMono) {
        return entityMono.map(this::toResponse);
    }
}