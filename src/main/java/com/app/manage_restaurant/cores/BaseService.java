package com.app.manage_restaurant.cores;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.codec.multipart.FilePart;

import com.app.manage_restaurant.cores.BaseServiceImpl.PageResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BaseService<E, RQ, RS, ID> {

    Mono<RS> save(RQ request);
    Mono<RS> update(ID id, RQ request);

    public Mono<RS> save(RQ request,Mono<FilePart> files,String folderR);   

    public Mono<RS> update(ID id,RQ request,Mono<FilePart> file,String folder);   

    Mono<RS> delete(ID id);

    Mono<RS> findById(ID id);

    Mono<RS> existsById(ID id);

    Flux<RS> findAll();
    Flux<RS> findAllActive(Boolean active,EnumFilter type);
    /**
     * Traitement d'un seul fichier avec gestion d'erreur améliorée
     */
    public Mono<String> processSingleFile(Mono<FilePart> fileMono, String folder);
 // Recherche avec pagination
    Mono<PageResponse<RS>> search(Map<String, Object> filters,EnumFilter type);
    
    // Recherche simple (sans pagination) - optionnel
    Flux<RS> searchAll(Map<String, Object> filters,EnumFilter type);
    
    Mono<RS> changeState(ID id);   
    public Mono<RS> createWithFiles(
    		RQ request,
            Mono<FilePart> logoMono,
            Mono<FilePart> coverMono);
    public Mono<RS> updateWithFiles(UUID id,
    		RQ request,
            Mono<FilePart> logoMono,
            Mono<FilePart> coverMono) ;
    // Méthodes à implémenter pour validation et unicité
    Mono<Void> validate(RQ request);

}
