package com.app.manage_restaurant.controllers;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.manage_restaurant.cores.BaseController;
import com.app.manage_restaurant.dtos.request.ModuleRequest;
import com.app.manage_restaurant.dtos.request.UpdateModuleActiveRequest;
import com.app.manage_restaurant.dtos.request.UpdateOrderStatusRequest;
import com.app.manage_restaurant.dtos.request.UpdatePermissionsRequest;
import com.app.manage_restaurant.dtos.response.ModuleResponse;
import com.app.manage_restaurant.dtos.response.Response;
import com.app.manage_restaurant.entities.Module;
import com.app.manage_restaurant.services.ModuleService;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/modules")
@Tag(name = "Modules", description = "Gestion des modules")
public class ModuleController extends BaseController<Module, ModuleRequest, ModuleResponse, UUID> {

    private final ModuleService moduleService;
    private final ReactiveExceptionHandler exceptionHandler;
    private final Logger logger = LoggerFactory.getLogger(ModuleController.class);

    public ModuleController(ModuleService service, ReactiveExceptionHandler exceptionHandler) {
        super(service, exceptionHandler, "Module");
        this.moduleService = service;
        this.exceptionHandler = exceptionHandler;
    }

    // ==============================
    // LIST ALL MODULES WITH PERMISSIONS
    // ==============================
    @Override
    @GetMapping
    @Operation(summary = "Lister tous les modules avec privilèges",
               description = "Retourne la liste de tous les modules avec privilèges")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public Mono<ResponseEntity<Response>> findAll() {
        logger.info("📋 Tentative de récupération de tous les modules avec privilèges");

        Mono<?> modulesMono = moduleService.findAllWithPermissions().collectList();

        return exceptionHandler.handleMono(modulesMono)
                .doOnSuccess(resp -> logger.info("✅ Récupération réussie de modules avec privilèges"))
                .doOnError(error -> logger.error("❌ Erreur lors de la récupération de modules avec privilèges", error));
    }

    // ==============================
    // GET MODULE BY ID
    // ==============================
    @Override
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un module par ID",
               description = "Retourne un module spécifique par son identifiant")
    public Mono<ResponseEntity<Response>> findById(@PathVariable UUID id) {
        logger.info("🔍 Tentative de récupération du module ID : {}", id);
        return exceptionHandler.handleMono(moduleService.findById(id))
                .doOnSuccess(resp -> logger.info("✅ Module récupéré ID : {}", id))
                .doOnError(error -> logger.error("❌ Erreur lors de la récupération du module ID : {}", id, error));
    }

    // ==============================
    // CREATE MODULE
    // ==============================
    @Override
    @PostMapping
    @Operation(summary = "Créer un module",
               description = "Crée un nouveau module")
    public Mono<ResponseEntity<Response>> create(@Valid @RequestBody ModuleRequest request) {
        logger.info("➕ Création d'un nouveau module : {}", request.getName());
        return exceptionHandler.handleMono(super.create(request))
                .doOnSuccess(resp -> logger.info("✅ Module créé : {}", request.getName()))
                .doOnError(error -> logger.error("❌ Erreur lors de la création du module : {}", request.getName(), error));
    }

    // ==============================
    // UPDATE MODULE
    // ==============================
    @Override
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un module",
               description = "Met à jour un module existant")
    public Mono<ResponseEntity<Response>> update(@PathVariable UUID id, @Valid @RequestBody ModuleRequest request) {
        logger.info("✏️ Mise à jour du module ID : {}", id);
        return exceptionHandler.handleMono(super.update(id, request))
                .doOnSuccess(resp -> logger.info("✅ Module mis à jour ID : {}", id))
                .doOnError(error -> logger.error("❌ Erreur lors de la mise à jour du module ID : {}", id, error));
    }

    // ==============================
    // DELETE MODULE
    // ==============================
    @Override
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un module",
               description = "Supprime un module existant")
    public Mono<ResponseEntity<Response>> delete(@PathVariable UUID id) {
        logger.info("🗑️ Suppression du module ID : {}", id);
        return exceptionHandler.handleMono(super.delete(id))
                .doOnSuccess(resp -> logger.info("✅ Module supprimé ID : {}", id))
                .doOnError(error -> logger.error("❌ Erreur lors de la suppression du module ID : {}", id, error));
    }

    // ==============================
    // UPDATE MODULE PERMISSIONS
    // ==============================
    @PutMapping("/{id}/permissions")
    @Operation(summary = "Mettre à jour les permissions d'un module",
               description = "Met à jour les permissions associées à un module")
    public Mono<ResponseEntity<Response>> updatePermissions(@PathVariable UUID id, @Valid @RequestBody UpdatePermissionsRequest request) {
        logger.info("✏️ Mise à jour des permissions du module ID : {}", id);
        Mono<ModuleResponse> updateMono = moduleService.updateModulePermissions(id, request.getPermissions());
        return exceptionHandler.handleMono(updateMono)
                .doOnSuccess(resp -> logger.info("✅ Permissions mises à jour module ID : {}", id))
                .doOnError(error -> logger.error("❌ Erreur lors de la mise à jour des permissions module ID : {}", id, error));
    }
    
    
    // ==============================
    // CHANGE STATE (ACTIVE/INACTIVE)
    // ==============================
    @Override
    @GetMapping("/{id}/changestate")
    public Mono<ResponseEntity<Response>> changeState(@PathVariable UUID id) {
        return exceptionHandler.handleMono(moduleService.changeState(id));
    }
    
    /**
     * Met à jour le statut d'une module
     * @param id L'ID de la module
     * @param request La requête contenant le nouveau statut
     * @return La module mise à jour
     */
    @PatchMapping("/{id}/changestate")
    public Mono<ResponseEntity<Response>> updateOrderStatus(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateModuleActiveRequest request) {
        
        logger.info("🔄 Mise à jour de l'état du module {} vers: {}", id, request.isActive());
        return exceptionHandler.handleMono(moduleService.updateActive(id, request.isActive()));
    }
}
