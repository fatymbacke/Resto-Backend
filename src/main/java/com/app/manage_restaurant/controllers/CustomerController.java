package com.app.manage_restaurant.controllers;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.manage_restaurant.cores.BaseController;
import com.app.manage_restaurant.cores.EnumFilter;
import com.app.manage_restaurant.dtos.request.CustomerRequest;
import com.app.manage_restaurant.dtos.response.CustomerResponse;
import com.app.manage_restaurant.dtos.response.Response;
import com.app.manage_restaurant.entities.Customer;
import com.app.manage_restaurant.services.CustomerService;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
@Tag(name = "Customers", description = "Gestion des clients")
public class CustomerController extends BaseController<Customer, CustomerRequest, CustomerResponse, UUID> {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService, ReactiveExceptionHandler exceptionHandler) {
        super(customerService, exceptionHandler, "Customers");
        this.customerService = customerService;
    }
    
 // ==============================
    // SEARCH AVEC PAGINATION
    // ==============================
    @Override
    public Mono<ResponseEntity<Response>> search(@RequestBody Map<String, Object> filters) {
    	
    	UUID restoCode =UUID.fromString((String) filters.get("restoCode.eq")) ;
        logger.info("🔍 Recherche Customer avec pagination - filtres: {} with resto {}", filters,restoCode);
        return exceptionHandler.handleMono(customerService.search(restoCode,filters,EnumFilter.ALL));
    }
    

    
}
