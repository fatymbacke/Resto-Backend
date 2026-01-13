package com.app.manage_restaurant.services.impl;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;

import com.app.manage_restaurant.cores.BaseServiceImpl;
import com.app.manage_restaurant.dtos.request.MenuCategoryRequest;
import com.app.manage_restaurant.dtos.request.MenusRequest;
import com.app.manage_restaurant.dtos.request.PaymentMethodRequest;
import com.app.manage_restaurant.dtos.response.MenuCategoryResponse;
import com.app.manage_restaurant.dtos.response.MenusResponse;
import com.app.manage_restaurant.dtos.response.PaymentMethodResponse;
import com.app.manage_restaurant.entities.Menus;
import com.app.manage_restaurant.entities.PaymentMethod;
import com.app.manage_restaurant.entities.MenuCategory;
import com.app.manage_restaurant.mapper.MenuCategoryMapper;
import com.app.manage_restaurant.mapper.MenuMapper;
import com.app.manage_restaurant.mapper.PaymentMethodMapper;
import com.app.manage_restaurant.repositories.MenuCategoryRepository;
import com.app.manage_restaurant.repositories.MenuRepository;
import com.app.manage_restaurant.repositories.PaymentMethodRepository;
import com.app.manage_restaurant.services.GenericDuplicateChecker;
import com.app.manage_restaurant.services.MenuCategoryService;
import com.app.manage_restaurant.services.MenuService;
import com.app.manage_restaurant.services.ModuleService;
import com.app.manage_restaurant.services.PaymentMethodService;
import com.app.manage_restaurant.utilities.FileStorageUtil;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import reactor.core.publisher.Mono;
@Service
public class PaymentMethodServiceImpl extends BaseServiceImpl<PaymentMethod, PaymentMethodRequest, PaymentMethodResponse, UUID> implements PaymentMethodService {
    private PaymentMethodRepository repository;
    private final Logger logger;
    protected final R2dbcEntityTemplate template; // 🔥 Pour la recherche dynamique
    public PaymentMethodServiceImpl(PaymentMethodRepository repository,    		
    		             FileStorageUtil fileStorageUtil,
    		             R2dbcEntityTemplate template,
                         ReactiveExceptionHandler exceptionHandler,
                         GenericDuplicateChecker duplicateChecker) {
        super(repository,fileStorageUtil,template,PaymentMethodMapper::toEntity,PaymentMethodMapper::toResponse,
        		PaymentMethod.class, "PaymentMethod", exceptionHandler, duplicateChecker);
        this.repository = repository;
        this.template = template;
        this.logger = LoggerFactory.getLogger(ModuleService.class);
    }

    @Override
    public Mono<Void> validate(PaymentMethodRequest request) {
        return Mono.fromRunnable(() -> {
            if (request.getName() == null || request.getName().trim().isEmpty())
                throw new IllegalArgumentException("Le nom du Menu est obligatoire");
        });
    }

    @Override
    public Map<String, Object> extractUniqueFields(PaymentMethodRequest request) {
        return Map.of(
            "name", request.getName()
        );
    }

    @Override
    public void applyRequestToEntity(PaymentMethod existing, PaymentMethodRequest request) {
    	PaymentMethodMapper.updateEntityFromRequest(request, existing);
    }

	@Override
	protected String getFileField(PaymentMethod entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setFileField(PaymentMethod entity, String filePath) {
		// TODO Auto-generated method stub
		
	}

    
    
}
