package com.app.manage_restaurant.services.impl;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;

import com.app.manage_restaurant.cores.BaseServiceImpl;
import com.app.manage_restaurant.cores.EnumFilter;
import com.app.manage_restaurant.dtos.request.ReservationRequest;
import com.app.manage_restaurant.dtos.response.reservationResponse.ReservationResponse;
import com.app.manage_restaurant.entities.EnumReservation;
import com.app.manage_restaurant.entities.Reservation;
import com.app.manage_restaurant.mapper.ReservationMapper;
import com.app.manage_restaurant.repositories.ReservationRepository;
import com.app.manage_restaurant.services.GenericDuplicateChecker;
import com.app.manage_restaurant.services.ReservationService;
import com.app.manage_restaurant.utilities.FileStorageUtil;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ReservationServiceImpl extends BaseServiceImpl<Reservation, ReservationRequest, ReservationResponse, UUID> implements ReservationService {
    private final ReservationRepository repository;
    private final ReservationMapper reservationMapper;
    protected final R2dbcEntityTemplate template;

    public ReservationServiceImpl(ReservationRepository repository, 
    		              ReservationMapper reservationMapper,
                          FileStorageUtil fileStorageUtil,
                          R2dbcEntityTemplate template,
                          ReactiveExceptionHandler exceptionHandler,
                          GenericDuplicateChecker duplicateChecker) {
        super(repository, fileStorageUtil, template, reservationMapper::toEntity, reservationMapper::toResponse,
        		Reservation.class, "Reservation", exceptionHandler, duplicateChecker);
        this.repository = repository;
        this.reservationMapper = reservationMapper;
        this.template = template;

    }

    // ==================================
    // IMPLÉMENTATION DES MÉTHODES ABSTRAITES
    // ==================================

    @Override
    public Mono<Void> validate(ReservationRequest request) {
        return Mono.defer(() -> {
            // Validation du customer info
            if (request.getCustomerInfo() == null) {
                return Mono.error(new RuntimeException("Les informations client sont obligatoires"));
            }
            
            ReservationRequest.CustomerInfo customerInfo = request.getCustomerInfo();
            if (customerInfo.getFirstName() == null || customerInfo.getFirstName().trim().isEmpty()) {
                return Mono.error(new RuntimeException("Le prénom est obligatoire"));
            }
            if (customerInfo.getLastName() == null || customerInfo.getLastName().trim().isEmpty()) {
                return Mono.error(new RuntimeException("Le nom est obligatoire"));
            }
            if (customerInfo.getPhone() == null || customerInfo.getPhone().trim().isEmpty()) {
                return Mono.error(new RuntimeException("Le téléphone est obligatoire"));
            }         
           
            
            return Mono.empty();
        });
    }

    @Override
    public Map<String, Object> extractUniqueFields(ReservationRequest request) {
        // Les commandes n'ont généralement pas de champs uniques (sauf orderNumber généré automatiquement)
        return Map.of();
    }

    
    @Override
    protected String getFileField(Reservation entity) {
        // Les commandes n'ont généralement pas de fichier associé
        return null;
    }

    @Override
    protected void setFileField(Reservation entity, String filePath) {
        // Les commandes n'ont généralement pas de fichier associé
    }

    // ==================================
    // IMPLÉMENTATION DES MÉTHODES ORDER SERVICE
    // ==================================

    @Override
    public Mono<ReservationResponse> createReservation(ReservationRequest reservationRequest) {
        logger.info("📦 Creating new Reservation for customer: {}", reservationRequest.getCustomerInfo().getFirstName());      
        
        return validate(reservationRequest)
            .then(Mono.defer(() -> {
            	Reservation order = reservationMapper.toEntity(reservationRequest); 
                                return repository.save(order);
            }))     
            .map( reservationMapper ::toResponse)
            .doOnSuccess(reservationResponse -> 
                logger.info("✅ Reservation created successfully: {}", reservationResponse.getReservationNumber())
            )
            .doOnError(error -> 
                logger.error("❌ Error creating Reservation: {}", error.getMessage())
            );
    }
	@Override
	public Mono<ReservationResponse> getReservationById(UUID id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<ReservationResponse> getAllReservations() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public Mono<Void> cancelReservation(UUID orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<ReservationResponse> getReservationsByResto(UUID restoCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<ReservationResponse> getReservationsByCustomer(UUID customerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<ReservationResponse> getReservationsByStatus(String status) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public Mono<Long> countByRestoAndStatus(UUID restoCode, String status) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<ReservationResponse> getTodayReservations(UUID restoCode) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// ==================================
    // Recherche avec pagination
    // ==================================
    @Override
    public Mono<PageResponse<ReservationResponse>> search(Map<String, Object> filters,EnumFilter type) {
        logger.debug("Searching {} with pagination - filters: {}", entityName, filters);
        
        return repository.searchWithPagination(filters,type)
        		
                .map(page -> new PageResponse<>(
                    page.getContent().stream()
                        .map(s-> reservationMapper.toResponse(s)).toList(),
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
    public Mono<ReservationResponse> updateReservationStatus(UUID orderId, EnumReservation status) {
        return repository.findById(orderId)
            .switchIfEmpty(Mono.error(new RuntimeException("Reservation non trouvée avec l'ID: " + orderId)))
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
    private boolean isValidStatusTransition(EnumReservation currentStatus, EnumReservation newStatus) {
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
    
    private ReservationResponse convertToResponse(Reservation order) {
        // Votre logique de conversion Order → OrderResponse
    	ReservationResponse response = new ReservationResponse();
        return response;
    }

	@Override
	public Mono<ReservationResponse> assignDeliveryReservation(UUID id, UUID deliverInfo) {
		        return repository.findById(id)
		            .switchIfEmpty(Mono.error(new RuntimeException("Reservation non trouvée avec l'ID: " + id)))
		            .flatMap(order -> {
		                
		                // Mettre à jour la date de modification si nécessaire
		                order.setModifiedDate(System.currentTimeMillis());
		                                return template.update(order)
		                    .map(this::convertToResponse);
		            });
		    }
    
	
}