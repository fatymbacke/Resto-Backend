package com.app.manage_restaurant.services;

import java.util.UUID;

import com.app.manage_restaurant.cores.BaseService;
import com.app.manage_restaurant.dtos.request.Login;
import com.app.manage_restaurant.dtos.request.PrsnlRequest;
import com.app.manage_restaurant.dtos.response.PartenaireResponse;
import com.app.manage_restaurant.dtos.response.PrsnlResponse;
import com.app.manage_restaurant.entities.EnumPerson;
import com.app.manage_restaurant.entities.Person;
import com.app.manage_restaurant.entities.Prsnl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PrsnlService extends BaseService<Prsnl, PrsnlRequest, PrsnlResponse, UUID> {  


    // ==============================
    // LOGIN - VERSION ADAPTÉE AVEC GÉNÉRATION DU TOKEN COMPLET
    // ==============================
    public Mono<String> login(Login login) ;

    public Mono<Void> validateLoginRequest(Login login) ;

    public Mono<Person> authenticateUser(Login login, Person user);

    // ==============================
    // GÉNÉRATION DU TOKEN JWT AVEC RESTOCODE ET OWNERCODE
    // ==============================
    public Mono<String> generateJwtTokenWithCodes(Person user, Login login);
    // ==============================
    // EXTRACTION DE L'OWNERCODE
    // ==============================
    public UUID extractOwnerCodeFromUser(Person user) ;

    // ==============================
    // MÉTHODE DE GÉNÉRATION DE TOKEN POUR USAGE EXTERNE
    // ==============================
    public Mono<String> generateToken(Person user,UUID restoCode, UUID ownerCode,EnumPerson type);
    // ==============================
    // CHANGE STATE
    // ==============================
    public Mono<PrsnlResponse> changeState(UUID id);

    
	public Flux<PrsnlResponse> findLivreurs(UUID resto);

	public Flux<PartenaireResponse> findPartenaires(UUID resto,boolean active);

}