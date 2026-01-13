package com.app.manage_restaurant.repositories.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.app.manage_restaurant.cores.BaseRepositoryImpl;
import com.app.manage_restaurant.cores.EnumFilter;
import com.app.manage_restaurant.entities.Customer;
import com.app.manage_restaurant.entities.EnumReservation;
import com.app.manage_restaurant.entities.Reservation;
import com.app.manage_restaurant.repositories.CustomerRepository;
import com.app.manage_restaurant.repositories.PrsnlRepository;
import com.app.manage_restaurant.repositories.ReservationRepository;
import com.app.manage_restaurant.repositories.TablesRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ReservationRepositoryImpl extends BaseRepositoryImpl<Reservation, UUID> implements ReservationRepository {

    private final CustomerRepository customerRepository;
	private final PrsnlRepository prnslRepository;
	private final TablesRepository tableRepository;

    private final PasswordEncoder passwordEncoder;
    public ReservationRepositoryImpl(R2dbcEntityTemplate template,TablesRepository tableRepository,PrsnlRepository prnslRepository, CustomerRepository customerRepository,PasswordEncoder passwordEncoder) {
        super(template, Reservation.class);
        this.customerRepository = customerRepository;
        this.prnslRepository =  prnslRepository;
        this.passwordEncoder = passwordEncoder;
        this.tableRepository = tableRepository;
        excludeMethodFromFiltering("findTodayOrders");
        excludeMethodFromFiltering("countByRestoCode");
    }

    @Override
    public Mono<Reservation> save(Reservation reservation) {
        logger.debug("Starting reservation save process for customer phone: {}", reservation.getPhone());
        
        return Mono.just(reservation)
            .flatMap(this::findOrCreateCustomer)
            .map(customer -> {
                updateReservationWithCustomerInfo(reservation, customer);
                return reservation;
            })
            .flatMap(this::generateReservationNumberIfNeeded)
            .flatMap(reservationToSave -> {
                logger.debug("Saving reservation: {}", reservationToSave);
                return template.insert(reservationToSave);
            })
            .flatMap(savedReservation -> 
                updateTableStatus(savedReservation)
                    .thenReturn(savedReservation)
            )
            .doOnSuccess(savedOrder -> 
                logger.info("Reservation saved successfully with ID: {} and number: {}", 
                    savedOrder.getId(), savedOrder.getReservationNumber()))
            .doOnError(error -> 
                logger.error("Error saving reservation: {}", error.getMessage(), error));
    }

    private Mono<Void> updateTableStatus(Reservation reservation) {
        return tableRepository.findByRestoAndCapacity(
                reservation.getRestoCode(), 
                reservation.getCapacity())
            .flatMap(table -> {
                table.setStatus("reserved");
                return tableRepository.save(table);
            })
            .then(); // Convertir en Mono<Void>
    }

    /**
     * Trouve un customer existant par téléphone ou en crée un nouveau
     */
    private Mono<Customer> findOrCreateCustomer(Reservation reservation) {      
        return customerRepository.findByPhone(reservation.getPhone())
            .switchIfEmpty(Mono.defer(() -> {
                logger.debug("No existing customer found with phone: {}, creating new one", reservation.getPhone());
                Customer newCustomer = createCustomerFromReservation(reservation);
                String pwd = (newCustomer.getPassword() != null && !newCustomer.getPassword().isEmpty())
                        ? newCustomer.getPassword()
                        : newCustomer.getPhone();
                newCustomer.setPassword(passwordEncoder.encode(pwd));
                return customerRepository.save(newCustomer)
                    .doOnSuccess(savedCustomer -> 
                        logger.debug("New customer created with ID: {}", savedCustomer.getId()));
            }))
            .doOnSuccess(customer -> 
                logger.debug("Customer found/created with ID: {}", customer.getId()));
    }

    /**
     * Crée un nouveau customer à partir des informations de l'order
     */
    private Customer createCustomerFromReservation(Reservation order) {
        Customer customer = new Customer();
        customer.setFirstname(order.getFirstName());
        customer.setLastname(order.getLastName());
        customer.setPhone(order.getPhone());
        customer.setEmail(order.getEmail());
        customer.setActive(true);
        return customer;
    }

    /**
     * Met à jour l'order avec les informations du customer
     */
    private void updateReservationWithCustomerInfo(Reservation order, Customer customer) {
        order.setCustomerId(customer.getId());
        // Mettre à jour les informations de contact si elles sont plus complètes chez le customer
        if (customer.getFirstname() != null) order.setFirstName(customer.getFirstname());
        if (customer.getLastname() != null) order.setLastName(customer.getLastname());
        if (customer.getPhone() != null) order.setPhone(customer.getPhone());
        if (customer.getEmail() != null) order.setEmail(customer.getEmail());
        if (customer.getId() != null) order.setOwnerCode(customer.getId());

        logger.debug("Order updated with customer ID: {}", customer.getId());
    }

    /**
     * Génère un numéro de commande si nécessaire
     */
    private Mono<Reservation> generateReservationNumberIfNeeded(Reservation order) {
        if (order.getReservationNumber() != null && !order.getReservationNumber().trim().isEmpty()) {
            return Mono.just(order);
        }
        
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String orderNumber = "ORD-" + timestamp + "-" + randomSuffix;
        
        order.setReservationNumber(orderNumber);
        logger.debug("Generated reservation number: {}", orderNumber);
        
        return Mono.just(order);
    }

    
    private Mono<Reservation> findByCustomerIdAndPaymentId(UUID customerId, UUID paymentId) {
        Criteria criteria = Criteria.where("customer_id").is(customerId)
                                  .and("payment_id").is(paymentId);
        return applyGlobalFilter(Query.query(criteria), "findByCustomerIdAndPaymentId",EnumFilter.ALL)
                .flatMap(query -> template.selectOne(query, Reservation.class));
    }

    // Implémentation des autres méthodes de l'interface
    @Override
    public Flux<Reservation> findByRestoCode(UUID restoCode) {
        logger.debug("Finding orders by restoCode: {}", restoCode);
        
        return applyGlobalFilter(Query.query(Criteria.where("resto_code").is(restoCode)), "findByRestoCode",EnumFilter.ALL)
                .flatMapMany(query -> template.select(query, Reservation.class))
                .doOnComplete(() -> logger.debug("Completed finding orders by restoCode: {}", restoCode));
    }

    @Override
    public Flux<Reservation> findByCustomerId(UUID customerId) {
        logger.debug("Finding orders by customerId: {}", customerId);
        
        return applyGlobalFilter(Query.query(Criteria.where("customer_id").is(customerId)), "findByCustomerId",EnumFilter.ALL)
                .flatMapMany(query -> template.select(query, Reservation.class))
                .doOnComplete(() -> logger.debug("Completed finding orders by customerId: {}", customerId));
    }

    @Override
    public Flux<Reservation> findByStatus(String status) {
        logger.debug("Finding orders by status: {}", status);
        
        return applyGlobalFilter(Query.query(Criteria.where("status").is(status)), "findByStatus",EnumFilter.ALL)
                .flatMapMany(query -> template.select(query, Reservation.class))
                .doOnComplete(() -> logger.debug("Completed finding Reservation by status: {}", status));
    }

    @Override
    public Flux<Reservation> findByRestoCodeAndStatus(UUID restoCode, String status) {
        logger.debug("Finding Reservation by restoCode: {} and status: {}", restoCode, status);
        
        Criteria criteria = Criteria.where("resto_code").is(restoCode)
                                  .and("status").is(status);
        
        return applyGlobalFilter(Query.query(criteria), "findByRestoCodeAndStatus",EnumFilter.ALL)
                .flatMapMany(query -> template.select(query, Reservation.class))
                .doOnComplete(() -> logger.debug("Completed finding orders by restoCode and status: {}, {}", restoCode, status));
    }

   
    @Override
    public Mono<Long> countByRestoCode(UUID restoCode) {
        logger.debug("Counting orders by restoCode: {}", restoCode);
        
        return applyGlobalFilter(Query.query(Criteria.where("resto_code").is(restoCode)), "countByRestoCode",EnumFilter.ALL)
                .flatMap(query -> template.count(query, Reservation.class))
                .doOnSuccess(count -> logger.debug("Counted {} orders for restoCode: {}", count, restoCode));
    }

    @Override
    public Mono<Long> countByRestoCodeAndStatus(UUID restoCode, String status) {
        logger.debug("Counting orders by restoCode: {} and status: {}", restoCode, status);
        
        Criteria criteria = Criteria.where("resto_code").is(restoCode)
                                  .and("status").is(status);
        
        return applyGlobalFilter(Query.query(criteria), "countByRestoCodeAndStatus",EnumFilter.ALL)
                .flatMap(query -> template.count(query, Reservation.class))
                .doOnSuccess(count -> logger.debug("Counted {} orders for restoCode: {} and status: {}", count, restoCode, status));
    }

    

    // Méthode utilitaire pour le mapping
    private Reservation mapRowToOrder(io.r2dbc.spi.Row row) {
    	Reservation order = new Reservation();        
        order.setId(getUuid(row, "id"));
        order.setReservationNumber(getString(row, "reservation_number"));
        order.setCustomerId(getUuid(row, "customer_id"));
        order.setDate(getLong(row, "reservation_date"));
       // order.setStatus(getString(row, "status"));
        order.setLastName(getString(row, "lastname"));
        order.setFirstName(getString(row, "firstname"));
        order.setPhone(getString(row, "customer_phone"));
        order.setEmail(getString(row, "customer_email"));
        order.setTime(getString(row, "reservation_time"));
        order.setCapacity(getInteger(row, "capacity"));
        order.setCommentaire(getString(row, "commentaire"));
        order.setActive(getBoolean(row, "active"));
        
        return order;
    }

    // Méthodes helper pour extraire les valeurs avec gestion des nulls
    private UUID getUuid(io.r2dbc.spi.Row row, String column) {
        try {
            return row.get(column, UUID.class);
        } catch (Exception e) {
            return null;
        }
    }

    private String getString(io.r2dbc.spi.Row row, String column) {
        try {
            return row.get(column, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    private Double getDouble(io.r2dbc.spi.Row row, String column) {
        try {
            return row.get(column, Double.class);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private Integer getInteger(io.r2dbc.spi.Row row, String column) {
        try {
            return row.get(column, Integer.class);
        } catch (Exception e) {
            return 0;
        }
    }

    private Long getLong(io.r2dbc.spi.Row row, String column) {
        try {
            return row.get(column, Long.class);
        } catch (Exception e) {
            return 0L;
        }
    }

    private Boolean getBoolean(io.r2dbc.spi.Row row, String column) {
        try {
            return row.get(column, Boolean.class);
        } catch (Exception e) {
            return true;
        }
    }

	@Override
	public Flux<Reservation> findByCustomerPhoneOrEmail(String phone, String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<Reservation> findByTotalPriceGreaterThanEqual(Double minPrice) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<Reservation> findByStatusIn(Iterable<String> statuses) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<Reservation> findByTotalItemsGreaterThanEqual(Integer minItems) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<Integer> updateOrderStatus(UUID orderId, String status, LocalDateTime modifiedDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<Reservation> findTodayOrders(UUID restoCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<Reservation> findByRestoCodeAndDateRange(UUID restoCode, LocalDateTime startDate, LocalDateTime endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	// =====================================
	// Méthode de recherche avec résultat paginé
	// =====================================
	@Override
	public Mono<PageResponse<Reservation>> searchWithPagination(Map<String, Object> filters,EnumFilter type) {
	    return Mono.zip(
	        super.search(filters, type).collectList(),
	        super.count(filters,type)
	    ).flatMap(tuple -> {
	        List<Reservation> content = tuple.getT1();
	        long totalElements = tuple.getT2();
	       
	        // Chargement parallèle des OrderItem
	        return Flux.fromIterable(content)
	            .collectList()
	            .map(enrichedOrders -> createPageResponse(enrichedOrders, filters, totalElements));
	    });
	}

	
	private PageResponse<Reservation> createPageResponse(List<Reservation> content, 
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

	@Override
	public Flux<Reservation> search(Map<String, Object> filters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<Reservation> findByActive(Boolean active) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
     * Valide la transition entre deux statuts selon les règles métier
     */
    private boolean isValidStatusTransition(EnumReservation currentStatus, EnumReservation newStatus) {
        // Règles de base - à adapter selon vos besoins
        
        // 1. Si la commande est déjà terminée ou annulée, pas de changement
        if (currentStatus.isCompleted() || currentStatus.isCancelled()) {
            return false;
        }
        
        // 2. Si le nouveau statut est le même que l'actuel
        if (currentStatus == newStatus) {
            return true; // C'est OK de "re-mettre" le même statut
        }
        
        // 3. Vérifier les transitions autorisées
        switch (currentStatus) {
            case PENDING:
            
                
            case CONFIRMED:
                return  newStatus == EnumReservation.CANCELLED;
                
            
           
                
            default:
                return false;
        }
        
        
    }
	
}