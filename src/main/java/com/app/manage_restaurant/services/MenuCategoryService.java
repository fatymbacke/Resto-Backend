package com.app.manage_restaurant.services;

import java.util.UUID;

import com.app.manage_restaurant.cores.BaseService;
import com.app.manage_restaurant.dtos.request.MenuCategoryRequest;
import com.app.manage_restaurant.dtos.response.MenuCategoryResponse;
import com.app.manage_restaurant.entities.MenuCategory;

public interface MenuCategoryService extends BaseService<MenuCategory, MenuCategoryRequest, MenuCategoryResponse, UUID>{

}
