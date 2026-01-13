package com.app.manage_restaurant.cores;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.transaction.annotation.Transactional;

import com.app.manage_restaurant.exceptions.entities.EntityNotFoundException;
import com.app.manage_restaurant.services.GenericDuplicateChecker;
import com.app.manage_restaurant.utilities.FileStorageUtil;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class BaseServiceImpl<E extends HasOwnerAndResto<UUID>, RQ, RS, ID>
        implements BaseService<E, RQ, RS, ID> {

    protected final BaseRepository<E, ID> repository;
    protected final R2dbcEntityTemplate template;
    protected final Logger logger;
    protected final String entityName;
    protected final Class<E> entityClass;
    protected final Function<RQ, E> createMapper;
    protected final Function<E, RS> responseMapper;
    protected final ReactiveExceptionHandler exceptionHandler;
    protected final GenericDuplicateChecker duplicateChecker;
    private final FileStorageUtil fileStorageUtil;

    public BaseServiceImpl(BaseRepository<E, ID> repository,
                          FileStorageUtil fileStorageUtil,
                          @Lazy R2dbcEntityTemplate template,
                          Function<RQ, E> createMapper,
                          Function<E, RS> responseMapper,
                          Class<E> entityClass,
                          String entityName,
                          ReactiveExceptionHandler exceptionHandler,
                          GenericDuplicateChecker duplicateChecker) {
        this.repository = repository;
        this.template = template;
        this.entityName = entityName;
        this.createMapper = createMapper;
        this.responseMapper = responseMapper;
        this.fileStorageUtil = fileStorageUtil;
        this.entityClass = entityClass;
        this.exceptionHandler = exceptionHandler;
        this.duplicateChecker = duplicateChecker;
        this.logger = LoggerFactory.getLogger(getClass());
    }
    
    // ==================================
    // Find génériques avec filtrage automatique
    // ==================================
    @Override
    public Mono<RS> findById(ID id) {
        logger.debug("Finding {} by ID: {}", entityName, id);
        
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(entityName, id)))
                .map(responseMapper::apply)
                .doOnSuccess(result -> logger.debug("Successfully found {} by ID: {}", entityName, id))
                .doOnError(error -> logger.error("Error finding {} by ID {}: {}", entityName, id, error.getMessage(), error));
    }

    @Override
    public Flux<RS> findAll() {
        logger.debug("Finding all {}", entityName);
        
        return repository.findAll()
                .map(responseMapper::apply)
                .doOnComplete(() -> logger.debug("Completed finding all {}", entityName))
                .doOnError(error -> logger.error("Error finding all {}: {}", entityName, error.getMessage(), error));
    }

    @Override
    public Mono<RS> existsById(ID id) {
        logger.debug("Checking existence of {} with ID: {}", entityName, id);
        
        return repository.existsById(id)
                .map(exists -> {
                    Map<String, Object> data = Map.of("id", id, "exists", exists);
                    return responseMapper.apply(createDummyEntity(data));
                })
                .doOnSuccess(result -> logger.debug("Existence check completed for {} with ID: {}", entityName, id))
                .doOnError(error -> logger.error("Error checking existence of {} with ID {}: {}", entityName, id, error.getMessage(), error));
    }

    // ==================================
    // Save / Update
    // ==================================
    @Override
    public Mono<RS> save(RQ request) {
        logger.debug("Saving new {}", entityName); 
        return validate(request)
                .then(checkUnique(request, null,createMapper.apply(request).getRestoCode() , createMapper.apply(request).getOwnerCode()))
                .then(Mono.defer(() -> {
                    E entity = createMapper.apply(request);
                    return repository.save(entity);
                }))
                .map(responseMapper::apply)
                .doOnSuccess(result -> logger.debug("Successfully saved new {}", entityName))
                .doOnError(error -> logger.error("Error saving new {}: {}", entityName, error.getMessage(), error));
    }

    @Override
    public Mono<RS> update(ID id, RQ request) {
        logger.debug("Updating {} with ID: {}", entityName, id);
        return validate(request)
                .then(checkUnique(request, id,createMapper.apply(request).getRestoCode() , createMapper.apply(request).getOwnerCode()))
                .then(Mono.defer(() -> {
                    E entity = createMapper.apply(request);
                    return repository.save(entity);
                }))
                .map(responseMapper::apply)
                .doOnSuccess(result -> logger.debug("Successfully updated  {}", entityName))
                .doOnError(error -> logger.error("Error updating new {}: {}", entityName, error.getMessage(), error));
 
        
    }    
    
    @Override
    public Flux<RS> findAllActive(Boolean active,EnumFilter type) {
        logger.debug("Finding all {} with active status: {}", entityName, active);        
        return repository.findByActive(active,type)
                .map(responseMapper::apply)
                .doOnComplete(() -> logger.debug("Completed finding active {} with status: {}", entityName, active))
                .doOnError(error -> logger.error("Error finding active {} with status {}: {}", 
                    entityName, active, error.getMessage(), error));
    }
    
    // ==================================
    // Méthodes supplémentaires pour le statut actif
    // ==================================
    
    /**
     * Vérifie si une entité existe par ID et statut actif
     */
    public Mono<Boolean> existsByIdAndActive(ID id, Boolean active) {
        logger.debug("Checking existence of {} with ID: {} and active: {}", entityName, id, active);
        
        return ((BaseRepositoryImpl<E, ID>) repository).existsByIdAndActive(id, active)
                .doOnSuccess(result -> logger.debug("Existence check completed for {} with ID: {} and active: {} - Result: {}", 
                    entityName, id, active, result))
                .doOnError(error -> logger.error("Error checking existence of {} with ID {} and active {}: {}", 
                    entityName, id, active, error.getMessage(), error));
    }
    
    /**
     * Trouve une entité par ID et statut actif
     */
    public Mono<RS> findByIdAndActive(ID id, Boolean active) {
        logger.debug("Finding {} by ID: {} and active: {}", entityName, id, active);
        
        return ((BaseRepositoryImpl<E, ID>) repository).findByIdAndActive(id, active)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(entityName, id)))
                .map(responseMapper::apply)
                .doOnSuccess(result -> logger.debug("Successfully found {} by ID: {} and active: {}", entityName, id, active))
                .doOnError(error -> logger.error("Error finding {} by ID {} and active {}: {}", entityName, id, active, error.getMessage(), error));
    }

    // ==================================
    // Méthodes avec fichiers (à override si nécessaire)
    // ==================================
    public Mono<RS> updateWithFiles(ID id, RQ request, Mono<FilePart> logoMono, Mono<FilePart> coverMono) {
        throw new UnsupportedOperationException("Override in concrete service if entity has files");
    }

    public Mono<RS> createWithFiles(RQ request, Mono<FilePart> logoMono, Mono<FilePart> coverMono) {
        throw new UnsupportedOperationException("Override in concrete service if entity has files");
    }

    // ==================================
    // Delete générique avec filtrage global
    // ==================================
    @Override
    public Mono<RS> delete(ID id) {
        logger.debug("Deleting {} with ID: {}", entityName, id);
        
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(entityName, id)))
                .flatMap(entity -> repository.delete(entity).thenReturn(entity))
                .map(responseMapper::apply)
                .doOnSuccess(result -> logger.debug("Successfully deleted {} with ID: {}", entityName, id))
                .doOnError(error -> logger.error("Error deleting {} with ID {}: {}", entityName, id, error.getMessage(), error));
    }

   

    // ==================================
    // Méthode utilitaire pour créer une entité factice
    // ==================================
    protected E createDummyEntity(Map<String, Object> data) {
        throw new UnsupportedOperationException("Override in concrete service if needed");
    }

    // ==================================
    // Changement d'état
    // ==================================
    @Override
    public Mono<RS> changeState(ID id) {
        logger.debug("Changing state of {} with ID: {}", entityName, id);
        
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(entityName, id)))
                .flatMap(entity -> {
                    try {
                        var activeField = entity.getClass().getMethod("isActive");
                        var setActiveMethod = entity.getClass().getMethod("setActive", boolean.class);
                        boolean currentState = (boolean) activeField.invoke(entity);
                        setActiveMethod.invoke(entity, !currentState);
                        return repository.save(entity);
                    } catch (NoSuchMethodException e) {
                        return Mono.error(new RuntimeException("L'entité ne possède pas de champ 'active'"));
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                })
                .map(responseMapper::apply)
                .doOnSuccess(result -> logger.debug("Successfully changed state of {} with ID: {}", entityName, id))
                .doOnError(error -> logger.error("Error changing state of {} with ID {}: {}", entityName, id, error.getMessage(), error));
    }

    // ==================================
    // Champs uniques
    // ==================================
    public abstract Map<String, Object> extractUniqueFields(RQ request);

    public Mono<Void> checkUnique(RQ request, ID excludeId,UUID restoCodeR,UUID ownerCodeR) {
        Map<String, Object> uniqueFields = extractUniqueFields(request);
        if (uniqueFields == null || uniqueFields.isEmpty()) return Mono.empty();

        String table = entityClass.getSimpleName().toLowerCase();

        if (excludeId == null) {
            return duplicateChecker.checkDuplicates(table, uniqueFields, entityClass,restoCodeR,ownerCodeR);
        } else {
            return duplicateChecker.checkDuplicatesForUpdate(table, uniqueFields, excludeId, entityClass,restoCodeR,ownerCodeR);
        }
    }

    // ==================================
    // Validation abstraite
    // ==================================
    public abstract Mono<Void> validate(RQ request);

    // ==================================
    // Mapper les champs
    // ==================================
    public void applyRequestToEntity(E existing, RQ request) {
        // Override dans services concrets
    }
    
    // ==================================
    // Méthodes utilitaires supplémentaires
    // ==================================
    
    /**
     * Compte toutes les entités avec filtrage global automatique
     */
    public Mono<Long> count() {
        logger.debug("Counting all {}", entityName);
        
        return repository.count()
                .doOnSuccess(count -> logger.debug("Counted {} {} entities", count, entityName))
                .doOnError(error -> logger.error("Error counting {}: {}", entityName, error.getMessage(), error));
    }
    
    /**
     * Trouve toutes les entités par IDs avec filtrage global automatique
     */
    public Flux<RS> findAllById(Iterable<ID> ids) {
        logger.debug("Finding all {} by IDs: {}", entityName, ids);
        
        return repository.findAllById(ids)
                .map(responseMapper::apply)
                .doOnComplete(() -> logger.debug("Completed finding all {} by IDs", entityName))
                .doOnError(error -> logger.error("Error finding all {} by IDs: {}", entityName, error.getMessage(), error));
    }
    
    // ==================================
    // IMPLÉMENTATION DES MÉTHODES AVEC FICHIERS
    // ==================================
    
    @Override
    public Mono<RS> save(RQ request, Mono<FilePart> file, String folder) {
        logger.debug("🎯 SAVE - Début pour {}", entityName);
        UUID restoCodeR= createMapper.apply(request).getRestoCode() ;
        UUID ownerCodeR=createMapper.apply(request).getOwnerCode() ;
        return validate(request)
            .then(checkUnique(request, null,restoCodeR,ownerCodeR))
            .then(Mono.defer(() -> {
                // Créer l'entité sans le fichier d'abord
                E entity = createMapper.apply(request);
                return repository.save(entity);
            }))
            .flatMap(savedEntity -> {
                logger.debug("✅ Entité {} sauvegardée, traitement du fichier", entityName);

                return processFileForSave(file, folder)
                    .flatMap(filePath -> {
                        // Mettre à jour l'entité avec le chemin du fichier
                        setFileField(savedEntity, filePath);
                        return repository.save(savedEntity);
                    })
                    .map(responseMapper::apply)
                    .onErrorResume(error -> {
                        logger.error("❌ Erreur lors de la création de {}: {}", entityName, error.getMessage());
                        
                        // Nettoyage en cas d'erreur
                        return cleanupOnError(file, folder, error);
                    });
            })
            .doOnSuccess(response -> logger.debug("🎉 {} créé avec succès", entityName))
            .doOnError(error -> logger.error("💥 Échec création {}: {}", entityName, error.getMessage()));
    }

    @Transactional
    @Override
    public Mono<RS> update(ID id, RQ request, Mono<FilePart> file, String folder) {
        logger.debug("🎯 UPDATE - Début pour {} ID: {}", entityName, id);

        return validate(request)
            .then(checkUnique(request, id,createMapper.apply(request).getRestoCode() , createMapper.apply(request).getOwnerCode()))
            // Étape 1 : Récupérer l'entité existante
            .then(repository.findById(id))
            .switchIfEmpty(Mono.error(new EntityNotFoundException(entityName, id)))
            // Étape 2 : Mettre à jour l'entité SANS le fichier d'abord
            .flatMap(existingEntity -> {
                logger.debug("💾 Mise à jour de l'entité {} SANS fichier", entityName);
                
                // Sauvegarder l'ancien chemin de fichier
                String oldFilePath = getFileField(existingEntity);
                
                // Mettre à jour l'entité avec les nouvelles données
                applyRequestToEntity(existingEntity, request);
                return repository.save(existingEntity)
                    .flatMap(updatedEntity -> {
                        logger.debug("✅ Entité {} mise à jour, traitement du fichier", entityName);
                        
                        return processFileForUpdate(file, folder, oldFilePath)
                            .flatMap(newFilePath -> {
                                // Mettre à jour le chemin du fichier si nécessaire
                                if (newFilePath != null && !newFilePath.isEmpty()) {
                                    setFileField(updatedEntity, newFilePath);
                                    return repository.save(updatedEntity);
                                }
                                return Mono.just(updatedEntity);
                            });
                    });
            })
            .map(responseMapper::apply)
            .doOnSuccess(response -> logger.debug("🎉 {} mis à jour avec succès", entityName))
            .doOnError(error -> logger.error("💥 Échec mise à jour {}: {}", entityName, error.getMessage()));
    }
    
    /**
     * Traitement du fichier pour la création
     */
    protected Mono<String> processFileForSave(Mono<FilePart> fileMono, String folder) {
        if (fileMono == null) {
            return Mono.just("");
        }

        return fileMono
            .hasElement()
            .flatMap(hasFile -> {
                if (hasFile) {
                    logger.debug("📁 Traitement fichier pour création dans le dossier: {}", folder);
                    return processSingleFile(fileMono, folder)
                        .doOnNext(path -> logger.debug("✅ Fichier sauvegardé: {}", path));
                } else {
                    logger.debug("📁 Aucun fichier fourni pour la création");
                    return Mono.just("");
                }
            })
            .defaultIfEmpty("");
    }
    
    /**
     * Traitement du fichier pour la mise à jour
     */
    protected Mono<String> processFileForUpdate(Mono<FilePart> fileMono, String folder, String oldFilePath) {
        if (fileMono == null) {
            return Mono.just(oldFilePath != null ? oldFilePath : "");
        }

        return fileMono
            .hasElement()
            .flatMap(hasNewFile -> {
                if (hasNewFile) {
                    logger.debug("📁 Nouveau fichier fourni pour la mise à jour");
                    
                    // Traiter le nouveau fichier
                    return processSingleFile(fileMono, folder)
                        .flatMap(newFilePath -> {
                            // Supprimer l'ancien fichier si il existe
                            if (oldFilePath != null && !oldFilePath.isEmpty() && !newFilePath.isEmpty()) {
                                logger.debug("🗑️ Suppression ancien fichier: {}", oldFilePath);
                                return fileStorageUtil.deleteFile(oldFilePath)
                                    .doOnSuccess(deleted -> {
                                        if (deleted) logger.debug("✅ Ancien fichier supprimé");
                                        else logger.debug("⚠️ Impossible de supprimer l'ancien fichier");
                                    })
                                    .onErrorResume(error -> {
                                        logger.debug("⚠️ Erreur suppression ancien fichier: {}", error.getMessage());
                                        return Mono.just(false);
                                    })
                                    .thenReturn(newFilePath);
                            }
                            return Mono.just(newFilePath);
                        });
                } else {
                    logger.debug("📁 Aucun nouveau fichier, conservation de l'ancien: {}", 
                                oldFilePath != null ? "présent" : "absent");
                    return Mono.just(oldFilePath != null ? oldFilePath : "");
                }
            })
            .defaultIfEmpty(oldFilePath != null ? oldFilePath : "");
    }

    public Mono<String> processSingleFile(Mono<FilePart> fileMono, String folder) {
        return fileMono
            .flatMap(file -> {
                logger.debug("📁 Traitement fichier {}: {}", folder, file.filename());
                return fileStorageUtil.storeFile(file, folder)
                    .doOnSuccess(path -> logger.debug("✅ Fichier {} sauvegardé: {}", folder, path))
                    .doOnError(error -> logger.debug("❌ Erreur fichier {}: {}", folder, error.getMessage()));
            })
            .onErrorResume(error -> {
                logger.debug("⚠️ Erreur traitement {}, utilisation valeur vide: {}", folder, error.getMessage());
                return Mono.just("");
            })
            .defaultIfEmpty("");
    }

    /**
     * Nettoyage en cas d'erreur
     */
    protected <T> Mono<T> cleanupOnError(Mono<FilePart> fileMono, String folder, Throwable error) {
        if (fileMono == null) {
            return Mono.error(error);
        }

        return fileMono
            .hasElement()
            .flatMap(hasFile -> {
                if (hasFile) {
                    return processSingleFile(fileMono, folder)
                        .flatMap(filePath -> {
                            if (filePath != null && !filePath.isEmpty()) {
                                logger.debug("🧹 Nettoyage fichier en erreur: {}", filePath);
                                return fileStorageUtil.deleteFile(filePath)
                                    .then(Mono.error(new RuntimeException(
                                        "Erreur lors de la création. Rollback effectué.",
                                        error
                                    )));
                            }
                            return Mono.error(new RuntimeException(
                                "Erreur lors de la création. Rollback effectué.",
                                error
                            ));
                        });
                }
                return Mono.error(new RuntimeException(
                    "Erreur lors de la création. Rollback effectué.",
                    error
                ));
            });
    }

    // ==================================
    // MÉTHODES ABSTRAITES POUR LA GESTION DES FICHIERS
    // ==================================
    
    /**
     * Retourne le nom du dossier pour stocker les fichiers
     * Override dans les classes concrètes si nécessaire
     */
    protected String getFileFolder() {
        return entityName.toLowerCase() + "s";
    }
    
    /**
     * Retourne le chemin du fichier dans l'entité
     */
    protected abstract String getFileField(E entity);
    
    /**
     * Définit le chemin du fichier dans l'entité
     */
    protected abstract void setFileField(E entity, String filePath);
    
    // ==================================
    // Recherche avec pagination
    // ==================================
    @Override
    public Mono<PageResponse<RS>> search(Map<String, Object> filters,EnumFilter type) {
        logger.debug("Searching {} with pagination - filters: {}", entityName, filters);
        
        return ((BaseRepositoryImpl<E, ID>) repository).searchWithPagination(filters,type)
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

    // ==================================
    // Recherche simple (sans pagination - pour rétrocompatibilité)
    // ==================================
    @Override
    public Flux<RS> searchAll(Map<String, Object> filters,EnumFilter type) {
        logger.debug("Searching all {} with filters: {}", entityName, filters);
        
        return ((BaseRepositoryImpl<E, ID>) repository).search(filters,type)
                .map(responseMapper::apply)
                .doOnComplete(() -> logger.debug("Completed search for {} with filters: {}", entityName, filters))
                .doOnError(error -> logger.error("Error searching {} with filters {}: {}", 
                    entityName, filters, error.getMessage(), error));
    }

    // ==================================
    // Classe PageResponse pour le service
    // ==================================
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
}