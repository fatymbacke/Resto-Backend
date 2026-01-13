package com.app.manage_restaurant.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.manage_restaurant.dtos.request.Login;
import com.app.manage_restaurant.dtos.response.Response;
import com.app.manage_restaurant.services.PrsnlService;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "Gestion des authentifications des utilisateurs")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final PrsnlService prsnlService;
    private final ReactiveExceptionHandler exceptionHandler;


	public AuthController(PrsnlService prsnlService, ReactiveExceptionHandler exceptionHandler) {
        this.prsnlService = prsnlService;
        this.exceptionHandler = exceptionHandler;
    }

    @PostMapping("/login")
    @Operation(summary = "Authentifier un utilisateur")
    @ApiResponse(responseCode = "200", description = "Authentification réussie")
    public Mono<ResponseEntity<Response>> login(@RequestBody @Valid Login login) {
        logger.info("🔐 Tentative de connexion pour le téléphone : {} dans le restaurant : {}",  login.getPhone(), login.getRestoCode());

        // handleMono s'occupe de la gestion des exceptions et retourne Mono<ResponseEntity<Response>>
        return exceptionHandler.handleMono(prsnlService.login(login));
    }

   

    @GetMapping("/health")
    @Operation(summary = "Vérifier l'état du service d'authentification")
    public Mono<ResponseEntity<Response>> healthCheck() {
        Response healthResponse = new Response(200, "Service opérationnel", "OK");
        return Mono.just(healthResponse)
                .flatMap(resp -> exceptionHandler.handleMono(Mono.just(resp)));
    }
}
