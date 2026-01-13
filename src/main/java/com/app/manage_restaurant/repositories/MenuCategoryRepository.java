package com.app.manage_restaurant.repositories;

import java.util.UUID;

import org.springframework.data.repository.NoRepositoryBean;

import com.app.manage_restaurant.cores.BaseRepository;
import com.app.manage_restaurant.entities.MenuCategory;
@NoRepositoryBean
public interface MenuCategoryRepository extends BaseRepository<MenuCategory, UUID> {
    
    
}
