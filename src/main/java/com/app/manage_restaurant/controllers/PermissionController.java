package com.app.manage_restaurant.controllers;

import java.util.List;
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
import com.app.manage_restaurant.dtos.request.ChangePermissionsRequest;
import com.app.manage_restaurant.dtos.request.PermissionRequest;
import com.app.manage_restaurant.dtos.response.PermissionResponse;
import com.app.manage_restaurant.dtos.response.Response;
import com.app.manage_restaurant.entities.Permission;
import com.app.manage_restaurant.services.PermissionService;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/permissions")
@Tag(name = "Permissions", description = "Gestion des permissions")
public class PermissionController extends BaseController<Permission, PermissionRequest, PermissionResponse, UUID> {

    private final PermissionService permissionService;
    private final ReactiveExceptionHandler exceptionHandler;
    private final Logger logger = LoggerFactory.getLogger(PermissionController.class);

    public PermissionController(PermissionService service, ReactiveExceptionHandler exceptionHandler) {
        super(service, exceptionHandler, "Permission");
        this.permissionService = service;
        this.exceptionHandler = exceptionHandler;
    }

    // ==============================
    // CRUD Hérité de BaseController avec gestion centralisée
    // ==============================
    @Override
    public Mono<ResponseEntity<Response>> create(@Valid @RequestBody PermissionRequest request) {
        return exceptionHandler.handleMono(super.create(request));
    }

    @Override
    public Mono<ResponseEntity<Response>> update(UUID id, @Valid @RequestBody PermissionRequest request) {
        return exceptionHandler.handleMono(super.update(id, request));
    }

    @Override
    public Mono<ResponseEntity<Response>> delete(UUID id) {
        return exceptionHandler.handleMono(super.delete(id));
    }

    @Override
    public Mono<ResponseEntity<Response>> findById(UUID id) {
        return exceptionHandler.handleMono(super.findById(id));
    }

    // ==============================
    // Route spécifique pour changer l'état d'une permission
    // ==============================
    @PutMapping("/{moduleId}/state")
    @Operation(summary = "Activer/Désactiver une permission")
    public Mono<ResponseEntity<Response>> changeState(@PathVariable UUID moduleId, @Valid @RequestBody ChangePermissionsRequest request) {
        logger.info("🛠️ Changement les états des permissions du module de  ID : {}", moduleId);
        // Gestion centralisée via ReactiveExceptionHandler
        return exceptionHandler.handleMono(permissionService.changeState(request));
    }
    
    
    @GetMapping("/byRoles")
    @Operation(summary = "Récupérer les permissions par liste de roles",
               description = "Retourne les permissions par liste de roles")
    public Mono<ResponseEntity<Response>> findByRoleIds(@RequestParam List<UUID> roleId) {
        logger.info("🔍 Tentative de récupération du permission par roles : {}", roleId);
        return exceptionHandler.handleFlux(permissionService.findPermissionsWithRoles(roleId))
                .doOnSuccess(resp -> logger.info("✅ Les permissions par liste de roles "))
                .doOnError(error -> logger.error("❌ Erreur lors de la récupération des permissions par liste de roles : {}", error));
    }

    @GetMapping("/active")
    @Operation(summary = "Récupérer les permissions par liste active",
               description = "Retourne les permissions par liste active")
    public Mono<ResponseEntity<Response>> findByActive() {
        logger.info("🔍 Tentative de récupération du permission par active : {}", true);
        return exceptionHandler.handleFlux(permissionService.findPermissionsActive())
                .doOnSuccess(resp -> logger.info("✅ Les permissions par liste active "))
                .doOnError(error -> logger.error("❌ Erreur lors de la récupération des permissions par liste active : {}", error));
    }
}
