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
import com.app.manage_restaurant.entities.EnumOrder;
import com.app.manage_restaurant.entities.Order;
import com.app.manage_restaurant.repositories.CustomerRepository;
import com.app.manage_restaurant.repositories.OrderItemRepository;
import com.app.manage_restaurant.repositories.OrderRepository;
import com.app.manage_restaurant.repositories.PrsnlRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class OrderRepositoryImpl extends BaseRepositoryImpl<Order, UUID> implements OrderRepository {

    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;
	 private final PrsnlRepository prnslRepository;
    private final PasswordEncoder passwordEncoder;
    public OrderRepositoryImpl(R2dbcEntityTemplate template,OrderItemRepository orderItemRepository,PrsnlRepository prnslRepository, CustomerRepository customerRepository,PasswordEncoder passwordEncoder) {
        super(template, Order.class);
        this.customerRepository = customerRepository;
        this.orderItemRepository = orderItemRepository;
        this.prnslRepository =  prnslRepository;
        this.passwordEncoder = passwordEncoder;
        excludeMethodFromFiltering("findTodayOrders");
        excludeMethodFromFiltering("countByRestoCode");
    }

    @Override
    public Mono<Order> save(Order order) {
        logger.debug("Starting order save process for customer phone: {}", order.getCustomerPhone());
                return Mono.just(order)
            .flatMap(this::findOrCreateCustomer)
            .map(customer -> {
                updateOrderWithCustomerInfo(order, customer);
                return order;
            })
            .flatMap(this::generateOrderNumberIfNeeded)
            .flatMap(orderToSave -> {
            	// Préparation finale                
                logger.debug("Saving order: {}", orderToSave);
                return template.insert(orderToSave);
            })
            .doOnSuccess(savedOrder -> 
                logger.info("Order saved successfully with ID: {} and number: {}", 
                    savedOrder.getId(), savedOrder.getOrderNumber()))
            .doOnError(error -> 
                logger.error("Error saving order: {}", error.getMessage(), error));
    }
   

    /**
     * Trouve un customer existant par téléphone ou en crée un nouveau
     */
    private Mono<Customer> findOrCreateCustomer(Order order) {      
        return customerRepository.findByPhone(order.getCustomerPhone())
            .switchIfEmpty(Mono.defer(() -> {
                logger.debug("No existing customer found with phone: {}, creating new one", order.getCustomerPhone());
                Customer newCustomer = createCustomerFromOrder(order);
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
    private Customer createCustomerFromOrder(Order order) {
        Customer customer = new Customer();
        customer.setFirstname(order.getFirstname());
        customer.setLastname(order.getLastname());
        customer.setPhone(order.getCustomerPhone());
        customer.setEmail(order.getCustomerEmail());
        customer.setAddress(order.getDeliveryAddress());
        customer.setCity(order.getDeliveryCity());
        customer.setActive(true);
        return customer;
    }

    /**
     * Met à jour l'order avec les informations du customer
     */
    private void updateOrderWithCustomerInfo(Order order, Customer customer) {
        order.setCustomerId(customer.getId());
        // Mettre à jour les informations de contact si elles sont plus complètes chez le customer
        if (customer.getFirstname() != null) order.setFirstname(customer.getFirstname());
        if (customer.getLastname() != null) order.setLastname(customer.getLastname());
        if (customer.getPhone() != null) order.setCustomerPhone(customer.getPhone());
        if (customer.getEmail() != null) order.setCustomerEmail(customer.getEmail());
        if (customer.getAddress() != null) order.setDeliveryAddress(customer.getAddress());
        if (customer.getCity() != null) order.setDeliveryCity(customer.getCity());
        if (customer.getId() != null) order.setOwnerCode(customer.getId());

        logger.debug("Order updated with customer ID: {}", customer.getId());
    }

    /**
     * Génère un numéro de commande si nécessaire
     */
    private Mono<Order> generateOrderNumberIfNeeded(Order order) {
        if (order.getOrderNumber() != null && !order.getOrderNumber().trim().isEmpty()) {
            return Mono.just(order);
        }
        
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String orderNumber = "ORD-" + timestamp + "-" + randomSuffix;
        
        order.setOrderNumber(orderNumber);
        logger.debug("Generated order number: {}", orderNumber);
        
        return Mono.just(order);
    }

    
    private Mono<Order> findByCustomerIdAndPaymentId(UUID customerId, UUID paymentId) {
        Criteria criteria = Criteria.where("customer_id").is(customerId)
                                  .and("payment_id").is(paymentId);
        return applyGlobalFilter(Query.query(criteria), "findByCustomerIdAndPaymentId",EnumFilter.ALL)
                .flatMap(query -> template.selectOne(query, Order.class));
    }

    // Implémentation des autres méthodes de l'interface
    @Override
    public Flux<Order> findByRestoCode(UUID restoCode) {
        logger.debug("Finding orders by restoCode: {}", restoCode);
        
        return applyGlobalFilter(Query.query(Criteria.where("resto_code").is(restoCode)), "findByRestoCode",EnumFilter.ALL)
                .flatMapMany(query -> template.select(query, Order.class))
                .doOnComplete(() -> logger.debug("Completed finding orders by restoCode: {}", restoCode));
    }

    @Override
    public Flux<Order> findByCustomerId(UUID customerId) {
        logger.debug("Finding orders by customerId: {}", customerId);
        
        return applyGlobalFilter(Query.query(Criteria.where("customer_id").is(customerId)), "findByCustomerId",EnumFilter.ALL)
                .flatMapMany(query -> template.select(query, Order.class))
                .doOnComplete(() -> logger.debug("Completed finding orders by customerId: {}", customerId));
    }

    @Override
    public Flux<Order> findByStatus(String status) {
        logger.debug("Finding orders by status: {}", status);
        
        return applyGlobalFilter(Query.query(Criteria.where("status").is(status)), "findByStatus",EnumFilter.ALL)
                .flatMapMany(query -> template.select(query, Order.class))
                .doOnComplete(() -> logger.debug("Completed finding orders by status: {}", status));
    }

    @Override
    public Flux<Order> findByRestoCodeAndStatus(UUID restoCode, String status) {
        logger.debug("Finding orders by restoCode: {} and status: {}", restoCode, status);
        
        Criteria criteria = Criteria.where("resto_code").is(restoCode)
                                  .and("status").is(status);
        
        return applyGlobalFilter(Query.query(criteria), "findByRestoCodeAndStatus",EnumFilter.ALL)
                .flatMapMany(query -> template.select(query, Order.class))
                .doOnComplete(() -> logger.debug("Completed finding orders by restoCode and status: {}, {}", restoCode, status));
    }

   
    @Override
    public Mono<Long> countByRestoCode(UUID restoCode) {
        logger.debug("Counting orders by restoCode: {}", restoCode);
        
        return applyGlobalFilter(Query.query(Criteria.where("resto_code").is(restoCode)), "countByRestoCode",EnumFilter.ALL)
                .flatMap(query -> template.count(query, Order.class))
                .doOnSuccess(count -> logger.debug("Counted {} orders for restoCode: {}", count, restoCode));
    }

    @Override
    public Mono<Long> countByRestoCodeAndStatus(UUID restoCode, String status) {
        logger.debug("Counting orders by restoCode: {} and status: {}", restoCode, status);
        
        Criteria criteria = Criteria.where("resto_code").is(restoCode)
                                  .and("status").is(status);
        
        return applyGlobalFilter(Query.query(criteria), "countByRestoCodeAndStatus",EnumFilter.ALL)
                .flatMap(query -> template.count(query, Order.class))
                .doOnSuccess(count -> logger.debug("Counted {} orders for restoCode: {} and status: {}", count, restoCode, status));
    }

    

    // Méthode utilitaire pour le mapping
    private Order mapRowToOrder(io.r2dbc.spi.Row row) {
        Order order = new Order();        
        order.setId(getUuid(row, "id"));
        order.setOrderNumber(getString(row, "order_number"));
        order.setCustomerId(getUuid(row, "customer_id"));
        order.setTotalPrice(getDouble(row, "total_price"));
        order.setTotalItems(getInteger(row, "total_items"));
        order.setOrderDate(getLong(row, "order_date"));
       // order.setStatus(getString(row, "status"));
        order.setLastname(getString(row, "lastname"));
        order.setFirstname(getString(row, "firstname"));
        order.setCustomerPhone(getString(row, "customer_phone"));
        order.setCustomerEmail(getString(row, "customer_email"));
        order.setDeliveryAddress(getString(row, "delivery_address"));
        order.setDeliveryCity(getString(row, "delivery_city"));
        order.setDeliveryInstructions(getString(row, "delivery_instructions"));
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
	public Flux<Order> findByCustomerPhoneOrEmail(String phone, String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<Order> findByTotalPriceGreaterThanEqual(Double minPrice) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<Order> findByStatusIn(Iterable<String> statuses) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<Order> findByTotalItemsGreaterThanEqual(Integer minItems) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<Integer> updateOrderStatus(UUID orderId, String status, LocalDateTime modifiedDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<Order> findTodayOrders(UUID restoCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<Order> findByRestoCodeAndDateRange(UUID restoCode, LocalDateTime startDate, LocalDateTime endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	// =====================================
	// Méthode de recherche avec résultat paginé
	// =====================================
	@Override
	public Mono<PageResponse<Order>> searchWithPagination(Map<String, Object> filters,EnumFilter type) {
	    return Mono.zip(
	        super.search(filters, type).collectList(),
	        super.count(filters,type)
	    ).flatMap(tuple -> {
	        List<Order> content = tuple.getT1();
	        long totalElements = tuple.getT2();
	       
	        // Chargement parallèle des OrderItem
	        return Flux.fromIterable(content)
	            .flatMap(this::enrichOrderWithItems)
	            .collectList()
	            .map(enrichedOrders -> createPageResponse(enrichedOrders, filters, totalElements));
	    });
	}

	private Mono<Order> enrichOrderWithItems(Order order) {
	    return orderItemRepository.findByOrderId(order.getId())
	        .collectList()
	        .flatMap(orderItems -> {
	            order.setOrderItems(orderItems);
	         // Vérifier si deliver_info est null
	            if (order.getDeliver_info() == null) {
	                return Mono.just(order);
	            }
	            return prnslRepository.findById(order.getDeliver_info())
	                .flatMap(deliverInfo -> {
	                    // Process deliverInfo if needed
	                     order.setDeliveryInfo(deliverInfo);
	                    return Mono.just(order);
	                })
	                .defaultIfEmpty(order);
	        });
	}

	private PageResponse<Order> createPageResponse(List<Order> content, 
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
	public Flux<Order> search(Map<String, Object> filters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<Order> findByActive(Boolean active) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
     * Valide la transition entre deux statuts selon les règles métier
     */
    private boolean isValidStatusTransition(EnumOrder currentStatus, EnumOrder newStatus) {
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
            case WAITING_FOR_PAYMENT:
                return newStatus == EnumOrder.CONFIRMED || 
                       newStatus == EnumOrder.CANCELLED;
                
            case CONFIRMED:
                return newStatus == EnumOrder.PREPARING || 
                       newStatus == EnumOrder.CANCELLED;
                
            case PREPARING:
                return newStatus == EnumOrder.READY || 
                       newStatus == EnumOrder.CANCELLED;
                
            case READY:
                return newStatus == EnumOrder.OUT_FOR_DELIVERY || 
                       newStatus == EnumOrder.CANCELLED;
                
            case OUT_FOR_DELIVERY:
                return newStatus == EnumOrder.DELIVERED || 
                       newStatus == EnumOrder.COMPLETED;
                
            case PAYMENT_FAILED:
                return newStatus == EnumOrder.PENDING || 
                       newStatus == EnumOrder.WAITING_FOR_PAYMENT;
                
            default:
                return false;
        }
    }
	
}