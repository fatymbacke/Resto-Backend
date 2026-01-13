package com.app.manage_restaurant.repositories.impl;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.app.manage_restaurant.cores.BaseRepositoryImpl;
import com.app.manage_restaurant.cores.EnumFilter;
import com.app.manage_restaurant.entities.Tables;
import com.app.manage_restaurant.repositories.TablesRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class TablesRepositoryImpl extends BaseRepositoryImpl<Tables, UUID> implements TablesRepository {
    
    private final DatabaseClient databaseClient;
    
    public TablesRepositoryImpl(R2dbcEntityTemplate template) {
        super(template, Tables.class);
        this.databaseClient = template.getDatabaseClient();
    }

	@Override
	public Flux<Tables> search(Map<String, Object> filters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<Tables> findByResto(UUID resto_code) {
        logger.debug("Finding Tables by resto_code: {}", resto_code);
        
        return applyGlobalFilter(Query.query(Criteria.where("resto_code").is(resto_code)), "findByResto",EnumFilter.ALL)
                .flatMapMany(query -> template.select(query, Tables.class))
                .doOnComplete(() -> logger.debug("Completed finding Tables by resto_code: {}", resto_code));
    }

	@Override
	public Flux<Tables> findByRestoAndStatus(UUID resto_code, String status)  {
        logger.debug("Finding Tables by resto_code: {}", resto_code);
        
        return applyGlobalFilter(Query.query(Criteria.where("resto_code").is(resto_code).and("status").is(status)), "findByResto",EnumFilter.ALL)
                .flatMapMany(query -> template.select(query, Tables.class))
                .doOnComplete(() -> logger.debug("Completed finding Tables by resto_code: {}", resto_code));
    }

	@Override
	public Mono<Tables> findByRestoAndCapacity(UUID restoCode, Integer capacity) {
        logger.debug("Finding Tables by resto_code: {} and capacity :{}", restoCode,capacity);
        
        return applyGlobalFilter(Query.query(Criteria.where("resto_code").is(restoCode).and("capacity").is(capacity)), "findByResto",EnumFilter.ALL)
                .flatMap(query -> template.selectOne(query, Tables.class));
    }
    

}