package com.app.manage_restaurant.services;

import java.util.Map;
import java.util.UUID;

import com.app.manage_restaurant.cores.BaseService;
import com.app.manage_restaurant.cores.EnumFilter;
import com.app.manage_restaurant.cores.BaseServiceImpl.PageResponse;
import com.app.manage_restaurant.dtos.request.CustomerRequest;
import com.app.manage_restaurant.dtos.response.CustomerResponse;
import com.app.manage_restaurant.entities.Customer;

import reactor.core.publisher.Mono;

public interface CustomerService extends BaseService<Customer, CustomerRequest, CustomerResponse, UUID> {  

	public Mono<PageResponse<CustomerResponse>> search(UUID restoCode, Map<String, Object> filters,EnumFilter type);
   

}