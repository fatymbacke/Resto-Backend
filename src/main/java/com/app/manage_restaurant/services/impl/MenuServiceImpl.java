package com.app.manage_restaurant.services.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.manage_restaurant.cores.BaseServiceImpl;
import com.app.manage_restaurant.cores.EnumFilter;
import com.app.manage_restaurant.dtos.request.MenusAvailableRequest;
import com.app.manage_restaurant.dtos.request.MenusRequest;
import com.app.manage_restaurant.dtos.response.MenusResponse;
import com.app.manage_restaurant.entities.MenuIngredient;
import com.app.manage_restaurant.entities.MenuTag;
import com.app.manage_restaurant.entities.Menus;
import com.app.manage_restaurant.mapper.MenuMapper;
import com.app.manage_restaurant.repositories.MenuIngredientRepository;
import com.app.manage_restaurant.repositories.MenuRepository;
import com.app.manage_restaurant.repositories.MenuTagRepository;
import com.app.manage_restaurant.services.GenericDuplicateChecker;
import com.app.manage_restaurant.services.MenuService;
import com.app.manage_restaurant.utilities.FileStorageUtil;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MenuServiceImpl extends BaseServiceImpl<Menus, MenusRequest, MenusResponse, UUID> implements MenuService {
    private MenuRepository menuRepository;
    private final MenuIngredientRepository menuIngredientRepository;
    private final MenuTagRepository menuTagRepository;         
    private final Logger logger;
    protected final R2dbcEntityTemplate template;

    public MenuServiceImpl(MenuRepository menuRepository,    		
                         FileStorageUtil fileStorageUtil,
                         MenuIngredientRepository menuIngredientRepository,
                         MenuTagRepository menuTagRepository,
                         R2dbcEntityTemplate template,
                         ReactiveExceptionHandler exceptionHandler,
                         GenericDuplicateChecker duplicateChecker) {
        super(menuRepository, fileStorageUtil, template, MenuMapper::toEntity, MenuMapper::toResponse,
                Menus.class, "Menus", exceptionHandler, duplicateChecker);
        this.menuRepository = menuRepository;
        this.menuTagRepository = menuTagRepository;
        this.menuIngredientRepository = menuIngredientRepository;
        this.template = template;
        this.logger = LoggerFactory.getLogger(MenuServiceImpl.class);
    }

    @Override
    public Mono<Void> validate(MenusRequest request) {
        return Mono.fromRunnable(() -> {
            if (request.getName() == null || request.getName().trim().isEmpty())
                throw new IllegalArgumentException("Le nom du Menu est obligatoire");
            if (request.getCategory() == null || request.getCategory().trim().isEmpty())
                throw new IllegalArgumentException("La catégorie est obligatoire");
        });
    }

    @Override
    public Map<String, Object> extractUniqueFields(MenusRequest request) {
        return Map.of(
            "name", request.getName()
        );
    }

    @Override
    public void applyRequestToEntity(Menus existing, MenusRequest request) {
        MenuMapper.updateEntityFromRequest(request, existing);
    }

    @Override
    protected String getFileField(Menus entity) {
        return entity.getImageUrl();
    }

    @Override
    protected void setFileField(Menus entity, String filePath) {
        entity.setImageUrl(filePath);
    }

    @Override
    public Mono<Menus> loadMenuWithCollections(Menus menu) {
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
    @Transactional
    public Mono<MenusResponse> saveMenu(MenusRequest request, Mono<FilePart> file, String folder) {
        logger.info("💾 Saving new Menu: '{}'", request != null ? request.getName() : "null");
        return this.save(request, file, folder)
            .flatMap(savedMenu -> {
                UUID menuId = savedMenu.getId();
                List<Mono<?>> allOperations = new ArrayList<>();

                // Préparer les opérations pour les ingrédients
                if (request.getIngredients() != null && !request.getIngredients().isEmpty()) {
                    int order = 0;
                    for (String ingredient : request.getIngredients()) {
                        MenuIngredient menuIngredient = new MenuIngredient();
                        menuIngredient.setIngredient(ingredient);
                        menuIngredient.setMenuId(menuId);
                        menuIngredient.setOrder(order++);
                        menuIngredient.setActive(true);
                        
                        allOperations.add(menuIngredientRepository.save(menuIngredient)
                            .doOnSuccess(ing -> logger.debug("✅ Saved ingredient: {} for menu: {}", ingredient, menuId))
                            .doOnError(error -> logger.error("❌ Failed to save ingredient: {} for menu: {}", ingredient, menuId, error))
                        );
                    }
                }

                // Préparer les opérations pour les tags
                if (request.getTags() != null && !request.getTags().isEmpty()) {
                    for (String tag : request.getTags()) {
                        MenuTag menuTag = new MenuTag();
                        menuTag.setTag(tag);
                        menuTag.setMenuId(menuId);
                        menuTag.setActive(true);
                        
                        allOperations.add(menuTagRepository.save(menuTag)
                            .doOnSuccess(t -> logger.debug("✅ Saved tag: {} for menu: {}", tag, menuId))
                            .doOnError(error -> logger.error("❌ Failed to save tag: {} for menu: {}", tag, menuId, error))
                        );
                    }
                }

                // Si aucune opération supplémentaire, retourner directement
                if (allOperations.isEmpty()) {
                    return Mono.just(savedMenu);
                }

                // Exécuter toutes les opérations et attendre leur completion
                return Mono.when(allOperations)
                          .thenReturn(savedMenu);
            })
            .doOnSuccess(response -> {
                logger.info("✅ Successfully saved new Menu: '{}' with ID: {}", 
                           response.getName(), response.getId());
                logger.info("✅ Successfully saved all ingredients and tags for Menu ID: {}", 
                           response.getId());
            })
            .doOnError(error -> {
                logger.error("❌ Error saving Menu '{}': {}", 
                           request != null ? request.getName() : "null", error.getMessage());
                logger.error("🚨 Transaction rolled back completely for Menu: {}", 
                           request != null ? request.getName() : "null");
            });
    }

    @Override
    @Transactional
    public Mono<MenusResponse> updateMenu(UUID id, MenusRequest request, Mono<FilePart> file, String folder) {
        logger.info("🔄 Updating Menu ID: {} with name: '{}'", 
                   id, request != null ? request.getName() : "null");        
        request.setId(id);       
        return super.update(id,request, file, folder)
            .flatMap(updatedMenu -> {
                UUID menuId = updatedMenu.getId();                
                // Étape 1: Créer d'abord les nouvelles relations
                Mono<Void> createNewRelations = createNewRelations(menuId, request);                
                // Étape 2: Supprimer les anciennes relations seulement après le succès
                Mono<Void> deleteOldRelations = deleteAllOldRelations(menuId);                
                return createNewRelations
                    .then(deleteOldRelations)
                    .thenReturn(updatedMenu);
            })
            .doOnSuccess(response -> 
                logger.info("✅ Successfully updated Menu: '{}' with ID: {}", 
                           response.getName(), response.getId()))
            .doOnError(error -> {
                logger.error("❌ Error updating Menu ID {}: {}", id, error.getMessage());
                logger.info("🔄 Rollback completed - old data preserved for Menu ID: {}", id);
            });
    }

    private Mono<Void> createNewRelations(UUID menuId, MenusRequest request) {
        List<Mono<?>> operations = new ArrayList<>();
        
        // Créer les nouveaux ingrédients
        if (request.getIngredients() != null && !request.getIngredients().isEmpty()) {
            int order = 0;
            for (String ingredient : request.getIngredients()) {
                MenuIngredient menuIngredient = new MenuIngredient();
                menuIngredient.setIngredient(ingredient);
                menuIngredient.setMenuId(menuId);
                menuIngredient.setOrder(order++);
                menuIngredient.setActive(true);
                operations.add(menuIngredientRepository.save(menuIngredient)
                    .doOnSuccess(ing -> logger.debug("✅ Created new ingredient: {} for menu: {}", ingredient, menuId))
                );
            }
            logger.debug("🔄 Creating {} new ingredients for menu: {}", request.getIngredients().size(), menuId);
        }
        
        // Créer les nouveaux tags
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            for (String tag : request.getTags()) {
                MenuTag menuTag = new MenuTag();
                menuTag.setTag(tag);
                menuTag.setMenuId(menuId);
                menuTag.setActive(true);
                operations.add(menuTagRepository.save(menuTag)
                    .doOnSuccess(t -> logger.debug("✅ Created new tag: {} for menu: {}", tag, menuId))
                );
            }
            logger.debug("🔄 Creating {} new tags for menu: {}", request.getTags().size(), menuId);
        }
        
        return Mono.when(operations)
                  .doOnSuccess(v -> logger.debug("✅ Successfully created all new relations for menu: {}", menuId))
                  .doOnError(error -> logger.error("❌ Failed to create new relations for menu: {}", menuId, error));
    }

    private Mono<Void> deleteAllOldRelations(UUID menuId) {
        // Supprimer tous les anciens ingrédients et tags en une seule opération
        Mono<Void> deleteIngredients = menuIngredientRepository.deleteByMenuId(menuId)
            .doOnSuccess(v -> logger.debug("✅ Deleted all old ingredients for menu: {}", menuId))
            .doOnError(error -> logger.error("❌ Failed to delete old ingredients for menu: {}", menuId, error));
        
        Mono<Void> deleteTags = menuTagRepository.deleteByMenuId(menuId)
            .doOnSuccess(v -> logger.debug("✅ Deleted all old tags for menu: {}", menuId))
            .doOnError(error -> logger.error("❌ Failed to delete old tags for menu: {}", menuId, error));
        
        return Mono.when(deleteIngredients, deleteTags)
                  .doOnSuccess(v -> logger.debug("✅ Successfully deleted all old relations for menu: {}", menuId));
    }

    @Override
    public Flux<MenusResponse> findAllMenus(boolean active) {
        return this.findAll()
            .filter(menu -> menu.isActive() == active)
            .flatMap(menu -> this.loadMenuWithCollections(MenuMapper.toEntity(menu))
                .map(MenuMapper::toResponse)
            );
    }
    
    @Override
    public Flux<MenusResponse> searchAll(Map<String, Object> filters, EnumFilter type) {
    	// TODO Auto-generated method stub
    	return super.searchAll(filters,type).flatMap(menu -> this.loadMenuWithCollections(MenuMapper.toEntity(menu))
                .map(MenuMapper::toResponse)
            );
    }

    @Override
    public Mono<MenusResponse> toggleMenuAvailability(MenusAvailableRequest request) {
        // Créer une entité avec seulement l'ID et le statut inversé
        return menuRepository.findById(request.getId())
                .flatMap(menu -> {
                    menu.setIsAvailable(request.getAvailable());
                    return menuRepository.save(menu);
                })
                .map(MenuMapper::toResponse)
                .onErrorResume(error -> {
                    logger.error("Erreur lors du toggle de disponibilité: {}", error.getMessage());
                    return Mono.error(new RuntimeException("Erreur lors du changement de disponibilité: " + error.getMessage()));
                });
    }
}