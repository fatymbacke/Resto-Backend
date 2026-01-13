package com.app.manage_restaurant.repositories.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import com.app.manage_restaurant.cores.BaseRepositoryImpl;
import com.app.manage_restaurant.cores.EnumFilter;
import com.app.manage_restaurant.entities.Customer;
import com.app.manage_restaurant.entities.EnumPerson;
import com.app.manage_restaurant.entities.Person;
import com.app.manage_restaurant.entities.Prsnl;
import com.app.manage_restaurant.repositories.PrsnlRepository;
import com.app.manage_restaurant.security.SecurityUser;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class PrsnlRepositoryImpl extends BaseRepositoryImpl<Prsnl, UUID> implements PrsnlRepository {
    
    public PrsnlRepositoryImpl(R2dbcEntityTemplate template) {
        super(template, Prsnl.class);
    //    super.excludeMethodFromFiltering("findAll");

        
    }

    @Override
    public Mono<Person> findByPhoneAndRestoCodeAndType(String phone, String restoCode, EnumPerson type) {
        logger.debug("Finding Person by phone: {} and restoCode: {} and type: {}", phone, restoCode, type);
        
        UUID restoCodeConvert = restoCode != null ? UUID.fromString(restoCode) : null;
        
        return Mono.deferContextual(ctx -> {
            if (type == EnumPerson.CUSTOMER) {
                // Recherche dans la table customer
                Criteria criteria = Criteria.where("phone").is(phone);
                return template.select(Customer.class)
                             .matching(Query.query(criteria)) // Utiliser matching() au lieu de select(Query, Class)
                             .one() // Utiliser one() au lieu de next()
                             .cast(Person.class);
            } else {
            	Criteria criteria =null;
            	
            	if(restoCodeConvert !=null) {
            		 // Recherche dans la table prsnl
                     criteria = Criteria.where("phone").is(phone)
                                             .and("resto_code").is(restoCodeConvert);
            	}else {
            		 // Recherche dans la table prsnl
                     criteria = Criteria.where("phone").is(phone);
            	}               
                
                SecurityUser securityUser = ctx.getOrDefault("CURRENT_USER", null);
                if (securityUser != null && securityUser.getOwnerCode() != null) {
                    criteria = criteria.and("owner_code").is(securityUser.getOwnerCode());
                }
                
                return template.select(Prsnl.class)
                             .matching(Query.query(criteria))
                             .one()
                             .cast(Person.class);
            }
        })
        .doOnSuccess(person -> {
            if (person != null) {
                logger.debug("Found {} by phone: {} and type: {}", 
                    person.getClass().getSimpleName(), phone, type);
            } else {
                logger.debug("No {} found by phone: {}", 
                    type == EnumPerson.CUSTOMER ? "Customer" : "Prsnl", phone);
            }
        })
        .doOnError(error -> logger.error("Error finding {} by phone {}: {}", 
            type == EnumPerson.CUSTOMER ? "Customer" : "Prsnl", phone, error.getMessage(), error));
    }
    
    
    

    @Override
    public Mono<Long> counts() {
        logger.debug("Counting all Prsnl");
        
        return applyGlobalFilter(Query.empty(),EnumFilter.ALL)
                .flatMap(q -> template.select(q, Prsnl.class).count())
                .doOnSuccess(count -> logger.debug("Total Prsnl count: {}", count))
                .doOnError(error -> logger.error("Error counting Prsnl: {}", error.getMessage(), error));
    }

    @Override
    public Mono<Boolean> existsByPhoneAndRestoCode(String phone, String restoCode) {
        logger.debug("Checking existence of Prsnl by phone: {} and restoCode: {}", phone, restoCode);
        
        return Mono.deferContextual(ctx -> {
            // Convertir le restoCode string en UUID
            UUID restoCodeUUID;
            try {
                restoCodeUUID = UUID.fromString(restoCode);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid restoCode format: {}", restoCode);
                return Mono.just(false);
            }
            
            Criteria criteria = Criteria.where("phone").is(phone)
                                      .and("resto_code").is(restoCodeUUID);
            
            // Ajouter le filtrage owner_code si disponible dans le contexte
            SecurityUser securityUser = ctx.getOrDefault("CURRENT_USER", null);
            if (securityUser != null && securityUser.getOwnerCode() != null) {
                criteria = criteria.and("owner_code").is(securityUser.getOwnerCode());
            }
            
            return template.exists(Query.query(criteria), Prsnl.class);
        })
        .doOnSuccess(exists -> logger.debug("Existence check result for phone {} and restoCode {}: {}", 
            phone, restoCode, exists))
        .doOnError(error -> logger.error("Error checking existence by phone {} and restoCode {}: {}", 
            phone, restoCode, error.getMessage(), error));
    }

    // Méthode utilitaire supplémentaire pour compter avec filtres
    public Mono<Long> countByRestoCode(UUID restoCode) {
        logger.debug("Counting Prsnl by restoCode: {}", restoCode);
        
        return applyGlobalFilter(Query.query(Criteria.where("resto_code").is(restoCode)),EnumFilter.ALL)
                .flatMap(q -> template.select(q, Prsnl.class).count())
                .doOnSuccess(count -> logger.debug("Prsnl count for restoCode {}: {}", restoCode, count))
                .doOnError(error -> logger.error("Error counting Prsnl by restoCode {}: {}", 
                    restoCode, error.getMessage(), error));
    }

    // Méthode utilitaire pour trouver par phone avec sécurité
    public Mono<Prsnl> findByPhoneWithSecurity(String phone) {
        logger.debug("Finding Prsnl by phone with security: {}", phone);
        
        return applyGlobalFilter(Query.query(Criteria.where("phone").is(phone)),EnumFilter.ALL)
                .flatMap(q -> template.selectOne(q, Prsnl.class))
                .doOnSuccess(prsnl -> {
                    if (prsnl != null) {
                        logger.debug("Found Prsnl by phone with security: {}", phone);
                    } else {
                        logger.debug("No Prsnl found by phone with security: {}", phone);
                    }
                })
                .doOnError(error -> logger.error("Error finding Prsnl by phone with security {}: {}", 
                    phone, error.getMessage(), error));
    }
    

    @Override
    public Flux<Prsnl> findByResto(UUID resto,boolean active) {
        logger.debug("Finding Prsnl by resto with security: {}", resto);       
        
        return applyGlobalFilter(Query.query(Criteria.where("resto_code").is(resto)
                .and("active").is(active)),EnumFilter.NOTHING)
                .flatMapMany(q -> template.select(q, Prsnl.class));
                
    }
    

    
	@Override
	public Mono<Prsnl> update(Prsnl entity) {
		// TODO Auto-generated method stub
		logger.debug("UPDATE WITH EXISTENCE CHECK - ID: {}", entity.getId());
        return findExistingEntity(entity)
            .flatMap(existing -> {                
                logger.debug("ATTEMPTING UPDATE...");
                return template.update(existing);
            })
            .flatMap(updated -> {
                if (updated == null) {
                    logger.error("TEMPLATE.UPDATE RETURNED NULL");
                    return Mono.error(new RuntimeException("L'opération de mise à jour a échoué - résultat null"));
                }
                
                logger.debug("UPDATE SUCCESSFUL - ID: {}", updated.getId());
                logger.debug("UPDATED ENTITY: {}", updated.toString());
                return Mono.just(updated);
            })
            .onErrorResume(err -> {
                logger.error("UPDATE ERROR: {}", err.getMessage(), err);
                return handleUpdateError(err);
            });
	}

	@Override
	public Flux<Prsnl> search(Map<String, Object> filters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<Prsnl> findByUsernanme(String phone) {
		// TODO Auto-generated method stub
		
	        logger.debug("Finding Prsnl by phone: {}", phone);
	        return applyGlobalFilter(Query.query(Criteria.where("phone").is(phone)),EnumFilter.ALL)
	                .flatMap(q -> template.selectOne(q, Prsnl.class))
	                .doOnSuccess(prsnl -> {
	                    if (prsnl != null) {
	                        logger.debug("Found Prsnl by phone: {}", phone);
	                    } else {
	                        logger.debug("No Prsnl found by phone: {}", phone);
	                    }
	                })
	                .doOnError(error -> logger.error("Error finding Prsnl by phone {}: {}", 
	                    phone, error.getMessage(), error));
	    }
	
	
	// =====================================
		// Méthode de recherche avec résultat paginé
		// =====================================
		@Override
		public Mono<PageResponse<Prsnl>> searchWithPagination(Map<String, Object> filters,EnumFilter type) {
		    return Mono.zip(
		        super.search(filters, type).collectList(),
		        super.count(filters,type)
		    ).flatMap(tuple -> {
		    	
		        List<Prsnl> content = tuple.getT1();
		        long totalElements = tuple.getT2();		       
		        // Chargement parallèle des OrderItem
		        return Flux.fromIterable(content)
		            .collectList()
		            .map(enrichedOrders -> createPageResponse(enrichedOrders, filters, totalElements));
		    });
		}
	
		private PageResponse<Prsnl> createPageResponse(List<Prsnl> content, 
                Map<String, Object> filters, 
                long totalElements) {
			int page = (int) filters.getOrDefault("page", 0);
			int size = (int) filters.getOrDefault("size", 20);
			int totalPages = (int) Math.ceil((double) totalElements / size);
			
			return new PageResponse<>(
			content,
			page,
			size,
			totalElements,
			totalPages,
			page > 0,
			page < totalPages - 1
			);
			}
	
	
}