package com.app.manage_restaurant.cores;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;

import com.app.manage_restaurant.dtos.response.Response;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

public abstract class BaseController<E, RQ, RS, ID> {

    protected final BaseService<E, RQ, RS, ID> service;
    protected final ReactiveExceptionHandler exceptionHandler;
    protected final Logger logger;
    protected final String entityName;

    protected BaseController(BaseService<E, RQ, RS, ID> service,
                             ReactiveExceptionHandler exceptionHandler,
                             String entityName) {
        this.service = service;
        this.exceptionHandler = exceptionHandler;
        this.entityName = entityName;
        this.logger = LoggerFactory.getLogger(getClass());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Response>> findById(@PathVariable ID id) {
        return exceptionHandler.handleMono(service.findById(id));
    }

    @GetMapping
    public Mono<ResponseEntity<Response>> findAll() {
        return exceptionHandler.handleFlux(service.findAll());
    }

    @PostMapping
    public Mono<ResponseEntity<Response>> create(@RequestBody @Valid RQ dto) {
        return exceptionHandler.handleMono(service.save(dto));
    }
    
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Response>> update(@PathVariable ID id, @RequestBody @Valid RQ dto) {
        return exceptionHandler.handleMono(service.update(id, dto));
    }
    
    // ==============================
    // CREATE / UPDATE avec fichiers
    // ==============================
    @PostMapping("/withFile/{folder}")
    public Mono<ResponseEntity<Response>> createWithFile(
    		@PathVariable String folder,
            @RequestPart("data") @Valid  RQ request,
            @RequestPart(value = "file", required = false) Mono<FilePart> file) {
        return exceptionHandler.handleMono(service.save(request,file,folder));
    }

    @PutMapping("/withFile/{folder}/{id}")
    @Transactional
    public Mono<ResponseEntity<Response>> updateWithFile(
    		@PathVariable ID id,
    		@PathVariable String folder,
            @RequestPart("data") @Valid  RQ request,
            @RequestPart(value = "file", required = false) Mono<FilePart> file) {
        return exceptionHandler.handleMono(service.update(id,request,file,folder));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Response>> delete(@PathVariable ID id) {
        return exceptionHandler.handleMono(service.delete(id));
    }

    // ==============================
    // CHANGE STATE (ACTIVE/INACTIVE)
    // ==============================
    @PutMapping("/{id}/changestate")
    public Mono<ResponseEntity<Response>> changeState(@PathVariable ID id) {
        return exceptionHandler.handleMono(service.changeState(id));
    }
    
    // ==============================
    // SEARCH AVEC PAGINATION
    // ==============================
    @PostMapping("/search")
    public Mono<ResponseEntity<Response>> search(@RequestBody Map<String, Object> filters) {
        logger.info("🔍 Recherche {} avec pagination - filtres: {}", entityName, filters);
        return exceptionHandler.handleMono(service.search(filters,EnumFilter.ALL));
    }

    // ==============================
    // SEARCH SANS PAGINATION (optionnel)
    // ==============================
    @PostMapping("/search-all")
    public Mono<ResponseEntity<Response>> searchAll(@RequestBody Map<String, Object> filters) {
        logger.info("🔍 Recherche complète {} - filtres: {}", entityName, filters);
        return exceptionHandler.handleFlux(service.searchAll(filters,EnumFilter.ALL));
    }
}