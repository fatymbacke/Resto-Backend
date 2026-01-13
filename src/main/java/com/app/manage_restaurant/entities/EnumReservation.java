package com.app.manage_restaurant.entities;

public enum EnumReservation {
    
    // Statuts de base
    PENDING("En attente"),
    CONFIRMED("Confirmée"),
    SEATED("Installée"),
    NO_SHOW("No-show"),
    COMPLETED("Terminée"),
    CANCELLED("Annulée");

	
	
    private final String displayName;

    EnumReservation(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValue() {
        return this.name();
    }

    // Méthodes utilitaires pour vérifier le statut
    public boolean isPending() {
        return this == PENDING ;
    }

    public boolean isActive() {
        return this == PENDING || this == CONFIRMED  || 
               this == SEATED || this == NO_SHOW ;
    }

    public boolean isCompleted() {
        return this == COMPLETED ;
    }

    public boolean isCancelled() {
        return this == CANCELLED ;
    }

    public boolean canBeCancelled() {
        return this == PENDING || this == CONFIRMED ;
    }

    public boolean canBeModified() {
        return this == PENDING || this == CONFIRMED;
    }

    
    // Méthode pour obtenir l'énumération à partir d'une chaîne
    public static EnumReservation fromString(String value) {
        if (value == null) return null;
        
        try {
            return EnumReservation.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Recherche par nom d'affichage
            for (EnumReservation status : EnumReservation.values()) {
                if (status.getDisplayName().equalsIgnoreCase(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Statut de commande inconnu: " + value);
        }
    }

    // Méthode pour obtenir tous les statuts actifs
    public static EnumReservation[] getActiveStatuses() {
        return new EnumReservation[]{PENDING, CONFIRMED, SEATED, NO_SHOW};
    }

    // Méthode pour obtenir tous les statuts terminés
    public static EnumReservation[] getCompletedStatuses() {
        return new EnumReservation[]{COMPLETED, CANCELLED};
    }

   
    @Override
    public String toString() {
        return this.displayName;
    }
}