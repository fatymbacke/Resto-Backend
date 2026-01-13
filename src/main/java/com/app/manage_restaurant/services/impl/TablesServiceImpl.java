package com.app.manage_restaurant.services.impl;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;

import com.app.manage_restaurant.cores.BaseServiceImpl;
import com.app.manage_restaurant.dtos.request.TablesRequest;
import com.app.manage_restaurant.dtos.response.TablesResponse;
import com.app.manage_restaurant.entities.Tables;
import com.app.manage_restaurant.mapper.TablesMapper;
import com.app.manage_restaurant.repositories.TablesRepository;
import com.app.manage_restaurant.services.GenericDuplicateChecker;
import com.app.manage_restaurant.services.TablesService;
import com.app.manage_restaurant.utilities.FileStorageUtil;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TablesServiceImpl extends BaseServiceImpl<Tables, TablesRequest, TablesResponse, UUID> implements TablesService {

    private final TablesRepository tableRepository;
    private final Logger logger; 
    protected final R2dbcEntityTemplate template; // 🔥 Pour la recherche dynamique

    public TablesServiceImpl(TablesRepository repository,
    		            R2dbcEntityTemplate template,
    		             FileStorageUtil fileStorageUtil,
                        ReactiveExceptionHandler exceptionHandler,
                        GenericDuplicateChecker duplicateChecker) {
        super(repository,fileStorageUtil,template, TablesMapper::toEntity, TablesMapper::toResponse,
              Tables.class, "Tables", exceptionHandler, duplicateChecker);
        this.tableRepository = repository;
        this.template = template;
        this.logger = LoggerFactory.getLogger(TablesService.class);
    }   

    // ==============================
    // VALIDATION DES DONNÉES
    // ==============================
    
    @Override
    public Mono<Void> validate(TablesRequest request) {
        return Mono.fromRunnable(() -> {
            if (request.getName() == null || request.getName().trim().isEmpty())
                throw new IllegalArgumentException("Le nom de la table est obligatoire");
            if (request.getCapacity() == null || request.getCapacity() <= 0)
                throw new IllegalArgumentException("La capacité doit être supérieure à 0");            
            if (request.getShape() == null || request.getShape().trim().isEmpty())
                throw new IllegalArgumentException("La forme de la table est obligatoire");
            if (request.getStatus() == null || request.getStatus().trim().isEmpty())
                throw new IllegalArgumentException("Le statut de la table est obligatoire");
            
            // Validation des valeurs enum
            validateShape(request.getShape());
            validateStatus(request.getStatus());
        });
    }
    @Override
    public void validateShape(String shape) {
        if (!shape.equals("rectangle") && !shape.equals("circle") && !shape.equals("square")) {
            throw new IllegalArgumentException("La forme doit être: rectangle, circle ou square");
        }
    }
    @Override
    public void validateStatus(String status) {
        if (!status.equals("available") && !status.equals("occupied") && 
            !status.equals("reserved") && !status.equals("cleaning")) {
            throw new IllegalArgumentException("Le statut doit être: available, occupied, reserved ou cleaning");
        }
    }

    // ==============================
    // CHAMPS UNIQUES
    // ==============================
    @Override
    public Map<String, Object> extractUniqueFields(TablesRequest request) {
        return Map.of(
            "name", request.getName()
        );
    }

	
    // ==============================
    // FIND ALL
    // ==============================
    @Override
    public Flux<TablesResponse> findAll() {
        return tableRepository.findAll()
                             .map(TablesMapper::toResponse);
    }

	@Override
	public Flux<TablesResponse> findByRestaurantId(UUID restaurantId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<TablesResponse> findByRestaurantIdAndStatus(UUID restaurantId, String status) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<TablesResponse> updateStatus(UUID tableId, String status) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<TablesResponse> updatePosition(UUID tableId, Integer x, Integer y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<TablesResponse> updateCapacity(UUID tableId, Integer capacity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<Integer> countActiveTablesByRestaurant(UUID restaurantId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<Boolean> existsByRestaurantIdAndName(UUID restaurantId, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<TablesResponse> findByRestaurantIdAndShape(UUID restaurantId, String shape) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<Integer> deactivateAllByRestaurantId(UUID restaurantId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getFileField(Tables entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setFileField(Tables entity, String filePath) {
		// TODO Auto-generated method stub
		
	}

    // ==============================
    // MÉTHODES SPÉCIFIQUES AUX TABLES
    // ==============================

    
}