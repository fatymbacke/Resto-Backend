package com.app.manage_restaurant.utilities;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.app.manage_restaurant.dtos.response.Response;
import com.app.manage_restaurant.exceptions.entities.EntityAlreadyExistsException;
import com.app.manage_restaurant.exceptions.entities.EntityCredentialsException;
import com.app.manage_restaurant.exceptions.entities.EntityNotFoundException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ReactiveExceptionHandler {

    // ==============================
    // Mono wrapper
    // ==============================
    public <T> Mono<ResponseEntity<Response>> handleMono(Mono<T> mono) {
        return mono.map(data -> ResponseEntity.ok(new Response(200, "Succès", data)))
                   .onErrorResume(this::handleException);
    }

    // ==============================
    // Flux wrapper
    // ==============================
    public <T> Mono<ResponseEntity<Response>> handleFlux(Flux<T> flux) {
        return flux.collectList()
                   .map(list -> ResponseEntity.ok(new Response(200, "Succès", list)))
                   .onErrorResume(this::handleException);
    }

    // ==============================
    // Gestion centralisée des exceptions
    // ==============================
    private Mono<ResponseEntity<Response>> handleException(Throwable ex) {
        if (ex instanceof EntityAlreadyExistsException) {
            return handleEntityAlreadyExists((EntityAlreadyExistsException) ex);
        }
        if (ex instanceof EntityNotFoundException) {
            return handleEntityNotFound((EntityNotFoundException) ex);
        }
        if (ex instanceof IllegalArgumentException) {
            return handleIllegalArgument((IllegalArgumentException) ex);
        }
        if (ex instanceof EntityCredentialsException) {
            return handleCredentials((EntityCredentialsException) ex);
        }
        
        
        // Gestion globale
        return handleAll(ex);
    }

    // ==============================
    // EntityAlreadyExistsException → HTTP 409
    // ==============================
    private Mono<ResponseEntity<Response>> handleEntityAlreadyExists(EntityAlreadyExistsException ex) {
        Map<String, Object> identifiers = ex.getIdentifiers() != null ? ex.getIdentifiers() : Map.of("entity", ex.getEntityType());
        Response resp = new Response(HttpStatus.CONFLICT.value(), "Il existe déjà "+ex.getEntityType() +" avec : "+identifiers.entrySet().iterator().next().getValue(), identifiers.entrySet().iterator().next().getValue());
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(resp));
    }

    // ==============================
    // EntityNotFoundException → HTTP 404
    // ==============================
    private Mono<ResponseEntity<Response>> handleEntityNotFound(EntityNotFoundException ex) {
        Response resp = new Response(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp));
    }
     // ==============================
    // EntityNotFoundException → HTTP 406
    // ==============================
    private Mono<ResponseEntity<Response>> handleCredentials(EntityCredentialsException ex) {
        Response resp = new Response(HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage(), null);
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(resp));
    }

    // ==============================
    // IllegalArgumentException → HTTP 400
    // ==============================
    private Mono<ResponseEntity<Response>> handleIllegalArgument(IllegalArgumentException ex) {
        Response resp = new Response(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp));
    }

    // ==============================
    // Autres erreurs → HTTP 500
    // ==============================
    private Mono<ResponseEntity<Response>> handleAll(Throwable ex) {  
    	System.out.println(ex.getMessage());
        Response resp = new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erreur interne", null);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp));
    }
}
