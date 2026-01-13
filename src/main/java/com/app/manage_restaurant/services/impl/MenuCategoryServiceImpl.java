package com.app.manage_restaurant.services.impl;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;

import com.app.manage_restaurant.cores.BaseServiceImpl;
import com.app.manage_restaurant.dtos.request.MenuCategoryRequest;
import com.app.manage_restaurant.dtos.response.MenuCategoryResponse;
import com.app.manage_restaurant.entities.MenuCategory;
import com.app.manage_restaurant.mapper.MenuCategoryMapper;
import com.app.manage_restaurant.repositories.MenuCategoryRepository;
import com.app.manage_restaurant.services.GenericDuplicateChecker;
import com.app.manage_restaurant.services.MenuCategoryService;
import com.app.manage_restaurant.services.ModuleService;
import com.app.manage_restaurant.utilities.FileStorageUtil;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import reactor.core.publisher.Mono;
@Service
public class MenuCategoryServiceImpl extends BaseServiceImpl<MenuCategory, MenuCategoryRequest, MenuCategoryResponse, UUID> implements MenuCategoryService {
    private MenuCategoryRepository repository;
    private final Logger logger;
    protected final R2dbcEntityTemplate template; // 🔥 Pour la recherche dynamique
    public MenuCategoryServiceImpl(MenuCategoryRepository repository,    		
    		             FileStorageUtil fileStorageUtil,
    		             R2dbcEntityTemplate template,
                         ReactiveExceptionHandler exceptionHandler,
                         GenericDuplicateChecker duplicateChecker) {
        super(repository,fileStorageUtil,template,MenuCategoryMapper::toEntity,MenuCategoryMapper::toResponse,
        		MenuCategory.class, "MenuCategorie", exceptionHandler, duplicateChecker);
        this.repository = repository;
        this.template = template;
        this.logger = LoggerFactory.getLogger(ModuleService.class);
    }

    @Override
    public Mono<Void> validate(MenuCategoryRequest request) {
        return Mono.fromRunnable(() -> {
            if (request.getName() == null || request.getName().trim().isEmpty())
                throw new IllegalArgumentException("Le nom du Menu est obligatoire");
        });
    }

    @Override
    public Map<String, Object> extractUniqueFields(MenuCategoryRequest request) {
        return Map.of(
            "name", request.getName()
        );
    }

    @Override
    public void applyRequestToEntity(MenuCategory existing, MenuCategoryRequest request) {
    	MenuCategoryMapper.updateEntityFromRequest(request, existing);
    }

	@Override
	protected String getFileField(MenuCategory entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setFileField(MenuCategory entity, String filePath) {
		// TODO Auto-generated method stub
		
	}

    
    
}
