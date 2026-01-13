package com.app.manage_restaurant.services.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;

import com.app.manage_restaurant.cores.BaseServiceImpl;
import com.app.manage_restaurant.cores.EnumFilter;
import com.app.manage_restaurant.dtos.request.OrderRequest;
import com.app.manage_restaurant.dtos.request.OrderRequest.OrderItemRequest;
import com.app.manage_restaurant.dtos.response.orderResponse.OrderResponse;
import com.app.manage_restaurant.dtos.response.orderResponse.OrderSummaryResponse;
import com.app.manage_restaurant.entities.EnumOrder;
import com.app.manage_restaurant.entities.Order;
import com.app.manage_restaurant.entities.OrderItem;
import com.app.manage_restaurant.mapper.OrderMapper;
import com.app.manage_restaurant.repositories.OrderItemRepository;
import com.app.manage_restaurant.repositories.OrderRepository;
import com.app.manage_restaurant.services.GenericDuplicateChecker;
import com.app.manage_restaurant.services.OrderService;
import com.app.manage_restaurant.utilities.FileStorageUtil;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderServiceImpl extends BaseServiceImpl<Order, OrderRequest, OrderResponse, UUID> implements OrderService {
    private final OrderRepository repository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    protected final R2dbcEntityTemplate template;

    public OrderServiceImpl(OrderRepository repository, 
                          OrderItemRepository orderItemRepository,
                          OrderMapper orderMapper,
                          FileStorageUtil fileStorageUtil,
                          R2dbcEntityTemplate template,
                          ReactiveExceptionHandler exceptionHandler,
                          GenericDuplicateChecker duplicateChecker) {
        super(repository, fileStorageUtil, template, orderMapper::toEntity, orderMapper::toResponse,
              Order.class, "Order", exceptionHandler, duplicateChecker);
        this.repository = repository;
        this.orderItemRepository = orderItemRepository;
        this.orderMapper = orderMapper;
        this.template = template;

    }

    // ==================================
    // IMPLÉMENTATION DES MÉTHODES ABSTRAITES
    // ==================================

    @Override
    public Mono<Void> validate(OrderRequest request) {
        return Mono.defer(() -> {
            // Validation du customer info
            if (request.getCustomerInfo() == null) {
                return Mono.error(new RuntimeException("Les informations client sont obligatoires"));
            }
            
            OrderRequest.CustomerInfo customerInfo = request.getCustomerInfo();
            if (customerInfo.getFirstName() == null || customerInfo.getFirstName().trim().isEmpty()) {
                return Mono.error(new RuntimeException("Le prénom est obligatoire"));
            }
            if (customerInfo.getLastName() == null || customerInfo.getLastName().trim().isEmpty()) {
                return Mono.error(new RuntimeException("Le nom est obligatoire"));
            }
            if (customerInfo.getPhone() == null || customerInfo.getPhone().trim().isEmpty()) {
                return Mono.error(new RuntimeException("Le téléphone est obligatoire"));
            }
            if (customerInfo.getAddress() == null || customerInfo.getAddress().trim().isEmpty()) {
                return Mono.error(new RuntimeException("L'adresse est obligatoire"));
            }
            if (customerInfo.getCity() == null || customerInfo.getCity().trim().isEmpty()) {
                return Mono.error(new RuntimeException("La ville est obligatoire"));
            }
            
            // Validation des articles
            if (request.getOrderItems() == null || request.getOrderItems().isEmpty()) {
                return Mono.error(new RuntimeException("La commande doit contenir au moins un article"));
            }
            
            // Validation des prix
            if (request.getTotalPrice() == null || request.getTotalPrice() <= 0) {
                return Mono.error(new RuntimeException("Le prix total doit être supérieur à 0"));
            }
            
            if (request.getTotalItems() == null || request.getTotalItems() <= 0) {
                return Mono.error(new RuntimeException("Le nombre total d'articles doit être supérieur à 0"));
            }
            
            return Mono.empty();
        });
    }

    @Override
    public Map<String, Object> extractUniqueFields(OrderRequest request) {
        // Les commandes n'ont généralement pas de champs uniques (sauf orderNumber généré automatiquement)
        return Map.of();
    }

    
    @Override
    protected String getFileField(Order entity) {
        // Les commandes n'ont généralement pas de fichier associé
        return null;
    }

    @Override
    protected void setFileField(Order entity, String filePath) {
        // Les commandes n'ont généralement pas de fichier associé
    }

    // ==================================
    // IMPLÉMENTATION DES MÉTHODES ORDER SERVICE
    // ==================================

    @Override
    public Mono<OrderResponse> createOrder(OrderRequest orderRequest) {
        logger.info("📦 Creating new order for customer: {}", orderRequest.getCustomerInfo().getFirstName());      
        
        return validate(orderRequest)
            .then(Mono.defer(() -> {
                Order order = orderMapper.toEntity(orderRequest); 
                                return repository.save(order);
            }))
            .flatMap(savedOrder -> {
                List<OrderItem> orderItems = orderMapper.toOrderItemEntities(
                    orderRequest.getOrderItems(), 
                    savedOrder 
                );
                
                // Sauvegarde séquentielle des items
                return Flux.fromIterable(orderItems)
                    .flatMap(orderItemRepository::save)
                    .collectList()
                    .flatMap(savedItems -> {
                        return Mono.just(savedOrder)
                            .map(updatedOrder -> orderMapper.toResponseWithItems(updatedOrder, savedItems));
                    });
            })
            .doOnSuccess(orderResponse -> 
                logger.info("✅ Order created successfully: {}", orderResponse.getOrderNumber())
            )
            .doOnError(error -> 
                logger.error("❌ Error creating order: {}", error.getMessage())
            );
    }
	@Override
	public Mono<OrderResponse> getOrderById(UUID id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<OrderResponse> getAllOrders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<OrderResponse> updateOrderStatus(UUID orderId, String status) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<Void> cancelOrder(UUID orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<OrderResponse> getOrdersByResto(UUID restoCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<OrderResponse> getOrdersByCustomer(UUID customerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<OrderResponse> getOrdersByStatus(String status) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<OrderSummaryResponse> getOrderSummariesByResto(UUID restoCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<OrderResponse> addOrderItem(UUID orderId, OrderItemRequest itemRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<OrderResponse> removeOrderItem(UUID orderId, UUID itemId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<Long> countByRestoAndStatus(UUID restoCode, String status) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<OrderResponse> getTodayOrders(UUID restoCode) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// ==================================
    // Recherche avec pagination
    // ==================================
    @Override
    public Mono<PageResponse<OrderResponse>> search(Map<String, Object> filters,EnumFilter type) {
        logger.debug("Searching {} with pagination - filters: {}", entityName, filters);
        
        return repository.searchWithPagination(filters,type)
        		
                .map(page -> new PageResponse<>(
                    page.getContent().stream()
                        .map(s-> orderMapper.toResponseWithItems(s, s.getOrderItems()) ).toList(),
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

	
	
	@Override
    public Mono<OrderResponse> updateOrderStatus(UUID orderId, EnumOrder status) {
        return repository.findById(orderId)
            .switchIfEmpty(Mono.error(new RuntimeException("Commande non trouvée avec l'ID: " + orderId)))
            .flatMap(order -> {
                // Validation des transitions de statut
                if (!isValidStatusTransition(order.getStatus(), status)) {
                    return Mono.error(new RuntimeException(
                        "Transition de statut invalide: " + order.getStatus() + " → " + status));
                }
                
                order.setStatus(status);                
                // Mettre à jour la date de modification si nécessaire
                order.setModifiedDate(System.currentTimeMillis());
                                return template.update(order)
                    .map(this::convertToResponse);
            });
    }
    
    /**
     * Valide la transition entre deux statuts
     */
    private boolean isValidStatusTransition(EnumOrder currentStatus, EnumOrder newStatus) {
        // Implémentez votre logique de validation ici
        // Exemple basique :
        
        // Ne pas permettre de revenir en arrière
    //    if (currentStatus.isCompleted() || currentStatus.isCancelled()) {
    //        return false;
    //    }
        
        // Autoriser toutes les transitions pour l'exemple
        // Vous devrez adapter selon vos règles métier
        return true;
    }
    
    private OrderResponse convertToResponse(Order order) {
        // Votre logique de conversion Order → OrderResponse
        OrderResponse response = new OrderResponse();
        return response;
    }

	@Override
	public Mono<OrderResponse> assignDeliveryOrder(UUID id, UUID deliverInfo) {
		        return repository.findById(id)
		            .switchIfEmpty(Mono.error(new RuntimeException("Commande non trouvée avec l'ID: " + id)))
		            .flatMap(order -> {
		                
		                order.setDeliver_info(deliverInfo);                
		                // Mettre à jour la date de modification si nécessaire
		                order.setModifiedDate(System.currentTimeMillis());
		                                return template.update(order)
		                    .map(this::convertToResponse);
		            });
		    }
    
	
}