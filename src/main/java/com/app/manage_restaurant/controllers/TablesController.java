package com.app.manage_restaurant.controllers;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.manage_restaurant.cores.BaseController;
import com.app.manage_restaurant.dtos.request.TablesRequest;
import com.app.manage_restaurant.dtos.response.Response;
import com.app.manage_restaurant.dtos.response.TablesResponse;
import com.app.manage_restaurant.entities.Tables;
import com.app.manage_restaurant.services.TablesService;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tables")
@Tag(name = "Tables", description = "Gestion des tables de restaurant")
public class TablesController extends BaseController<Tables, TablesRequest, TablesResponse, UUID> {

    private final TablesService tablesService;
    private final ReactiveExceptionHandler exceptionHandler;
    private final Logger logger = LoggerFactory.getLogger(TablesController.class);

    public TablesController(TablesService service, ReactiveExceptionHandler exceptionHandler) {
        super(service, exceptionHandler, "Table");
        this.tablesService = service;
        this.exceptionHandler = exceptionHandler;
    }

    // ==============================
    // CRUD Hérité de BaseController avec gestion centralisée
    // ==============================
    @Override
    public Mono<ResponseEntity<Response>> create(@Valid @RequestBody TablesRequest request) {
        return super.create(request);
    }
    
    @Override
    public Mono<ResponseEntity<Response>> update(UUID id, @Valid TablesRequest dto) {
    	// TODO Auto-generated method stub
        return super.update(id, dto);
    }
   
    @Override
    public Mono<ResponseEntity<Response>> delete(@PathVariable UUID id) {
        return super.delete(id);
    }

    @Override
    public Mono<ResponseEntity<Response>> findById(@PathVariable UUID id) {
        return exceptionHandler.handleMono(tablesService.findById(id));
    }

    // ==============================
    // Route spécifique pour changer l'état d'une table
    // ==============================
    @PutMapping("/{id}/state")
    @Operation(summary = "Activer/Désactiver une table")
    public Mono<ResponseEntity<Response>> changeState(@PathVariable UUID id) {
        logger.info("🛠️ Changement d'état de la table ID : {}", id);

        return exceptionHandler.handleMono(tablesService.changeState(id));
    }

    // ==============================
    // ROUTES SPÉCIFIQUES AUX TABLES
    // ==============================

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Obtenir toutes les tables d'un restaurant")
    public Mono<ResponseEntity<Response>> findByRestaurantId(@PathVariable UUID restaurantId) {
        logger.info("📋 Récupération des tables pour le restaurant ID : {}", restaurantId);

        return exceptionHandler.handleFlux(tablesService.findByRestaurantId(restaurantId));
    }

    @GetMapping("/restaurant/{restaurantId}/status/{status}")
    @Operation(summary = "Obtenir les tables d'un restaurant par statut")
    public Mono<ResponseEntity<Response>> findByRestaurantIdAndStatus(
            @PathVariable UUID restaurantId, 
            @PathVariable String status) {
        logger.info("📋 Récupération des tables pour le restaurant ID : {} avec statut : {}", restaurantId, status);

        return exceptionHandler.handleFlux(tablesService.findByRestaurantIdAndStatus(restaurantId, status));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Mettre à jour le statut d'une table")
    public Mono<ResponseEntity<Response>> updateStatus(
            @PathVariable UUID id, 
            @RequestParam String status) {
        logger.info("🔄 Mise à jour du statut de la table ID : {} → {}", id, status);

        return exceptionHandler.handleMono(tablesService.updateStatus(id, status));
    }

    @PutMapping("/{id}/position")
    @Operation(summary = "Mettre à jour la position d'une table")
    public Mono<ResponseEntity<Response>> updatePosition(
            @PathVariable UUID id, 
            @RequestParam Integer x, 
            @RequestParam Integer y) {
        logger.info("📍 Mise à jour de la position de la table ID : {} → ({}, {})", id, x, y);

        return exceptionHandler.handleMono(tablesService.updatePosition(id, x, y));
    }

    @PutMapping("/{id}/capacity")
    @Operation(summary = "Mettre à jour la capacité d'une table")
    public Mono<ResponseEntity<Response>> updateCapacity(
            @PathVariable UUID id, 
            @RequestParam Integer capacity) {
        logger.info("🪑 Mise à jour de la capacité de la table ID : {} → {}", id, capacity);

        return exceptionHandler.handleMono(tablesService.updateCapacity(id, capacity));
    }

    @GetMapping("/restaurant/{restaurantId}/shape/{shape}")
    @Operation(summary = "Obtenir les tables d'un restaurant par forme")
    public Mono<ResponseEntity<Response>> findByRestaurantIdAndShape(
            @PathVariable UUID restaurantId, 
            @PathVariable String shape) {
        logger.info("🔷 Récupération des tables pour le restaurant ID : {} avec forme : {}", restaurantId, shape);

        return exceptionHandler.handleFlux(tablesService.findByRestaurantIdAndShape(restaurantId, shape));
    }

    @GetMapping("/restaurant/{restaurantId}/count")
    @Operation(summary = "Compter les tables actives d'un restaurant")
    public Mono<ResponseEntity<Response>> countActiveTablesByRestaurant(@PathVariable UUID restaurantId) {
        logger.info("🔢 Comptage des tables actives pour le restaurant ID : {}", restaurantId);

        return exceptionHandler.handleMono(tablesService.countActiveTablesByRestaurant(restaurantId));
    }

    @PutMapping("/restaurant/{restaurantId}/deactivate-all")
    @Operation(summary = "Désactiver toutes les tables d'un restaurant")
    public Mono<ResponseEntity<Response>> deactivateAllByRestaurantId(@PathVariable UUID restaurantId) {
        logger.info("🚫 Désactivation de toutes les tables du restaurant ID : {}", restaurantId);

        return exceptionHandler.handleMono(tablesService.deactivateAllByRestaurantId(restaurantId));
    }

    
}