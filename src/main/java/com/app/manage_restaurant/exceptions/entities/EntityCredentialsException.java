package com.app.manage_restaurant.exceptions.entities;

import java.util.Map;

import com.app.manage_restaurant.exceptions.cores.BusinessException;

public class EntityCredentialsException extends BusinessException {

    private final String entityType;
    private final Map<String, Object> identifiers;
    private final Class<?> entityClass;

    // Constructeur principal avec identifiants et classe
    public EntityCredentialsException(String entityType, Map<String, Object> identifiers, Class<?> entityClass) {
        super(entityType );
        this.entityType = entityType;
        this.identifiers = identifiers;
        this.entityClass = entityClass;
    }

    // Constructeur simple pour compatibilité
    public EntityCredentialsException(String entityType) {
        super(entityType );
        this.entityType = entityType;
        this.identifiers = null;
        this.entityClass = null;
    }

    public String getEntityType() { return entityType; }
    public Map<String, Object> getIdentifiers() { return identifiers; }
    public Class<?> getEntityClass() { return entityClass; }
}
