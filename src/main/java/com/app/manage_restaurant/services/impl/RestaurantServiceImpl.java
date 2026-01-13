package com.app.manage_restaurant.services.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.manage_restaurant.cores.BaseServiceImpl;
import com.app.manage_restaurant.cores.EnumFilter;
import com.app.manage_restaurant.dtos.request.OpeningHourRequest;
import com.app.manage_restaurant.dtos.request.RestaurantRequest;
import com.app.manage_restaurant.dtos.response.MenuCategoryResponse;
import com.app.manage_restaurant.dtos.response.MenusResponse;
import com.app.manage_restaurant.dtos.response.OpeningHourResponse;
import com.app.manage_restaurant.dtos.response.RestaurantHomeResponse;
import com.app.manage_restaurant.dtos.response.RestaurantResponse;
import com.app.manage_restaurant.dtos.response.RestaurantSpecialResponse;
import com.app.manage_restaurant.entities.MenuIngredient;
import com.app.manage_restaurant.entities.MenuTag;
import com.app.manage_restaurant.entities.Menus;
import com.app.manage_restaurant.entities.OpeningHour;
import com.app.manage_restaurant.entities.Restaurant;
import com.app.manage_restaurant.mapper.MenuCategoryMapper;
import com.app.manage_restaurant.mapper.MenuMapper;
import com.app.manage_restaurant.mapper.OpeningHourMapper;
import com.app.manage_restaurant.mapper.PermissionMapper;
import com.app.manage_restaurant.mapper.RestaurantMapper;
import com.app.manage_restaurant.mapper.RolesMapper;
import com.app.manage_restaurant.mapper.TablesMapper;
import com.app.manage_restaurant.repositories.MenuCategoryRepository;
import com.app.manage_restaurant.repositories.MenuIngredientRepository;
import com.app.manage_restaurant.repositories.MenuRepository;
import com.app.manage_restaurant.repositories.MenuTagRepository;
import com.app.manage_restaurant.repositories.OpeningHourRepository;
import com.app.manage_restaurant.repositories.PermissionRepository;
import com.app.manage_restaurant.repositories.RestaurantRepository;
import com.app.manage_restaurant.repositories.RolesRepository;
import com.app.manage_restaurant.repositories.TablesRepository;
import com.app.manage_restaurant.services.GenericDuplicateChecker;
import com.app.manage_restaurant.services.RestaurantService;
import com.app.manage_restaurant.utilities.FileStorageUtil;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
public class RestaurantServiceImpl extends BaseServiceImpl<Restaurant, RestaurantRequest, RestaurantResponse, UUID> implements RestaurantService {
	 private final FileStorageUtil fileStorageUtil;
    private final RestaurantRepository repository;
    private final OpeningHourRepository openingHourRepository;
    private final RolesRepository rolesRepository;
    private final TablesRepository tableRepository;

    private final PermissionRepository permissionRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final MenuRepository menuRepository;
    private final DatabaseClient databaseClient;
    protected final R2dbcEntityTemplate template;
    private final MenuIngredientRepository menuIngredientRepository;
    private final MenuTagRepository menuTagRepository;    
	public RestaurantServiceImpl(RestaurantRepository repository,
			                 RolesRepository rolesRepository,
			                 PermissionRepository permissionRepository,
                             R2dbcEntityTemplate template,
                             TablesRepository tableRepository,
                             MenuIngredientRepository menuIngredientRepository,
                             MenuCategoryRepository menuCategoryRepository,
                             MenuTagRepository menuTagRepository,
                             MenuRepository menuRepository,
                             OpeningHourRepository openingHourRepository,
                             FileStorageUtil fileStorageUtil,
                             DatabaseClient databaseClient,
                             ReactiveExceptionHandler exceptionHandler,
                             GenericDuplicateChecker duplicateChecker) {
        super(repository,fileStorageUtil, template, RestaurantMapper::toEntity, RestaurantMapper::toResponse,
              Restaurant.class, "Restaurant", exceptionHandler, duplicateChecker);
        this.repository = repository;
        this.openingHourRepository = openingHourRepository;
        this.databaseClient = databaseClient;
        this.fileStorageUtil=fileStorageUtil;
        this.template = template;
        this.rolesRepository = rolesRepository;
        this.menuCategoryRepository = menuCategoryRepository;
        this.menuRepository = menuRepository;
        this.menuIngredientRepository = menuIngredientRepository;
        this.menuTagRepository = menuTagRepository;
        this.permissionRepository = permissionRepository;
        this.tableRepository = tableRepository;

    }

    @Override
    public Mono<Void> validate(RestaurantRequest request) {
        return Mono.fromRunnable(() -> {
            if (request.getName() == null || request.getName().trim().isEmpty())
                throw new IllegalArgumentException("Le nom du Restaurant est obligatoire");
            if (request.getEmail() == null || request.getEmail().trim().isEmpty())
                throw new IllegalArgumentException("L'email est obligatoire");
            if (request.getPhone() == null || request.getPhone().trim().isEmpty())
                throw new IllegalArgumentException("Le téléphone est obligatoire");
            if (request.getAddress() == null || request.getAddress().trim().isEmpty())
                throw new IllegalArgumentException("L'adresse est obligatoire");
            if (request.getCity() == null || request.getCity().trim().isEmpty())
                throw new IllegalArgumentException("La ville est obligatoire");
            if (request.getCapacity() == null || request.getCapacity() <= 0)
                throw new IllegalArgumentException("La capacité doit être positive");
            if (request.getCurrency() == null || request.getCurrency().trim().isEmpty())
                throw new IllegalArgumentException("La devise est obligatoire");
        });
    }

    @Override
    public Map<String, Object> extractUniqueFields(RestaurantRequest request) {
        return Map.of(
            "name", request.getName(),
            "email", request.getEmail(),
            "phone", request.getPhone()
        );
    }

    @Override
    public void applyRequestToEntity(Restaurant existing, RestaurantRequest request) {
        RestaurantMapper.updateEntityFromRequest(request, existing);
    }
    
    @Override
    public Mono<RestaurantResponse> createWithFiles(
            RestaurantRequest request,
            Mono<FilePart> logoMono,
            Mono<FilePart> coverMono) {

        return validate(request)
                .then(duplicateChecker.checkDuplicates(
                        "restaurant",
                        extractUniqueFields(request),
                        Restaurant.class,null,null
                ))
                .then(Mono.defer(() -> {
                    Restaurant entity = RestaurantMapper.toEntity(request);
                    entity.setLogo(null);
                    entity.setCoverImage(null);
                    return repository.save(entity);
                }))
                .flatMap(saved -> {
                    Mono<String> logoMonoFinal = (logoMono != null
                            ? logoMono.flatMap(f -> fileStorageUtil.storeFile(f, "logos"))
                            : Mono.justOrEmpty(""))
                            .defaultIfEmpty("");

                    Mono<String> coverMonoFinal = (coverMono != null
                            ? coverMono.flatMap(f -> fileStorageUtil.storeFile(f, "covers"))
                            : Mono.justOrEmpty(""))
                            .defaultIfEmpty("");

                    return Mono.zip(logoMonoFinal, coverMonoFinal)
                            .flatMap(tuple -> {
                                String newLogo = tuple.getT1();
                                String newCover = tuple.getT2();

                                saved.setLogo(newLogo);
                                saved.setCoverImage(newCover);

                                return repository.save(saved)
                                        .map(RestaurantMapper::toResponse)
                                        .onErrorResume(err -> {
                                            logger.error("❌ Erreur lors de la création : {}", err.getMessage());
                                            Mono<Void> cleanup = Mono.when(
                                                    newLogo != null && !newLogo.isBlank()
                                                            ? fileStorageUtil.deleteFile(newLogo)
                                                            : Mono.empty(),
                                                    newCover != null && !newCover.isBlank()
                                                            ? fileStorageUtil.deleteFile(newCover)
                                                            : Mono.empty()                                                            
                                            );
                                            return cleanup.then(Mono.error(new RuntimeException(
                                                    "Erreur lors de la création. Rollback effectué.",
                                                    err
                                            )));
                                        });
                            });
                });
    }

    @Transactional
    @Override
    public Mono<RestaurantResponse> updateWithFiles(UUID id,
                                                    RestaurantRequest request,
                                                    Mono<FilePart> logoMono,
                                                    Mono<FilePart> coverMono) {
        logger.debug("🎯 UPDATE WITH FILES - Début");
        return validate(request)
            .then(duplicateChecker.checkDuplicatesForUpdate(
                    "restaurant",
                    extractUniqueFields(request),
                    id,
                    Restaurant.class,null,null
            ))
            // Étape 1 : D'ABORD sauvegarder l'entité SANS les fichiers
            .then(Mono.defer(() -> {
                logger.debug("💾 Étape 1 : Sauvegarde de l'entité SANS fichiers");              
                Restaurant entity = RestaurantMapper.toEntity(request);
                // Ne pas modifier les fichiers encore
                return repository.save(entity);
            }))
            // Étape 2 : ENSUITE traiter les fichiers avec suppression des anciens
            .flatMap(savedEntity -> {
                logger.debug("✅ Entité sauvegardée, traitement des fichiers"+savedEntity.toString());
                
                // Sauvegarder les anciens chemins de fichiers AVANT traitement
                String oldLogo = savedEntity.getLogo();
                String oldCover = savedEntity.getCoverImage();
                
                logger.debug("📁 Ancien logo: " + (oldLogo != null && !oldLogo.isEmpty() ? "présent" : "absent"));
                logger.debug("📁 Ancien cover: " + (oldCover != null && !oldCover.isEmpty() ? "présent" : "absent"));
                
                return processFilesFlexible(logoMono, coverMono)
                    .flatMap(tuple -> {
                        String newLogo = tuple.getT1();
                        String newCover = tuple.getT2();
                        
                        logger.debug("📁 Fichiers traités - Nouveau logo: " + (!newLogo.isEmpty()) + ", Nouveau cover: " + (!newCover.isEmpty()));
                        
                        // Préparer les opérations de suppression des anciens fichiers
                        Mono<Void> deleteOldFilesMono = Mono.empty();
                        
                        // Si un nouveau logo est fourni ET qu'il y avait un ancien logo, supprimer l'ancien
                        if (!newLogo.isEmpty() && oldLogo != null && !oldLogo.isEmpty()) {
                            logger.debug("🗑️ Suppression ancien logo: " + oldLogo);
                            deleteOldFilesMono = deleteOldFilesMono.then(fileStorageUtil.deleteFile(oldLogo)
                                .doOnSuccess(deleted -> {
                                    if (deleted) logger.debug("✅ Ancien logo supprimé");
                                    else logger.debug("⚠️ Impossible de supprimer l'ancien logo");
                                })
                                .onErrorResume(error -> {
                                    logger.debug("⚠️ Erreur suppression ancien logo: " + error.getMessage());
                                    return Mono.just(false);
                                })
                                .then());
                        }
                        
                        // Si un nouveau cover est fourni ET qu'il y avait un ancien cover, supprimer l'ancien
                        if (!newCover.isEmpty() && oldCover != null && !oldCover.isEmpty()) {
                            logger.debug("🗑️ Suppression ancien cover: " + oldCover);
                            deleteOldFilesMono = deleteOldFilesMono.then(fileStorageUtil.deleteFile(oldCover)
                                .doOnSuccess(deleted -> {
                                    if (deleted) logger.debug("✅ Ancien cover supprimé");
                                    else logger.debug("⚠️ Impossible de supprimer l'ancien cover");
                                })
                                .onErrorResume(error -> {
                                    logger.debug("⚠️ Erreur suppression ancien cover: " + error.getMessage());
                                    return Mono.just(false);
                                })
                                .then());
                        }
                        
                        // Exécuter les suppressions puis mettre à jour l'entité
                        return deleteOldFilesMono
                            .then(Mono.defer(() -> {
                                // Mettre à jour l'entité avec les nouveaux fichiers
                                if (!newLogo.isEmpty()) {
                                    savedEntity.setLogo(newLogo);
                                    logger.debug("✅ Nouveau logo appliqué");
                                }
                                if (!newCover.isEmpty()) {
                                    savedEntity.setCoverImage(newCover);
                                    logger.debug("✅ Nouveau cover appliqué");
                                }
                                
                                // Sauvegarder l'entité avec les nouveaux fichiers
                                return repository.save(savedEntity);
                            }));
                    });
            })
            // Étape 3 : Synchronisation des horaires
            .flatMap(updatedWithFiles -> {
                logger.debug("⏰ Mise à jour des horaires");
                return updateRestaurantOpeningHours(updatedWithFiles.getId(), request.getOpeningHours())
                    .thenReturn(updatedWithFiles);
            })
            .map(finalEntity -> {
                logger.debug("📤 Mapping vers Response");
                return RestaurantMapper.toResponse(finalEntity);
            })
            .doOnSuccess(response -> logger.debug("🎉 UPDATE RÉUSSI"))
            .doOnError(error -> logger.debug("💥 UPDATE ÉCHOUÉ: " + error.getMessage()));
    }

    /**
     * Traitement FLEXIBLE des fichiers avec gestion d'absence
     */
    @Override
    public Mono<Tuple2<String, String>> processFilesFlexible(Mono<FilePart> logoMono, 
                                                             Mono<FilePart> coverMono) {
        logger.debug("🔄 Traitement flexible des fichiers");
        
        // Convertir les Mono<FilePart> en Mono<String> avec valeur par défaut si vide
        Mono<String> safeLogoMono = logoMono
                .hasElement()
                .flatMap(hasLogo -> {
                    logger.debug("📝 Logo présent: " + hasLogo);
                    return hasLogo ? 
                        processSingleFile(logoMono, "logos") : 
                        Mono.just("");
                })
                .doOnNext(logo -> logger.debug("🖼️ Logo résultat: " + (logo.isEmpty() ? "absent" : "présent")));

        Mono<String> safeCoverMono = coverMono
                .hasElement()
                .flatMap(hasCover -> {
                    logger.debug("📝 Cover présent: " + hasCover);
                    return hasCover ? 
                        processSingleFile(coverMono, "covers") : 
                        Mono.just("");
                })
                .doOnNext(cover -> logger.debug("🖼️ Cover résultat: " + (cover.isEmpty() ? "absent" : "présent")));

        return Mono.zip(safeLogoMono, safeCoverMono)
                .doOnNext(tuple -> {
                    logger.debug("🎉 Traitement fichiers terminé - Logo: " + 
                        (!tuple.getT1().isEmpty()) + ", Cover: " + (!tuple.getT2().isEmpty()));
                });
    }

   
    @Override
    public Mono<RestaurantResponse> changeState(UUID id) {
        return repository.findById(id)
                         .switchIfEmpty(Mono.error(new RuntimeException("Restaurant non trouvé")))
                         .flatMap(r -> {
                             r.setActive(!r.isActive());
                             return repository.save(r);
                         })
                         .map(RestaurantMapper::toResponse);
    }
    @Override
    public Mono<Void> deleteRestaurant(UUID id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Restaurant introuvable")))
                .flatMap(restaurant ->
                    repository.delete(restaurant)
                              .then(Mono.defer(() -> {
                                  Mono<Boolean> deleteLogo = fileStorageUtil.deleteFile(restaurant.getLogo())
                                          .onErrorResume(e -> {
                                              logger.warn("Erreur suppression logo: {}", e.getMessage());
                                              return Mono.just(false);
                                          });

                                  Mono<Boolean> deleteCover = fileStorageUtil.deleteFile(restaurant.getCoverImage())
                                          .onErrorResume(e -> {
                                              logger.warn("Erreur suppression cover: {}", e.getMessage());
                                              return Mono.just(false);
                                          });

                                  return Mono.when(deleteLogo, deleteCover).then();
                              }))
                );
    }
   
    
    @Override
    public Mono<RestaurantResponse> findById(UUID id) { 
        return repository.findById(id)
                .flatMap(restaurant ->
                    openingHourRepository.findOpeningHoursWithRestaurantId(id)
                            .collectList()
                            .map(openingHours -> {
                                restaurant.setOpeningHours(new HashSet<>(openingHours));
                                return RestaurantMapper.toResponse(restaurant);
                            })
                );
    }
    @Override
    public Mono<Void> updateRestaurantOpeningHours(UUID restaurantId, Set<OpeningHourRequest> openingHours) {
        if (openingHours == null || openingHours.isEmpty()) {
            return Mono.empty();
        }

        return Flux.fromIterable(openingHours)
                .flatMapSequential(hour -> {
                    Integer days = hour.getDays();
                    String open = hour.getOpen();
                    String close = hour.getClose();
                    Boolean isClosed = hour.getIsClosed();

                    return databaseClient.sql("""
                            SELECT oh.id
                            FROM opening_hour oh
                            INNER JOIN restaurant_opening_hour roh ON roh.opening_hour_id = oh.id
                            WHERE roh.restaurant_id = :restaurantId AND oh.days = :days
                            """)
                            .bind("restaurantId", restaurantId)
                            .bind("days", days)
                            .map((row, meta) -> row.get("id", UUID.class))
                            .one()
                            .flatMap(existingId -> {
                                return databaseClient.sql("""
                                        UPDATE opening_hour 
                                        SET open = :open, close = :close, is_closed = :isClosed
                                        WHERE id = :id
                                        """)
                                        .bind("open", open)
                                        .bind("close", close)
                                        .bind("isClosed", isClosed)
                                        .bind("id", existingId)
                                        .fetch()
                                        .rowsUpdated();
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                return databaseClient.sql("""
                                        INSERT INTO opening_hour (id, days, open, close, is_closed)
                                        VALUES (gen_random_uuid(), :days, :open, :close, :isClosed)
                                        RETURNING id
                                        """)
                                        .bind("days", days)
                                        .bind("open", open)
                                        .bind("close", close)
                                        .bind("isClosed", isClosed)
                                        .map((row, meta) -> row.get("id", UUID.class))
                                        .one()
                                        .flatMap(newId -> {
                                            return databaseClient.sql("""
                                                    INSERT INTO restaurant_opening_hour (restaurant_id, opening_hour_id)
                                                    VALUES (:restaurantId, :openingHourId)
                                                    """)
                                                    .bind("restaurantId", restaurantId)
                                                    .bind("openingHourId", newId)
                                                    .fetch()
                                                    .rowsUpdated();
                                        });
                            }));
                })
                .then();
    }

    @Override
    public Flux<RestaurantResponse> findAllRestaurants(boolean active) {
        logger.info("🔍 Finding all restaurants with active status: {}", active);
        
        return repository.findAllRestaurants(active)
            .doOnNext(restaurant -> 
                logger.debug("📋 Processing restaurant: {}", restaurant.getName()))
            .flatMap(restaurant -> 
                openingHourRepository.findOpeningHoursWithRestaurantId(restaurant.getId())
                    .collectList()
                    .doOnNext(openingHours -> 
                        logger.debug("🕒 Found {} opening hours for restaurant: {}", 
                                   openingHours.size(), restaurant.getName()))
                    .map(openingHours -> {
                        restaurant.setOpeningHours(new HashSet<>(openingHours));    
                                            
                        return restaurant;
                    })
                    
                    
                    .onErrorResume(error -> {
                        logger.warn("⚠️ Error loading opening hours for restaurant {}: {}", 
                                  restaurant.getName(), error.getMessage());
                        // Retourner le restaurant même sans les horaires d'ouverture
                        return Mono.just(restaurant);
                    })
               )
            .flatMap(restaurant -> 
             tableRepository.findByRestoAndStatus(restaurant.getId(),"available")
                .collectList()
                .doOnNext(tables -> 
                    logger.debug("🕒 Found {} tables for restaurant: {}", 
                    		tables.size(), restaurant.getName()))
                .map(tables2 -> {
                	
             RestaurantResponse   resto=	RestaurantMapper.toResponse(restaurant);  
             resto.setTables(TablesMapper.toResponses(tables2));
                                        
                    return resto;
                })
                
                
                .onErrorResume(error -> {
                    logger.warn("⚠️ Error loading opening hours for restaurant {}: {}", 
                              restaurant.getName(), error.getMessage());
                    // Retourner le restaurant même sans les horaires d'ouverture
                    return Mono.just(RestaurantMapper.toResponse(restaurant));
                })
           )
            


            
            
            
            
            .doOnComplete(() -> 
                logger.info("✅ Successfully found all restaurants with active status: {}", active))
            .doOnError(error -> 
                logger.error("❌ Error finding restaurants with active status {}: {}", 
                           active, error.getMessage()));
    }
	
    
    @Override
    public Flux<RestaurantResponse> findAllActive(Boolean active, EnumFilter type) {
        logger.debug("Finding all active restaurants with their roles");
        
        return super.findAllActive(active, type)
                .flatMap(resto -> {
                    return rolesRepository.findByResto(resto.getId())
                            .map(RolesMapper::toResponse)
                            .flatMap(role -> {
                                return permissionRepository.findPermissionsWithRole(role.getId())
                                        .collectList()
                                        .map(permissions -> {
                                            logger.debug("📋 Found {} permissions for role: {}", 
                                                    permissions.size(), role.getName());
                                            permissions.forEach(p -> 
                                                role.getPermissions().add(PermissionMapper.toResponse(p)));
                                            return role;
                                        });
                            })
                            .collectList()
                            .map(rolesList -> {
                                resto.setRoles(rolesList);
                                return resto;
                            });
                })
                .doOnNext(resto -> {
                    logger.debug("Processed restaurant: {} with {} roles", 
                        resto.getName(), 
                        resto.getRoles() != null ? resto.getRoles().size() : 0);
                })
                .doOnError(error -> 
                    logger.error("Error finding active restaurants with roles: {}", error.getMessage(), error)
                );
    }
	@Override
	protected String getFileField(Restaurant entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setFileField(Restaurant entity, String filePath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Mono<PageResponse<RestaurantHomeResponse>> searchHome(Map<String, Object> filters) {
	    logger.debug("Searching {} with pagination - filters: {}", entityName, filters);        
	    
	    return repository.searchWithPagination(filters)
	            .flatMap(page -> 
	                Flux.fromIterable(page.getContent())
	                    .flatMap(this::enrichRestaurantWithDetails)
	                    .collectList()
	                    .map(restaurantList -> new PageResponse<>(
	                        restaurantList,
	                        page.getCurrentPage(),
	                        page.getPageSize(),
	                        page.getTotalElements(),
	                        page.getTotalPages(),
	                        page.isHasPrevious(),
	                        page.isHasNext()
	                    ))
	            )
	            .doOnSuccess(result -> logger.debug("Pagination search completed for {} - {} results on page {}/{}", 
	                entityName, result.getContent().size(), result.getCurrentPage() + 1, result.getTotalPages()))
	            .doOnError(error -> logger.error("Error in pagination search for {} with filters {}: {}", 
	                entityName, filters, error.getMessage(), error));
	}

	private Mono<RestaurantHomeResponse> enrichRestaurantWithDetails(Restaurant rest) {
	    RestaurantHomeResponse dto = RestaurantMapper.toHomeResponse(rest);
	    
	    Mono<List<MenusResponse>> menusMono = menuRepository.findAllByRestoCode(rest.getId(),true)
	    		.flatMap(menu -> this.loadMenuWithCollections(menu))
	            .map(MenuMapper::toResponse)
	            .collectList();
	    
	    Mono<List<OpeningHourResponse>> openingHoursMono = openingHourRepository.findOpeningHoursWithRestaurantId(rest.getId())
	    		.map(OpeningHourMapper::toResponse)
	    		.collectList();
	    
	    
        

	    Mono<List<MenuCategoryResponse>> categoriesMono = menuCategoryRepository.findAllByRestoCode(rest.getId(),null)
	            .map(MenuCategoryMapper::toResponse)
	            .collectList();
	    
	    return Mono.zip(menusMono, categoriesMono,openingHoursMono)
	            .map(tuple -> {
	                dto.getMenus().addAll(tuple.getT1());
	                dto.getCategories().addAll(tuple.getT2());        		
	                dto.getOpeningHours().addAll(tuple.getT3());
	                return dto;
	            });
	}
	

	private Mono<Menus> loadMenuWithCollections(Menus menu) {
		
        return menuIngredientRepository.findByMenuId(menu.getId())
            .map(MenuIngredient::getIngredient)
            .collectList()
            .map(HashSet::new)
            .zipWith(
                menuTagRepository.findByMenuId(menu.getId())
                    .map(MenuTag::getTag)
                    .collectList()
                    .map(HashSet::new)
            )
            .map(tuple -> {
                menu.setIngredients(tuple.getT1());
                menu.setTags(tuple.getT2());

                return menu;
            });
    }

	@Override
	public Flux<RestaurantSpecialResponse> findAllRestaurants() {
        logger.info("🔍 Finding all restaurants with active status: {}", true);
        
        return repository.findAllRestaurants(true) 
            .map(RestaurantMapper::toResponseSpecial )
            .doOnComplete(() -> 
                logger.info("✅ Successfully found all restaurants with active status: {}", true))
            .doOnError(error -> 
                logger.error("❌ Error finding restaurants with active status {}: {}", 
                           true, error.getMessage()));
    }
	
	
}