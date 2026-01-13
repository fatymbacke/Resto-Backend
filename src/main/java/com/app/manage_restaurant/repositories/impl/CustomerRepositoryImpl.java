package com.app.manage_restaurant.repositories.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;

import com.app.manage_restaurant.cores.BaseRepositoryImpl;
import com.app.manage_restaurant.cores.EnumFilter;
import com.app.manage_restaurant.entities.Customer;
import com.app.manage_restaurant.entities.EnumPerson;
import com.app.manage_restaurant.mapper.CustomerMapper;
import com.app.manage_restaurant.repositories.CustomerRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class CustomerRepositoryImpl extends BaseRepositoryImpl<Customer, UUID> implements CustomerRepository {

    private final DatabaseClient databaseClient;
    
    public CustomerRepositoryImpl(R2dbcEntityTemplate template, DatabaseClient databaseClient) {
        super(template, Customer.class);
        this.databaseClient = databaseClient;
        excludeMethodFromFiltering("findByPhone");
    }

    @Override
    public Mono<Customer> findByPhone(String phone) {
        return applyGlobalFilter(Query.query(Criteria.where("phone").is(phone)), "findByPhone", EnumFilter.ALL)
                .flatMap(query -> template.selectOne(query, Customer.class));
    }

    @Override
    public Mono<Customer> findByPhoneAndRestoCode(String phone, UUID restoCode) {
        Criteria criteria = Criteria.where("phone").is(phone)
                                  .and("resto_code").is(restoCode);
        return applyGlobalFilter(Query.query(criteria), "findByPhoneAndRestoCode", EnumFilter.ALL)
                .flatMap(query -> template.selectOne(query, Customer.class));
    }
    
    @Override
    public Mono<Customer> save(Customer entity) {    	
        logger.debug("ENTITY TO SAVE - Phone: {}, Name: {}", entity.getPhone(), entity.getPhone());   
        entity.setRole(EnumPerson.CUSTOMER.name());
        return findByPhone(entity.getPhone())
            .flatMap(existing -> updateExistingCustomer(existing, entity))
            .switchIfEmpty(Mono.defer(() -> createNewCustomer(entity)))
            .onErrorResume(this::handleSaveError);
    }

    private Mono<Customer> updateExistingCustomer(Customer existing, Customer newData) {
        logger.debug("UPDATING EXISTING CUSTOMER - ID: {}, Phone: {}", existing.getId(), existing.getPhone());        
        // Mettre à jour les champs nécessaires
        CustomerMapper.updateEntity(newData, existing);        
        return template.update(existing)
            .flatMap(updated -> {
                if (updated == null) {
                    logger.error("UPDATE RETURNED NULL FOR CUSTOMER ID: {}", existing.getId());
                    return Mono.error(new RuntimeException("L'opération de mise à jour a échoué - résultat null"));
                }
                
                logger.debug("UPDATE SUCCESSFUL - ID: {}", updated.getId());
                logger.debug("UPDATED CUSTOMER: {}", updated);
                return Mono.just(updated);
            });
    }

    private Mono<Customer> createNewCustomer(Customer entity) {
        logger.debug("CREATING NEW CUSTOMER - Phone: {}", entity.getPhone());
        
        return template.insert(entity)
            .flatMap(created -> {
                if (created == null) {
                    logger.error("INSERT RETURNED NULL FOR NEW CUSTOMER");
                    return Mono.error(new RuntimeException("L'opération de création a échoué - résultat null"));
                }
                
                logger.debug("CREATION SUCCESSFUL - ID: {}", created.getId());
                logger.debug("CREATED CUSTOMER: {}", created);
                return Mono.just(created);
            });
    }

    private Mono<Customer> handleSaveError(Throwable error) {
        logger.error("SAVE OPERATION FAILED: {}", error.getMessage(), error);
        
        if (error instanceof RuntimeException) {
            return Mono.error(error);
        }
        
        return Mono.error(new RuntimeException("Erreur lors de la sauvegarde du client: " + error.getMessage(), error));
    }

    @Override
    public Flux<Customer> search(Map<String, Object> filters) {
        return search(filters, EnumFilter.ALL);
    }

    @Override
    public Flux<Customer> findAll() {
        return applyGlobalFilter(Query.empty(), "findAll", EnumFilter.ALL)
                .flatMapMany(query -> template.select(query, Customer.class));
    }
    
    // =====================================
    // Méthode de recherche avec résultat paginé
    // =====================================
    @Override
    public Mono<PageResponse<Customer>> searchWithPagination(UUID restoCode,Map<String, Object> filters, EnumFilter type) {
        return Mono.zip(
            search(restoCode,filters, type).collectList(),
            count(restoCode,filters, type)
        ).map(tuple -> {
            List<Customer> content = tuple.getT1();
            long totalElements = tuple.getT2();
            
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
        });
    }
    
    // =====================================
    // RECHERCHE AVEC PAGINATION - Utilisant R2dbcEntityTemplate
    // =====================================
    @Override
    public Flux<Customer> search(UUID restoCode,Map<String, Object> filters, EnumFilter type) {
        return Flux.deferContextual(ctx -> {           
            
            // Étape 1: Récupérer les IDs des customers qui ont des orders avec les bons codes
            Mono<List<UUID>> customerIdsWithOrders = getCustomerIdsWithOrders(restoCode);
            
            return customerIdsWithOrders.flatMapMany(customerIds -> {
                if (customerIds.isEmpty()) {
                    return Flux.empty();
                }
                
                // Étape 2: Construire la requête pour les customers avec les IDs filtrés
                Criteria criteria = Criteria.where("id").in(customerIds);                
                
                
                // Appliquer les filtres supplémentaires
                for (Map.Entry<String, Object> entry : filters.entrySet()) {
                    String field = entry.getKey();
                    Object value = entry.getValue();
                    if (value == null) continue;
                    
                    if (isPaginationParameter(field)) {
                        continue;
                    }
                    
                    applyCriteriaFilter(criteria, field, value);
                }
                
                // Préparer la requête avec pagination et tri
                int page = (int) filters.getOrDefault("page", 0);
                int size = (int) filters.getOrDefault("size", 20);
                String sortField = (String) filters.getOrDefault("sort", "createdDate");
                String sortDirection = (String) filters.getOrDefault("direction", "desc");
                
                sortField = validateSortField(sortField);
                Sort sort = sortDirection.equalsIgnoreCase("desc") 
                    ? Sort.by(sortField).descending() 
                    : Sort.by(sortField).ascending();
                
                Query query = Query.query(criteria)
                    .with(PageRequest.of(page, size, sort));
                
                // Exécuter la requête
                return template.select(query, Customer.class);
            });
        });
    }
    
    // =====================================
    // Méthode pour compter les résultats
    // =====================================
    @Override
    public Mono<Long> count(UUID restoCode,Map<String, Object> filters, EnumFilter type) {
        return Mono.deferContextual(ctx -> {
            
           
            
            // Étape 1: Récupérer les IDs des customers qui ont des orders avec les bons codes
            Mono<List<UUID>> customerIdsWithOrders = getCustomerIdsWithOrders(restoCode);
            
            return customerIdsWithOrders.flatMap(customerIds -> {
                if (customerIds.isEmpty()) {
                    return Mono.just(0L);
                }
                
                // Étape 2: Construire la requête de count avec les IDs filtrés
                Criteria criteria = Criteria.where("id").in(customerIds);
                
               
                // Appliquer les mêmes filtres que la recherche
                for (Map.Entry<String, Object> entry : filters.entrySet()) {
                    String field = entry.getKey();
                    Object value = entry.getValue();
                    if (value == null) continue;
                    
                    if (isPaginationParameter(field)) {
                        continue;
                    }
                    
                    applyCriteriaFilter(criteria, field, value);
                }
                
                // Exécuter le count
                return template.count(Query.query(criteria), Customer.class);
            });
        });
    }
    
    // =====================================
    // Méthode utilitaire pour récupérer les IDs des customers avec orders
    // =====================================
    private Mono<List<UUID>> getCustomerIdsWithOrders(UUID restoCode) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT DISTINCT customer_id FROM orders WHERE 1=1 ");
        
        // Utiliser des paramètres nommés pour éviter les problèmes d'index
        if (restoCode != null) {
            sqlBuilder.append("AND resto_code = :resto_code ");
            return databaseClient.sql(sqlBuilder.toString())
                .bind("resto_code", restoCode)
                .map((row, metadata) -> row.get("customer_id", UUID.class))
                .all()
                .collectList();
        }   else {
            // Si les deux sont null, retourner tous les customers avec orders
            sqlBuilder.append("AND 1=1");
            return databaseClient.sql(sqlBuilder.toString())
                .map((row, metadata) -> row.get("customer_id", UUID.class))
                .all()
                .collectList();
        }
    }
    
    // =====================================
    // Méthode utilitaire pour appliquer les filtres aux critères
    // =====================================
    private Criteria applyCriteriaFilter(Criteria criteria, String field, Object value) {
        if (field.endsWith(".like") && value instanceof String str && !str.isBlank()) {
            String actualField = field.substring(0, field.length() - 5);
            // Utiliser ilike pour PostgreSQL (case insensitive)
            return criteria.and(actualField).like("%" + str + "%").ignoreCase(true);
        }
        else if (field.endsWith(".eq") && value != null) {
            String actualField = field.substring(0, field.length() - 3);
            return criteria.and(actualField).is(value);
        }
        else if (field.endsWith(".in") && value instanceof Collection) {
            String actualField = field.substring(0, field.length() - 3);
            return criteria.and(actualField).in((Collection<?>) value);
        }
        else if (field.endsWith(".gte") && value != null) {
            String actualField = field.substring(0, field.length() - 4);
            return criteria.and(actualField).greaterThanOrEquals(value);
        }
        else if (field.endsWith(".lte") && value != null) {
            String actualField = field.substring(0, field.length() - 4);
            return criteria.and(actualField).lessThanOrEquals(value);
        }
        else if (value instanceof String str && !str.isBlank()) {
            return criteria.and(field).like("%" + str + "%").ignoreCase(true);
        } else {
            return criteria.and(field).is(value);
        }
    }
    
    // =====================================
    // Méthodes utilitaires
    // =====================================
    
    private String convertFieldToColumn(String field) {
        if (field == null || field.isEmpty()) {
            return field;
        }
        return field.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
    
    public String validateSortField(String sortField) {
        Set<String> allowedSortFields = new HashSet<>(Arrays.asList(
            "id", "firstname", "lastname", "email", "phone", 
            "createdDate", "updatedDate", "ownerCode", "restoCode"
        ));
        
        return allowedSortFields.contains(sortField) ? sortField : "createdDate";
    }
    
    public boolean isPaginationParameter(String field) {
        return field.equals("page") || field.equals("size") || 
               field.equals("sort") || field.equals("direction");
    }
    
    public boolean isSuperAdminRole(String role) {
        return "SUPER_ADMIN".equals(role);
    }
    
    public boolean isAdminRole(String role) {
        return "ADMIN".equals(role) || "MANAGER".equals(role);
    }
    
    public boolean hasCreatedByField() {
        return true;
    }
}