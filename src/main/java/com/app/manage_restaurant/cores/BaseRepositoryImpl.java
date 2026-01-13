package com.app.manage_restaurant.cores;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.transaction.annotation.Transactional;

import com.app.manage_restaurant.exceptions.entities.EntityNotFoundException;
import com.app.manage_restaurant.security.SecurityUser;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class BaseRepositoryImpl<T extends HasOwnerAndResto<UUID>, ID> implements BaseRepository<T, ID> {
    protected static final Logger logger = LoggerFactory.getLogger(BaseRepositoryImpl.class);    
    protected final R2dbcEntityTemplate template;
    protected final Class<T> entityClass;
    // Set pour stocker les méthodes à exclure du filtrage
    protected final Set<String> excludedMethods = ConcurrentHashMap.newKeySet();
    
    
    public BaseRepositoryImpl(R2dbcEntityTemplate template, Class<T> entityClass) {
        this.template = template;
        this.entityClass = entityClass;
    }

    // =====================================
    // Méthodes pour gérer l'exclusion
    // =====================================
    
    /**
     * Exclure une méthode spécifique du filtrage global
     */
    protected void excludeMethodFromFiltering(String methodName) {
        excludedMethods.add(methodName);
        logger.debug("Method {} excluded from global filtering", methodName);
    }
    
    /**
     * Réinclure une méthode dans le filtrage global
     */
    protected void includeMethodInFiltering(String methodName) {
        excludedMethods.remove(methodName);
        logger.debug("Method {} included in global filtering", methodName);
    }
    
    /**
     * Vérifie si une méthode est exclue du filtrage
     */
    protected boolean isMethodExcluded(String methodName) {
        return excludedMethods.contains(methodName);
    }
    
    /**
     * Applique le filtrage global seulement si la méthode n'est pas exclue
     */
    protected Mono<Query> applyGlobalFilter(Query query,EnumFilter type) {
        return applyGlobalFilter(query, null,type);
    }
    
    protected Mono<Query> applyGlobalFilter(Query query, String methodName,EnumFilter type) {
        // Si un nom de méthode est fourni et qu'il est exclu, retourner la query originale
        if (methodName != null && isMethodExcluded(methodName)) {
            logger.debug("Skipping global filter for excluded method: {}", methodName);
            return Mono.just(query);
        }
        return Mono.deferContextual(ctx -> {
            SecurityUser securityUser = ctx.getOrDefault("CURRENT_USER", null);
            
            UUID ownerCode = null;
            UUID restoCode = null;
            String userRole = null;
            UUID currentUserId = null;
            
            if (securityUser != null) {
                ownerCode = securityUser.getOwnerCode();
                restoCode = securityUser.getRestoCode();
                userRole = securityUser.getRole();
                currentUserId = securityUser.getId();
            }
            // Récupérer la criteria actuelle
            Criteria criteria;
            if (query.getCriteria().isPresent() && query.getCriteria().get() instanceof Criteria c) {
                criteria = c;
            } else {
                criteria = Criteria.empty();
            }
            boolean filterByOwner = type == EnumFilter.ALL || type == EnumFilter.BYOWNER;
            boolean filterByResto = type == EnumFilter.ALL || type == EnumFilter.BYRESTO;

            
            if (ownerCode != null && filterByOwner) criteria = criteria.and("owner_code").is(ownerCode);
            if (restoCode != null && filterByResto) criteria = criteria.and("resto_code").is(restoCode);

            
            // Ajouter filtre created_by UNIQUEMENT pour les admins
            if (isAdminRole(userRole) && currentUserId != null && hasCreatedByField()) {
                criteria = criteria.and("created_by").is(currentUserId);
            }

            return Mono.just(Query.query(criteria).sort(query.getSort()));
        });
    }

    // =====================================
    // Méthodes principales avec support d'exclusion
    // =====================================
    
    @Override
    public Mono<T> findById(ID id) {
        return applyGlobalFilter(Query.query(Criteria.where("id").is(id)), "findById",null)
                .flatMap(q -> template.selectOne(q, entityClass));
    }

    @Override
    public Flux<T> findAll(EnumFilter type) {
        return applyGlobalFilter(Query.empty(), "findAll",type)
                .flatMapMany(q -> template.select(q, entityClass));
    }

    @Override
    public Mono<Boolean> existsById(ID id) {
        return applyGlobalFilter(Query.query(Criteria.where("id").is(id)), "existsById",null)
                .flatMap(q -> template.exists(q, entityClass));
    }

    // =====================================
    // RECHERCHE AVEC PAGINATION - CORRIGÉE
    // =====================================
    
    public Flux<T> search(Map<String, Object> filters,EnumFilter type) {
    	
        return Flux.deferContextual(ctx -> {
            SecurityUser securityUser = ctx.getOrDefault("CURRENT_USER", null);            
            UUID ownerCode = null;
            UUID restoCode = null;
            String userRole = null;
            UUID currentUserId = null;
            
            if (securityUser != null){
                ownerCode = securityUser.getOwnerCode();
                restoCode = securityUser.getRestoCode();
                userRole = securityUser.getRole();
                currentUserId = securityUser.getId();
            }
            
            Criteria criteria = Criteria.empty();
            boolean filterByOwner = type == EnumFilter.ALL || type == EnumFilter.BYOWNER;
            boolean filterByResto = type == EnumFilter.ALL || type == EnumFilter.BYRESTO;
           
            if (ownerCode != null && filterByOwner) criteria = criteria.and("owner_code").is(ownerCode);
            if (restoCode != null && filterByResto && !isSuperAdminRole(userRole)) criteria = criteria.and("resto_code").is(restoCode);

            if (isAdminRole(userRole) && currentUserId != null && hasCreatedByField() && !isSuperAdminRole(userRole)) {
                criteria = criteria.and("created_by").is(currentUserId);
            }

            for (Map.Entry<String, Object> entry : filters.entrySet()) {
                String field = entry.getKey();
                Object value = entry.getValue();
                if (value == null) continue;
                
                if (isPaginationParameter(field)) {
                    continue;
                }
                
                // Gestion des opérateurs spéciaux
                if (field.endsWith(".like") && value instanceof String str && !str.isBlank()) {
                    String actualField = field.substring(0, field.length() - 5);
                    // SOLUTION PostgreSQL : Utiliser ilike pour case insensitive
                    criteria = criteria.and(actualField).like("%" + str + "%").ignoreCase(true);
                }
                else if (field.endsWith(".eq") && value != null) {
                    String actualField = field.substring(0, field.length() - 3);
                    criteria = criteria.and(actualField).is(value);
                }
                else if (field.endsWith(".in") && value instanceof Collection) {
                    String actualField = field.substring(0, field.length() - 3);
                    criteria = criteria.and(actualField).in((Collection<?>) value);
                }
                else if (field.endsWith(".gte") && value != null) {
                    String actualField = field.substring(0, field.length() - 4);
                    criteria = criteria.and(actualField).greaterThanOrEquals(value);
                }
                else if (field.endsWith(".lte") && value != null) {
                    String actualField = field.substring(0, field.length() - 4);
                    criteria = criteria.and(actualField).lessThanOrEquals(value);
                }
                // SOLUTION PostgreSQL : Recherche par défaut avec ilike
                else if (value instanceof String str && !str.isBlank()) {
                    criteria = criteria.and(field).like("%" + str + "%").ignoreCase(true);
                } else {
                    criteria = criteria.and(field).is(value);
                }
            }
            
            int page = (int) filters.getOrDefault("page", 0);
            int size = (int) filters.getOrDefault("size", 20);
            String sortField = (String) filters.getOrDefault("sort", "createdDate");
            String sortDirection = (String) filters.getOrDefault("direction", "desc");
            
            sortField = validateSortField(sortField);
            
            org.springframework.data.domain.Sort sort = sortDirection.equalsIgnoreCase("desc") 
                ? org.springframework.data.domain.Sort.by(sortField).descending() 
                : org.springframework.data.domain.Sort.by(sortField).ascending();
            
            Query query = Query.query(criteria)
                .with(org.springframework.data.domain.PageRequest.of(page, size, sort));
            

            return template.select(query, entityClass);
        });
    }
    // =====================================
    // Méthode pour compter les résultats (pour la pagination) - CORRIGÉE
    // =====================================
    public Mono<Long> count(Map<String, Object> filters,EnumFilter type) {
        return Mono.deferContextual(ctx -> {
            SecurityUser securityUser = ctx.getOrDefault("CURRENT_USER", null);            
            UUID ownerCode = null;
            UUID restoCode = null;
            String userRole = null;
            UUID currentUserId = null;
            
            if (securityUser != null){
                ownerCode = securityUser.getOwnerCode();
                restoCode = securityUser.getRestoCode();
                userRole = securityUser.getRole();
                currentUserId = securityUser.getId();
            }
            
            Criteria criteria = Criteria.empty();
            boolean filterByOwner = type == EnumFilter.ALL || type == EnumFilter.BYOWNER;
            boolean filterByResto = type == EnumFilter.ALL || type == EnumFilter.BYRESTO;

            
            if (ownerCode != null && filterByOwner) criteria = criteria.and("owner_code").is(ownerCode);
            if (restoCode != null && filterByResto) criteria = criteria.and("resto_code").is(restoCode);

            if (isAdminRole(userRole) && currentUserId != null && hasCreatedByField()) {
                criteria = criteria.and("created_by").is(currentUserId);
            }

            // Appliquer les mêmes filtres que la recherche (EXCLURE la pagination)
            for (Map.Entry<String, Object> entry : filters.entrySet()) {
                String field = entry.getKey();
                Object value = entry.getValue();
                if (value == null) continue;
                
                // EXCLURE les paramètres de pagination
                if (isPaginationParameter(field)) {
                    continue;
                }
                
                if (field.endsWith(".like") && value instanceof String str && !str.isBlank()) {
                	 String actualField = field.substring(0, field.length() - 5).toLowerCase();
                     criteria = criteria.and(actualField).like("%" + str.toLowerCase() + "%").ignoreCase(true);
                }
                else if (field.endsWith(".eq") && value != null) {
                    String actualField = field.substring(0, field.length() - 3);
                    criteria = criteria.and(actualField).is(value);
                }
                else if (field.endsWith(".in") && value instanceof Collection) {
                    String actualField = field.substring(0, field.length() - 3);
                    criteria = criteria.and(actualField).in((Collection<?>) value);
                }
                else if (field.endsWith(".gte") && value != null) {
                    String actualField = field.substring(0, field.length() - 4);
                    criteria = criteria.and(actualField).greaterThanOrEquals(value);
                }
                else if (field.endsWith(".lte") && value != null) {
                    String actualField = field.substring(0, field.length() - 4);
                    criteria = criteria.and(actualField).lessThanOrEquals(value);
                }
                else if (value instanceof String str && !str.isBlank()) {
                    criteria = criteria.and(field).like("%" + str.toLowerCase() + "%").ignoreCase(true);
                    
                } else {
                    criteria = criteria.and(field).is(value);
                }
            }
            
            return template.count(Query.query(criteria), entityClass);
        });
    }

    // =====================================
    // Méthode de recherche avec résultat paginé
    // =====================================
    @Override
    public Mono<PageResponse<T>> searchWithPagination(Map<String, Object> filters,EnumFilter type) {
        return Mono.zip(
            search(filters,type).collectList(),
            count(filters,type)
        ).map(tuple -> {
            java.util.List<T> content = tuple.getT1();
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
    // Méthodes utilitaires pour la pagination
    // =====================================

    /**
     * Vérifie si un paramètre est un paramètre de pagination
     */
    public boolean isPaginationParameter(String field) {
        return field.equals("page") || 
               field.equals("size") || 
               field.equals("sort") || 
               field.equals("direction");
    }

    /**
     * Valide et sécurise le champ de tri pour éviter l'injection SQL
     */
    public String validateSortField(String sortField) {
        // Liste des champs autorisés pour le tri
        Set<String> allowedSortFields = Set.of(
            "id", "name", "createdDate", "updatedDate", "active"
            // Ajoutez ici tous les champs de votre entité qui peuvent être triés
        );
        
        // Si le champ n'est pas autorisé, utiliser une valeur par défaut
        if (!allowedSortFields.contains(sortField)) {
            logger.warn("Champ de tri non autorisé: {}, utilisation de 'createdDate' par défaut", sortField);
            return "createdDate";
        }
        
        return sortField;
    }

    // =====================================
    // Classes utilitaires pour la pagination
    // =====================================

    // Classe de réponse paginée
    public static class PageResponse<T> {
        private final java.util.List<T> content;
        private final int currentPage;
        private final int pageSize;
        private final long totalElements;
        private final int totalPages;
        private final boolean hasPrevious;
        private final boolean hasNext;
        
        public PageResponse(java.util.List<T> content, int currentPage, int pageSize, 
                           long totalElements, int totalPages, 
                           boolean hasPrevious, boolean hasNext) {
            this.content = content;
            this.currentPage = currentPage;
            this.pageSize = pageSize;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.hasPrevious = hasPrevious;
            this.hasNext = hasNext;
        }
        
        // Getters
        public java.util.List<T> getContent() { return content; }
        public int getCurrentPage() { return currentPage; }
        public int getPageSize() { return pageSize; }
        public long getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public boolean isHasPrevious() { return hasPrevious; }
        public boolean isHasNext() { return hasNext; }
    }

    // =====================================
    // Méthodes utilitaires
    // =====================================
    
    // Vérifie si le rôle est un rôle admin
    public boolean isAdminRole(String role) {
        if (role == null) return false;
        String roleUpper = role.toUpperCase();
        return roleUpper.contains("ADMIN") || 
               roleUpper.contains("SUPER") || 
               roleUpper.equals("OWNER") ||
               roleUpper.equals("MANAGER");
    }
 // Vérifie si le rôle est un rôle admin
    public boolean isSuperAdminRole(String role) {
        if (role == null) return false;
        String roleUpper = role.toUpperCase();
        return roleUpper.contains("ROLE_SUPERADMIN");
    }

    // Vérifie si l'entité a le champ created_by
    public boolean hasCreatedByField() {
        try {
            entityClass.getDeclaredField("createdBy");
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    // =====================================
    // Méthodes principales (inchangées)
    // =====================================
    

    @Override
    public Mono<Void> deleteById(ID id) {
        return findById(id)
                .flatMap(template::delete)
                .then();
    }

    @Override
    public Mono<Void> delete(T entity) {
        return template.delete(entity).then();
    }

    

    // =====================================
    // MÉTHODE findExistingEntity modifiée pour inclure created_by pour admin
    // =====================================
    
    // Méthode pour trouver l'entité existante avec vérification de sécurité
    public <S extends T> Mono<S> findExistingEntity(S entity) {
        return Mono.deferContextual(ctx -> {           
            Criteria securityCriteria = Criteria.where("id").is(entity.getId());         
            logger.debug("SEARCHING ENTITY WITH CRITERIA: {}", securityCriteria);
            return template.selectOne(Query.query(securityCriteria), (Class<S>) entity.getClass())
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn("ENTITY NOT FOUND OR ACCESS DENIED - ID: {}, OwnerCode: {}, RestoCode: {}", 
                        entity.getId(), entity.getOwnerCode(), entity.getRestoCode());
                    return Mono.error(new EntityNotFoundException(
                        "Échec de la mise à jour : l'entité est introuvable ou vous n'avez pas les droits" 
                    ));
                }))
                .doOnSuccess(found -> {
                    logger.debug("ENTITY FOUND - Current Version: {}", found);
                });
        });
    }

 // Méthode pour trouver l'entité existante avec vérification de sécurité
    public <S extends T> Mono<S> findExistingEntityWithoutRestoCodeAndOwner(S entity) {
        return Mono.deferContextual(ctx -> {
            SecurityUser securityUser = ctx.getOrDefault("CURRENT_USER", null);            
            Criteria securityCriteria = Criteria.where("id").is(entity.getId());         
           
            // Ajouter filtre created_by UNIQUEMENT pour les admins
            if (securityUser != null && isAdminRole(securityUser.getRole()) && 
                securityUser.getId() != null && hasCreatedByField()) {
                securityCriteria = securityCriteria.and("created_by").is(securityUser.getId());
            }
            
            logger.debug("SEARCHING ENTITY WITH CRITERIA: {}", securityCriteria);
            
            return template.selectOne(Query.query(securityCriteria), (Class<S>) entity.getClass())
                .switchIfEmpty(Mono.defer(() -> {
                    logger.warn("ENTITY NOT FOUND OR ACCESS DENIED - ID: {}", 
                        entity.getId());
                    return Mono.error(new EntityNotFoundException(
                        "Échec de la mise à jour : l'entité est introuvable ou vous n'avez pas les droits" 
                    ));
                }))
                .doOnSuccess(found -> {
                    logger.debug("ENTITY FOUND - Current Version: {}", found);
                });
        });
    }

    
    // =====================================
    // Méthodes restantes inchangées
    // =====================================
    
    @Override 
    public <S extends T> Flux<S> saveAll(Iterable<S> entities) {
        return Flux.fromIterable(entities).flatMap(this::save);
    }

    @Override 
    public <S extends T> Flux<S> saveAll(Publisher<S> entityStream) {
        return Flux.from(entityStream).flatMap(this::save);
    }

    @Override 
    public Flux<T> findAllById(Iterable<ID> ids) {
        return Flux.fromIterable(ids).flatMap(this::findById);
    }

    @Override 
    public Flux<T> findAllById(Publisher<ID> idStream) {
        return Flux.from(idStream).flatMap(this::findById);
    }

    @Override 
    public Mono<Long> count() {
        return findAll().count();
    }

    @Override 
    public Mono<T> findByIdAndOwnerCodeAndRestoCode(ID id, UUID ownerCode, UUID restoCode) {
        Criteria criteria = Criteria.where("id").is(id);
        
        if (ownerCode != null) {
            criteria = criteria.and("owner_code").is(ownerCode);
        }
        if (restoCode != null) {
            criteria = criteria.and("resto_code").is(restoCode);
        }
        
        return template.selectOne(Query.query(criteria), entityClass);
    }

    @Override 
    public Flux<T> findAllByOwnerCodeAndRestoCode(UUID ownerCode, UUID restoCode) {
        Criteria criteria = Criteria.empty();
        
        if (ownerCode != null) {
            criteria = criteria.and("owner_code").is(ownerCode);
        }
        if (restoCode != null) {
            criteria = criteria.and("resto_code").is(restoCode);
        }
        
        return template.select(Query.query(criteria), entityClass);
    }

    @Override 
    public Mono<Boolean> existsByIdAndOwnerCodeAndRestoCode(ID id, UUID ownerCode, UUID restoCode) {
        Criteria criteria = Criteria.where("id").is(id);
        
        if (ownerCode != null) {
            criteria = criteria.and("owner_code").is(ownerCode);
        }
        if (restoCode != null) {
            criteria = criteria.and("resto_code").is(restoCode);
        }
        
        return template.exists(Query.query(criteria), entityClass);
    }

    // R2DBC stubs pour compatibilité Spring Data
    @Override 
    public Mono<T> findById(Publisher<ID> id) { 
        return Mono.from(id).flatMap(this::findById);
    }
    
    @Override 
    public Mono<Boolean> existsById(Publisher<ID> id) { 
        return Mono.from(id).flatMap(this::existsById);
    }
    
    @Override 
    public Mono<Void> deleteById(Publisher<ID> id) { 
        return Mono.from(id).flatMap(this::deleteById).then();
    }
    
    @Override 
    public Mono<Void> deleteAllById(Iterable<? extends ID> ids) { 
        return Flux.fromIterable(ids).flatMap(this::deleteById).then();
    }
    
    @Override 
    public Mono<Void> deleteAll(Iterable<? extends T> entities) { 
        return Flux.fromIterable(entities).flatMap(this::delete).then();
    }
    
    @Override 
    public Mono<Void> deleteAll(Publisher<? extends T> entityStream) { 
        return Flux.from(entityStream).flatMap(this::delete).then();
    }
    
    @Override 
    public Mono<Void> deleteAll() { 
        return applyGlobalFilter(Query.empty(),null)
                .flatMapMany(q -> template.select(q, entityClass))
                .flatMap(this::delete)
                .then();
    }
    
    @Override
    public Flux<T> findByActive(Boolean active,EnumFilter type) {
        logger.debug("Finding entities by active status: {}", active);
        return applyGlobalFilter(Query.query(Criteria.where("active").is(active)),type)
                .flatMapMany(q -> template.select(q, entityClass))
                .doOnComplete(() -> logger.debug("Completed finding entities by active status: {}", active))
                .doOnError(error -> logger.error("Error finding entities by active status {}: {}", active, error.getMessage(), error));
    }

    public Mono<Boolean> existsByIdAndActive(ID id, Boolean active) {
        logger.debug("Checking existence by ID: {} and active: {}", id, active);
        
        return applyGlobalFilter(Query.query(
                Criteria.where("id").is(id)
                       .and("active").is(active)),null)
                .flatMap(q -> template.exists(q, entityClass));
    }
    
    public Mono<T> findByIdAndActive(ID id, Boolean active) {
        logger.debug("Finding entity by ID: {} and active: {}", id, active);
        
        return applyGlobalFilter(Query.query(
                Criteria.where("id").is(id)
                       .and("active").is(active)),null)
                .flatMap(q -> template.selectOne(q, entityClass))
                .doOnSuccess(entity -> {
                    if (entity != null) {
                        logger.debug("Found entity by ID: {} and active: {}", id, active);
                    } else {
                        logger.debug("No entity found by ID: {} and active: {}", id, active);
                    }
                });
    }

    private <S extends T> Mono<S> updateEntity(S entity) {
        logger.debug("UPDATE PROCESS - Entity: {} | ID: {}", entity.getClass().getSimpleName(), entity.getId());
        
        return findExistingEntity(entity)
            .flatMap(existingEntity -> {
                copyUpdatableFields(existingEntity, entity);
                logger.debug("APPLYING UPDATES");
                return template.update(existingEntity)
                    .map(updated -> {
                        logger.debug("UPDATE SUCCESSFUL - ID: {}", updated.getId());
                        return entity;
                    });
            })
            .onErrorResume(this::handleUpdateError);
    }

    public <S extends T> void copyUpdatableFields(S target, S source) {
        try {
            // Copier les champs de la classe actuelle et toutes les classes parentes
            Class<?> currentClass = source.getClass();
            while (currentClass != null && currentClass != Object.class) {
                java.lang.reflect.Field[] fields = currentClass.getDeclaredFields();
                for (java.lang.reflect.Field field : fields) {
                    if (isUpdatableField(field.getName())) {
                        field.setAccessible(true);
                        Object value = field.get(source);
                        if (value != null) {
                            field.set(target, value);
                        }
                    }
                }
                currentClass = currentClass.getSuperclass();
            }
        } catch (Exception e) {
            logger.error("ERROR COPYING FIELDS: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la copie des champs", e);
        }
    }

    private boolean isUpdatableField(String fieldName) {
        Set<String> nonUpdatableFields = Set.of(
            "id", "version","createdDate", "createdBy");
        return !nonUpdatableFields.contains(fieldName);
    }

    public <S extends T> Mono<S> handleUpdateError(Throwable err) {
        String errorMsg = err.getMessage();
        
        if (errorMsg.contains("Version does not match")) {
            return Mono.error(new RuntimeException(
                "Conflit de version - cette donnée a été modifiée par un autre utilisateur"
            ));
        }
        
        if (errorMsg.contains("introuvable") || errorMsg.contains("not found")) {
            return Mono.error(new RuntimeException(
                "Entité non trouvée - vérifiez les permissions ou rafraîchissez la page"
            ));
        }
        
        logger.error("UPDATE ERROR: {}", errorMsg, err);
        return Mono.error(new RuntimeException("Erreur de mise à jour: " + errorMsg));
    }

    @Transactional
    public <S extends T> Mono<S> save(S entity) {
        return Mono.deferContextual(ctx -> {
            SecurityUser securityUser = ctx.getOrDefault("CURRENT_USER", null);
                    
            if (securityUser != null) {
                if (entity.getOwnerCode() == null) {
                    entity.setOwnerCode(securityUser.getOwnerCode());
                }
                if (entity.getRestoCode() == null) {
                    entity.setRestoCode(securityUser.getRestoCode());
                }
            }

            if (entity.getOwnerCode() == null && entity.getRestoCode() == null) {
                return Mono.error(new IllegalStateException(
                    "Au moins un code (ownerCode ou restoCode) doit être présent"));
            }
            if (entity.getId() == null) {
                return template.insert(entity);
            } else {
                return updateEntityWithExistenceCheck(entity);
            }
        });
    }
   
    @Transactional
    private <S extends T> Mono<S> updateEntityWithExistenceCheck(S entity) {
        logger.debug("UPDATE WITH EXISTENCE CHECK - ID: {}", entity.getId());
        logger.debug("ENTITY TO UPDATE: {}", entity.toString());
       
        return findExistingEntity(entity)
            .flatMap(existing -> {
                logger.debug("EXISTING ENTITY BEFORE COPY: {}", existing.toString());
                copyUpdatableFields(existing, entity);  
                logger.debug("EXISTING ENTITY AFTER COPY: {}", existing.toString());
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
    public Flux<T> findAllByRestoCode(UUID restoCode,Boolean available) {   
        Criteria criteria = Criteria.empty();      
        if (restoCode != null) 
            criteria = criteria.and("resto_code").is(restoCode); 
        if (available != null)
        criteria = criteria.and("is_available").is(available);

        return template.select(Query.query(criteria), entityClass);
    }
    
    @Override
    public Flux<T> findAll() {
    	// TODO Auto-generated method stub
    	return null;
    }
}