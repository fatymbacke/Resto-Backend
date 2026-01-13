package com.app.manage_restaurant.services;
import java.util.UUID;

import com.app.manage_restaurant.cores.BaseService;
import com.app.manage_restaurant.dtos.request.TablesRequest;
import com.app.manage_restaurant.dtos.response.TablesResponse;
import com.app.manage_restaurant.entities.Tables;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TablesService extends BaseService<Tables, TablesRequest, TablesResponse, UUID> {

	public void validateShape(String shape);

	public void validateStatus(String status) ;
   
    // ==============================
    // MÉTHODES SPÉCIFIQUES AUX TABLES
    // ==============================

    // Trouver toutes les tables d'un restaurant
    public Flux<TablesResponse> findByRestaurantId(UUID restaurantId) ;
    // Trouver les tables par statut
    public Flux<TablesResponse> findByRestaurantIdAndStatus(UUID restaurantId, String status);
    // Mettre à jour le statut d'une table
    public Mono<TablesResponse> updateStatus(UUID tableId, String status);
    // Mettre à jour la position d'une table
    public Mono<TablesResponse> updatePosition(UUID tableId, Integer x, Integer y) ;
    // Mettre à jour la capacité d'une table
    public Mono<TablesResponse> updateCapacity(UUID tableId, Integer capacity) ;
    // Compter les tables actives d'un restaurant
    public Mono<Integer> countActiveTablesByRestaurant(UUID restaurantId) ;

    // Vérifier si une table existe dans le restaurant avec le même nom
    public Mono<Boolean> existsByRestaurantIdAndName(UUID restaurantId, String name);
    // Trouver les tables par forme
    public Flux<TablesResponse> findByRestaurantIdAndShape(UUID restaurantId, String shape);

    // Désactiver toutes les tables d'un restaurant
    public Mono<Integer> deactivateAllByRestaurantId(UUID restaurantId);
    // ==============================
    // OVERRIDE DES MÉTHODES DE BASE POUR AJOUTER LA LOGIQUE MÉTIER
    // ==============================

}