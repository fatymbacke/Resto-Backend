package com.app.manage_restaurant.services.impl;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.manage_restaurant.cores.BaseServiceImpl;
import com.app.manage_restaurant.cores.EnumFilter;
import com.app.manage_restaurant.dtos.request.Login;
import com.app.manage_restaurant.dtos.request.PrsnlRequest;
import com.app.manage_restaurant.dtos.response.PartenaireResponse;
import com.app.manage_restaurant.dtos.response.PrsnlResponse;
import com.app.manage_restaurant.entities.EnumPerson;
import com.app.manage_restaurant.entities.Person;
import com.app.manage_restaurant.entities.Prsnl;
import com.app.manage_restaurant.exceptions.entities.EntityCredentialsException;
import com.app.manage_restaurant.exceptions.entities.EntityNotFoundException;
import com.app.manage_restaurant.mapper.PrsnlMapper;
import com.app.manage_restaurant.repositories.CustomerRepository;
import com.app.manage_restaurant.repositories.PermissionRepository;
import com.app.manage_restaurant.repositories.PrsnlRepository;
import com.app.manage_restaurant.security.JwtService;
import com.app.manage_restaurant.services.GenericDuplicateChecker;
import com.app.manage_restaurant.services.PrsnlService;
import com.app.manage_restaurant.utilities.FileStorageUtil;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PrsnlServiceImpl extends BaseServiceImpl<Prsnl, PrsnlRequest, PrsnlResponse, UUID> implements PrsnlService{

    private final PrsnlRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final Logger logger;
    private final PermissionRepository permissionRepository;
    public PrsnlServiceImpl(PrsnlRepository repository,
    		            @Lazy R2dbcEntityTemplate template,
                        PasswordEncoder passwordEncoder,
   		             FileStorageUtil fileStorageUtil,
   		          CustomerRepository customerRepository,
                        JwtService jwtService,
                        PermissionRepository permissionRepository,
                        ReactiveExceptionHandler exceptionHandler,
                        GenericDuplicateChecker duplicateChecker) {
        super(repository,fileStorageUtil,template, PrsnlMapper::toEntity, PrsnlMapper::toResponse,
              Prsnl.class, "Personnel", exceptionHandler, duplicateChecker);
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.permissionRepository = permissionRepository;
        this.logger = LoggerFactory.getLogger(PrsnlService.class);
    }
    


	// ==============================
    // SAVE AVEC ENCODAGE DU MOT DE PASSE ET VERIFICATION DES DOUBLONS
    // ==============================
    @Override
    public Mono<PrsnlResponse> save(PrsnlRequest prsnl) {
        return Mono.just(prsnl)
                   .doOnNext(this::validate)
                   .map(p -> {
                       String pwd = (p.getPassword() != null && !p.getPassword().isEmpty())
                               ? p.getPassword()
                               : p.getPhone();
                       p.setPassword(passwordEncoder.encode(pwd));
                       return p;
                   })
                   .flatMap(reponse->super.save(prsnl));
    }
    
    @Override
    public Mono<PrsnlResponse> update(UUID id, PrsnlRequest request) {
    	// TODO Auto-generated method stub
    	return super.update(id, request);
    }
    
    

    // ==============================
    // CHAMPS UNIQUES
    // ==============================
    @Override
    public Map<String, Object> extractUniqueFields(PrsnlRequest request) {
        return Map.of(
            "phone", request.getPhone()
        );
    }

    
    @Override
    public void applyRequestToEntity(Prsnl existing, PrsnlRequest request) {
        PrsnlMapper.updateEntityFromRequest(request, existing);
    }
    
    // ==============================
    // VALIDATION
    // ==============================
    
    @Override
    public Mono<Void> validate(PrsnlRequest p) {
        return Mono.fromRunnable(() -> {
            if (p.getPhone() == null || p.getPhone().trim().isEmpty())
                throw new IllegalArgumentException("Le numéro de téléphone est obligatoire");
            if (p.getLastname() == null || p.getLastname().trim().isEmpty())
                throw new IllegalArgumentException("Le nom est obligatoire");
            if (p.getFirstname() == null || p.getFirstname().trim().isEmpty())
                throw new IllegalArgumentException("Le prénom est obligatoire");             
            if (p.getRole() == null)
                throw new IllegalArgumentException("Le rôle est obligatoire");
        });
    }

    // ==============================
    // LOGIN - VERSION ADAPTÉE AVEC GÉNÉRATION DU TOKEN COMPLET
    // ==============================
    @Override
    public Mono<String> login(Login login) {    	
    	
        return validateLoginRequest(login)
                .then(repository.findByPhoneAndRestoCodeAndType(login.getPhone(),login.getRestoCode(),login.getType())
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("Personnel",
                                String.format("Téléphone=%s, restaurant=%s", login.getPhone(), login.getRestoLabel())))))
                .flatMap(user -> authenticateUser(login, user))
                .flatMap(user -> generateJwtTokenWithCodes(user, login));
    }
    @Override
    public Mono<Void> validateLoginRequest(Login login) {
        return Mono.fromRunnable(() -> {
            if (login.getPhone() == null || login.getPhone().trim().isEmpty())
                throw new IllegalArgumentException("Le numéro de téléphone est requis");
            if (login.getPassword() == null || login.getPassword().trim().isEmpty())
                throw new IllegalArgumentException("Le mot de passe est requis");
            
        });
    }
    @Override
    public Mono<Person> authenticateUser(Login login, Person user) {
    	        return Mono.fromCallable(() -> {

            if (passwordEncoder.matches(login.getPassword(), user.getPassword())) {
                return user;
            }
            throw new EntityCredentialsException("Mot de passe incorrect");
        });
    }

    // ==============================
    // GÉNÉRATION DU TOKEN JWT AVEC RESTOCODE ET OWNERCODE
    // ==============================
    @Override
    public Mono<String> generateJwtTokenWithCodes(Person user, Login login) {
        return Mono.fromCallable(() -> {
            try {
                // Convertir le restoCode en UUID
                UUID restoCodeUUID = login.getRestoCode() !=null ? UUID.fromString(login.getRestoCode()) : null;                
                // Récupérer l'ownerCode depuis l'utilisateur ou son restaurant
                UUID ownerCodeUUID = extractOwnerCodeFromUser(user);
                
                logger.info("Génération du token JWT pour: {}, restoCode: {}, ownerCode: {}", 
                           user.getPhone(), restoCodeUUID, ownerCodeUUID);

              
                // Générer le token avec les deux codes
                String token = jwtService.generateToken(
                    user.getPhone(),
                    restoCodeUUID,
                    ownerCodeUUID,
                    user.getLastname().concat(" ").concat(user.getFirstname()),
                    user.getId(),
                    user.getRole(),
                    user.getRoleId(),
                    login.getType()
                    
                );
                logger.info("Token JWT généré avec succès pour: {}", user.getPhone());
                return token;
                
            } catch (IllegalArgumentException e) {
                logger.error("Format UUID invalide pour restoCode: {}", login.getRestoCode());
                throw new IllegalArgumentException("Format de code restaurant invalide");
            } catch (Exception e) {
                logger.error("Erreur lors de la génération du token JWT: {}", e.getMessage());
                throw new RuntimeException("Erreur lors de la génération du token d'authentification");
            }
        });
    }

    // ==============================
    // EXTRACTION DE L'OWNERCODE
    // ==============================
    @Override
    public UUID extractOwnerCodeFromUser(Person user) {
        // Implémentez la logique pour récupérer l'ownerCode
        // Cela dépend de votre modèle de données
        
        // Exemple 1: Si l'ownerCode est stocké directement dans Prsnl
        if (user.getOwnerCode() != null) {
            return user.getOwnerCode();
        }
                
       
        // Le filtre JWT acceptera quand même le token car restoCode est présent
        logger.warn("OwnerCode non trouvé pour l'utilisateur: {}", user.getPhone());
        return null;
    }

    // ==============================
    // MÉTHODE DE GÉNÉRATION DE TOKEN POUR USAGE EXTERNE
    // ==============================
    @Override
    public Mono<String> generateToken(Person user, UUID restoCode, UUID ownerCode,EnumPerson type) {
        return Mono.fromCallable(() -> {
            // Vérifier qu'au moins un code est présent
            if (restoCode == null && ownerCode == null) {
                throw new IllegalArgumentException("Au moins un code (restoCode ou ownerCode) est requis");
            }            
            return jwtService.generateToken(
                user.getPhone(),
                restoCode ,
                ownerCode,
                user.getLastname().concat(" ").concat(user.getFirstname()),
                user.getId(),
                user.getRole(),
                user.getRoleId(),
                type
            );
        });
    }

    // ==============================
    // CHANGE STATE
    // ==============================
    @Override
    public Mono<PrsnlResponse> changeState(UUID id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Personnel", id)))
                .flatMap(user -> {
                    user.setActive(!user.isActive());
                    return repository.save(user);
                })
                .map(PrsnlMapper::toResponse);
    }
    
    

    @Override
    public Flux<PrsnlResponse> findAll() {
    	 logger.info("🔍 Starting to findAll prsnls");
         
         return repository.findAll()
                 .doOnSubscribe(subscription -> 
                     logger.debug("📡 Subscribed to findAll prsnls stream"))
                 .map(prsnl -> {
                     logger.debug("📝 Mapping prsnl to response: {}", prsnl.getEmail());
                     return PrsnlMapper.toResponse(prsnl);
                 })
                 .doOnComplete(() -> 
                     logger.info("✅ Successfully completed findAll prsnls"))
                 .doOnError(error -> 
                     logger.error("❌ Error in findAll prsnls: {}", error.getMessage(), error));
    
    }

    
    
    
    

            
	@Override
	protected String getFileField(Prsnl entity) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	protected void setFileField(Prsnl entity, String filePath) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Flux<PrsnlResponse> findLivreurs(UUID resto) {
	    logger.debug("Finding all active livreurs with DELIVERY_ASSIGN_DRIVER permission");
	    
	    return repository.findByResto(resto, true)
	            .flatMap(prsnl -> {
	                // Récupérer les permissions du livreur
	                return permissionRepository.findPermissionsWithRole(prsnl.getRoleId())
	                        .collectList()
	                        .filter(permissions -> {
	                            // Filtrer : garder seulement si DELIVERY_ASSIGN_DRIVER est présent
	                            boolean hasDeliveryAssignDriver = permissions.stream()
	                                    .anyMatch(p -> "DELIVERY_ASSIGN_DRIVER".equals(p.getCode()));
	                            // Logger pour debug
	                            if (hasDeliveryAssignDriver) {
	                                logger.debug("Livreur {} has DELIVERY_ASSIGN_DRIVER permission", 
	                                    prsnl.getPhone());
	                            }
	                            
	                            return hasDeliveryAssignDriver;
	                        })
	                        .map(permissions -> {
	                            // Créer la réponse
	                            PrsnlResponse response = PrsnlMapper.toResponse(prsnl);
	                           // response.setPermissions(permissions); // Optionnel
	                            return response;
	                        });
	            })
	            .doOnError(error -> 
	                logger.error("Error finding active livreurs with permission: {}", 
	                    error.getMessage(), error)
	            );
	}



	@Override
	public Flux<PartenaireResponse> findPartenaires(UUID resto,boolean active) {
        logger.info("🔍 Starting to findAll partenaires");
         
         return repository.findByResto( resto, active)
                 .doOnSubscribe(subscription -> 
                     logger.debug("📡 Subscribed to findAll partenaires stream"))
                 .map(prsnl -> {
                     logger.debug("📝 Mapping partenaires to response: {}", prsnl.getEmail());
                     return new PartenaireResponse(prsnl.getPhone(), prsnl.isActive());
                 })
                 .doOnComplete(() -> 
                     logger.info("✅ Successfully completed findAll partenaires"))
                 .doOnError(error -> 
                     logger.error("❌ Error in findAll prsnls: {}", error.getMessage(), error));
	}
	

	// ==================================
    // Recherche avec pagination
    // ==================================
    @Override
    public Mono<PageResponse<PrsnlResponse>> search(Map<String, Object> filters,EnumFilter type) {
        logger.debug("Searching {} with pagination - filters: {}", entityName, filters);
        
        return repository.searchWithPagination(filters,type)
        		
                .map(page -> new PageResponse<>(
                    page.getContent().stream()
                        .map(s-> PrsnlMapper.toResponse(s) ).toList(),
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

	


}