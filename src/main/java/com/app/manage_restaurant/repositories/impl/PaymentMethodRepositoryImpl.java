package com.app.manage_restaurant.repositories.impl;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.app.manage_restaurant.cores.BaseRepositoryImpl;
import com.app.manage_restaurant.entities.PaymentMethod;
import com.app.manage_restaurant.repositories.PaymentMethodRepository;

import reactor.core.publisher.Flux;

@Repository
public class PaymentMethodRepositoryImpl extends BaseRepositoryImpl<PaymentMethod, UUID> implements PaymentMethodRepository {
    
    private final DatabaseClient databaseClient;
    
    public PaymentMethodRepositoryImpl(R2dbcEntityTemplate template) {
        super(template, PaymentMethod.class);
        this.databaseClient = template.getDatabaseClient();
    }

	@Override
	public Flux<PaymentMethod> search(Map<String, Object> filters) {
		// TODO Auto-generated method stub
		return null;
	}
    
    

}