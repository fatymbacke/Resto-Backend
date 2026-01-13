 package com.app.manage_restaurant.controllers;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.manage_restaurant.cores.BaseController;
import com.app.manage_restaurant.cores.EnumFilter;
import com.app.manage_restaurant.dtos.request.Login;
import com.app.manage_restaurant.dtos.request.PrsnlRequest;
import com.app.manage_restaurant.dtos.response.PrsnlResponse;
import com.app.manage_restaurant.dtos.response.Response;
import com.app.manage_restaurant.entities.Prsnl;
import com.app.manage_restaurant.services.PrsnlService;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/prsnls")
@CrossOrigin(origins = "*")
@Tag(name = "Personnel", description = "Gestion des membres du personnel")
public class PrsnlController extends BaseController<Prsnl, PrsnlRequest, PrsnlResponse, UUID> {

    private static final Logger logger = LoggerFactory.getLogger(PrsnlController.class);
    private final PrsnlService prsnlService;

    public PrsnlController(PrsnlService service, ReactiveExceptionHandler exceptionHandler) {
        super(service, exceptionHandler, "Personnel");
        this.prsnlService = service;
    }

    // ==============================
    // CRUD de base
    // ==============================
    @Override
    @GetMapping
    public Mono<ResponseEntity<Response>> findAll() {
        logger.info("📋 Récupération de tous les membres du personnel");
        return exceptionHandler.handleFlux(prsnlService.findAll());
    }
    
    @GetMapping("/partenaires")
    public Mono<ResponseEntity<Response>> findAllPartenaire(@RequestParam UUID resto ) {
        logger.info("📋 Récupération de tous les partenaires ");
        return exceptionHandler.handleFlux(prsnlService.findPartenaires(resto,true));
    }
    @GetMapping("/livreurs/{resto}")
    public Mono<ResponseEntity<Response>> findAllLivreur(@PathVariable UUID resto) {
        logger.info("📋 Récupération de tous les livreurs du personnel");
        return exceptionHandler.handleFlux(prsnlService.findLivreurs(resto));
    }

    @Override
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Response>> findById(@PathVariable UUID id) {
        logger.info("🔍 Recherche du membre du personnel ID: {}", id);
        return exceptionHandler.handleMono(prsnlService.findById(id))
                .doOnSuccess(r -> logger.info("✅ Membre trouvé ID: {}", id));
    }

    @Override
    @PostMapping
    public Mono<ResponseEntity<Response>> create(@Valid @RequestBody PrsnlRequest prsnlRequest) {
        logger.info("➕ Création du membre du personnel: {} {}", prsnlRequest.getLastname(), prsnlRequest.getFirstname());
        return exceptionHandler.handleMono(prsnlService.save(prsnlRequest))
                .doOnSuccess(r -> logger.info("✅ Membre créé: {} {}", prsnlRequest.getLastname(), prsnlRequest.getFirstname()));
    }

    @Override
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Response>> update(@PathVariable UUID id, @Valid @RequestBody PrsnlRequest prsnlRequest) {
        logger.info("✏️ Mise à jour du membre du personnel ID: {}", id);
        return exceptionHandler.handleMono(prsnlService.update(id, prsnlRequest))
                .doOnSuccess(r -> logger.info("✅ Mise à jour réussie ID: {}", id));
    }

    @Override
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Response>> delete(@PathVariable UUID id) {
        logger.info("🗑️ Suppression du membre du personnel ID: {}", id);
        return exceptionHandler.handleMono(prsnlService.delete(id))
                .doOnSuccess(r -> logger.info("✅ Suppression réussie ID: {}", id));
    }  
    
    

    // ==============================
    // LOGIN
    // ==============================
    @PostMapping("/login")
    public Mono<ResponseEntity<Response>> login(@Valid @RequestBody Login loginRequest) {
        logger.info("🔐 Tentative de connexion pour le téléphone: {}", loginRequest.getPhone());

        if (loginRequest.getPhone() == null || loginRequest.getPhone().trim().isEmpty()
                || loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()
                || loginRequest.getRestoCode() == null || loginRequest.getRestoCode().trim().isEmpty()) {
            return exceptionHandler.handleMono(
                    Mono.error(new IllegalArgumentException("Champs obligatoires manquants")));
        }

        return exceptionHandler.handleMono(prsnlService.login(loginRequest))
                .doOnSuccess(r -> logger.info("✅ Connexion réussie pour le téléphone: {}", loginRequest.getPhone()));
    }
 @Override
    public Mono<ResponseEntity<Response>> changeState(@PathVariable UUID id) {
        logger.info("🛠️ Changement d'état du prsnls ID : {}", id);
        return exceptionHandler.handleMono(prsnlService.changeState(id));
    }
 
 @Override
 @PostMapping("/search")
 public Mono<ResponseEntity<Response>> search(@RequestBody Map<String, Object> filters) {
     logger.info("🔍 Recherche {} avec pagination - filtres: {}", entityName, filters);
     return exceptionHandler.handleMono(prsnlService.search(filters,EnumFilter.NOTHING));
 }
   
}
