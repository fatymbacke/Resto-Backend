package com.app.manage_restaurant.services;

import java.util.UUID;

import org.springframework.http.codec.multipart.FilePart;

import com.app.manage_restaurant.cores.BaseService;
import com.app.manage_restaurant.dtos.request.MenusAvailableRequest;
import com.app.manage_restaurant.dtos.request.MenusRequest;
import com.app.manage_restaurant.dtos.response.MenusResponse;
import com.app.manage_restaurant.entities.Menus;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MenuService extends BaseService<Menus, MenusRequest, MenusResponse, UUID>{
	Mono<MenusResponse> saveMenu(MenusRequest request, Mono<FilePart> file, String folder);
    Mono<MenusResponse> updateMenu(UUID id, MenusRequest request, Mono<FilePart> file, String folder);
	public Mono<Menus> loadMenuWithCollections(Menus menu);
    public Flux<MenusResponse> findAllMenus(boolean active);
    Mono<MenusResponse> toggleMenuAvailability(MenusAvailableRequest request);

	
}
