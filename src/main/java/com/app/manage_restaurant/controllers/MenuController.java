package com.app.manage_restaurant.controllers;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.app.manage_restaurant.cores.BaseController;
import com.app.manage_restaurant.cores.EnumFilter;
import com.app.manage_restaurant.dtos.request.MenusAvailableRequest;
import com.app.manage_restaurant.dtos.request.MenusRequest;
import com.app.manage_restaurant.dtos.response.MenusResponse;
import com.app.manage_restaurant.dtos.response.Response;
import com.app.manage_restaurant.entities.Menus;
import com.app.manage_restaurant.services.MenuService;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;
@RestController
@RequestMapping("/api/menus")
@CrossOrigin(origins = "*")
public class MenuController extends BaseController<Menus, MenusRequest, MenusResponse, UUID> {    
    private MenuService menuService;
    private final ReactiveExceptionHandler exceptionHandler;
    public MenuController( MenuService menuService, ReactiveExceptionHandler exceptionHandler) {
        super(menuService, exceptionHandler, "Menus");
        this.menuService = menuService;
        this.exceptionHandler = exceptionHandler;
    }
    
    @Override
    public Mono<ResponseEntity<Response>> createWithFile(
    		@PathVariable String folder,
            @RequestPart("data") @Valid  MenusRequest request,
            @RequestPart(value = "file", required = false) Mono<FilePart> file) {
        return exceptionHandler.handleMono(menuService.saveMenu(request,file,folder));
    }
    
    @Override
    public Mono<ResponseEntity<Response>> updateWithFile(
    		@PathVariable UUID id,
    		@PathVariable String folder,
            @RequestPart("data") @Valid  MenusRequest request,
            @RequestPart(value = "file", required = false) Mono<FilePart> file) {
    	request.setId(id);
        return exceptionHandler.handleMono(menuService.updateMenu(id,request,file,folder));
    }
    @Override
    public Mono<ResponseEntity<Response>> findAll() {
        return exceptionHandler.handleFlux(menuService.findAllMenus(true));
    }
    
    @Override
    public Mono<ResponseEntity<Response>> searchAll(Map<String, Object> filters) {
    	// TODO Auto-generated method stub
        return exceptionHandler.handleFlux(menuService.searchAll(filters,EnumFilter.ALL));
    }
    @PutMapping("/availability/{id}")
    public Mono<ResponseEntity<Response>> toggleMenuAvailability(
            @PathVariable UUID id,
            @RequestBody @Valid MenusAvailableRequest request) {
        
        logger.info("🛠️ Changement d'état du menu ID : {}", id);
        
        // Assurez-vous que l'ID du path est utilisé
        request.setId(id);
        
        return exceptionHandler.handleMono(menuService.toggleMenuAvailability(request));
    }
    
}