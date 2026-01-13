package com.app.manage_restaurant.services.impl;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;

import com.app.manage_restaurant.cores.BaseServiceImpl;
import com.app.manage_restaurant.cores.EnumFilter;
import com.app.manage_restaurant.dtos.request.CustomerRequest;
import com.app.manage_restaurant.dtos.response.CustomerResponse;
import com.app.manage_restaurant.entities.Customer;
import com.app.manage_restaurant.mapper.CustomerMapper;
import com.app.manage_restaurant.repositories.CustomerRepository;
import com.app.manage_restaurant.services.CustomerService;
import com.app.manage_restaurant.services.GenericDuplicateChecker;
import com.app.manage_restaurant.services.TablesService;
import com.app.manage_restaurant.utilities.FileStorageUtil;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import reactor.core.publisher.Mono;

@Service
public class CustomerServiceImpl extends BaseServiceImpl<Customer, CustomerRequest, CustomerResponse, UUID> implements CustomerService{

    private final CustomerRepository repository;
    private final Logger logger; 
    protected final R2dbcEntityTemplate template; // 🔥 Pour la recherche dynamique

    public CustomerServiceImpl(CustomerRepository repository,
    		            R2dbcEntityTemplate template,
    		             FileStorageUtil fileStorageUtil,
                        ReactiveExceptionHandler exceptionHandler,
                        GenericDuplicateChecker duplicateChecker) {
        super(repository,fileStorageUtil,template, CustomerMapper::toEntity, CustomerMapper::toResponse,
        		Customer.class, "Customer", exceptionHandler, duplicateChecker);
        this.repository = repository;
        this.template = template;
        this.logger = LoggerFactory.getLogger(TablesService.class);
    }

	@Override
	public Mono<Void> validate(CustomerRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> extractUniqueFields(CustomerRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	protected String getFileField(Customer entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setFileField(Customer entity, String filePath) {
		// TODO Auto-generated method stub
		
	}   
	 @Override
	    public Mono<PageResponse<CustomerResponse>> search(UUID restoCode, Map<String, Object> filters,EnumFilter type) {
	        logger.debug("Searching {} with pagination - filters: {}", entityName, filters);
	        
	        return repository.searchWithPagination(restoCode,filters,type)
	                .map(page -> new PageResponse<>(
	                    page.getContent().stream()
	                        .map(responseMapper::apply)
	                        .collect(java.util.stream.Collectors.toList()),
	                    page.getCurrentPage(),
	                    page.getPageSize(),
	                    page.getTotalElements(),
	                    page.getTotalPages(),
	                    page.isHasPrevious(),
	                    page.isHasNext()
	                ))
	                .doOnSuccess(result -> logger.debug("Pagination search completed for {} - {} results on page {}/{}", 
	                    entityName, result.getContent().size(), result.getCurrentPage() + 1, result.getTotalPages()))
	                .doOnError(error -> logger.error("Error in pagination search for {} with filters {}: {}", 
	                    entityName, filters, error.getMessage(), error));
	    }
    

}