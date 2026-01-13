package com.app.manage_restaurant.exceptions.entities;

import com.app.manage_restaurant.exceptions.cores.BusinessException;

public class EntityNotFoundException extends BusinessException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Object entityId;
    private final String entityType;

    public EntityNotFoundException(String entityType, Object entityId) {
        super(String.format("%s non trouvé avec l'identifiant : %s", entityType, entityId));
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public EntityNotFoundException(String message) {
        super(message);
        this.entityType = null;
        this.entityId = null;
    }

    // Getters
    public Object getEntityId() { return entityId; }
    public String getEntityType() { return entityType; }
}