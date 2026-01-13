package com.app.manage_restaurant.controllers;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.manage_restaurant.cores.BaseController;
import com.app.manage_restaurant.dtos.request.RolesRequest;
import com.app.manage_restaurant.dtos.request.UpdatePermissionsRequest;
import com.app.manage_restaurant.dtos.response.Response;
import com.app.manage_restaurant.dtos.response.RolesResponse;
import com.app.manage_restaurant.entities.Roles;
import com.app.manage_restaurant.services.RolesService;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;
@RestController
@CrossOrigin(origins = "*")
@Tag(name = "Role", description = "Gestion des roles membres du personnel")
@RequestMapping("/api/roles")
public class RoleController extends BaseController<Roles, RolesRequest, RolesResponse, UUID> {

    private final RolesService roleService;
    private final ReactiveExceptionHandler exceptionHandler;
    private final Logger logger = LoggerFactory.getLogger(RoleController.class);

    public RoleController(RolesService service, ReactiveExceptionHandler exceptionHandler) {
        super(service, exceptionHandler, "Role");
        this.roleService = service;
        this.exceptionHandler = exceptionHandler;
    }

    // ==============================
    // Mettre à jour les permissions d’un rôle
    // ==============================
    @PutMapping("/{roleId}/permissions")
    public Mono<ResponseEntity<Response>> updatePermissions( @PathVariable UUID roleId,@Valid @RequestBody UpdatePermissionsRequest request) {
        return exceptionHandler.handleMono(roleService.updateRolePermissions(roleId, request.getPermissions()));
    }

    // ==============================
    // Lister tous les rôles
    // ==============================
    @Override
    @GetMapping
    public Mono<ResponseEntity<Response>> findAll() {
        logger.info("📋 Récupération de tous les rôles");
        return exceptionHandler.handleFlux(roleService.findAll());
    }
    @GetMapping("/withPermissions")
    public Mono<ResponseEntity<Response>> findAll(@RequestParam(defaultValue = "true",required = false) boolean active) {
        logger.info("📋 Récupération de tous les rôles avec permission");
        return exceptionHandler.handleFlux(roleService.findAllRolesWithPermissions(active));
    }

    // ==============================
    // Changer l’état d’un rôle (actif / inactif)
    // ==============================
    @PutMapping("/{id}/state")
    public Mono<ResponseEntity<Response>> changeState(@PathVariable UUID id) {
        logger.info("🛠️ Changement d'état du rôle ID : {}", id);
        return exceptionHandler.handleMono(roleService.changeState(id));
    }

    // ==============================
    // CRUD hérité de BaseController
    // ==============================
    @Override
    public Mono<ResponseEntity<Response>> create(RolesRequest request) {
        return exceptionHandler.handleMono(roleService.save(request));
    }

    @Override
    public Mono<ResponseEntity<Response>> update(UUID id, RolesRequest request) {
        return exceptionHandler.handleMono(roleService.update(id, request));
    }

    @Override
    public Mono<ResponseEntity<Response>> delete(UUID id) {
        return exceptionHandler.handleMono(roleService.delete(id));
    }

    @Override
    public Mono<ResponseEntity<Response>> findById(UUID id) {
        return exceptionHandler.handleMono(super.findById(id));
    }
}
