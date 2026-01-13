package com.app.manage_restaurant.controllers;

import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.manage_restaurant.cores.BaseController;
import com.app.manage_restaurant.dtos.request.MenuCategoryRequest;
import com.app.manage_restaurant.dtos.response.MenuCategoryResponse;
import com.app.manage_restaurant.entities.MenuCategory;
import com.app.manage_restaurant.services.MenuCategoryService;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;
@RestController
@RequestMapping("/api/menucategories")
@CrossOrigin(origins = "*")
public class MenuCategoryController extends BaseController<MenuCategory, MenuCategoryRequest, MenuCategoryResponse, UUID> {    
    private MenuCategoryService menuCategory;
    private final ReactiveExceptionHandler exceptionHandler;
    public MenuCategoryController( MenuCategoryService menuCategory, ReactiveExceptionHandler exceptionHandler) {
        super(menuCategory, exceptionHandler, "MenuCategorie");
        this.menuCategory = menuCategory;
        this.exceptionHandler = exceptionHandler;
    }
    
}