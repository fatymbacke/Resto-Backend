package com.app.manage_restaurant.entities;

public enum EnumOrder {
    
    // Statuts de base
    PENDING("En attente"),
    CONFIRMED("Confirmée"),
    PREPARING("En préparation"),
    READY("Prête"),
    COMPLETED("Terminée"),
    CANCELLED("Annulée"),
    
    // Statuts de livraison
    OUT_FOR_DELIVERY("En livraison"),
    DELIVERED("Livrée"),
    
    // Statuts de paiement
    WAITING_FOR_PAYMENT("En attente de paiement"),
    PAYMENT_CONFIRMED("Paiement confirmé"),
    PAYMENT_FAILED("Échec du paiement"),
    
    // Statuts spéciaux
    ON_HOLD("En attente"),
    REFUNDED("Remboursée"),
    PARTIALLY_REFUNDED("Partiellement remboursée"),
    EXPIRED("Expirée");

    private final String displayName;

    EnumOrder(String displayName) {
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
        return this == PENDING || this == WAITING_FOR_PAYMENT;
    }

    public boolean isActive() {
        return this == PENDING || this == CONFIRMED || this == PREPARING || 
               this == READY || this == OUT_FOR_DELIVERY || this == WAITING_FOR_PAYMENT;
    }

    public boolean isCompleted() {
        return this == COMPLETED || this == DELIVERED;
    }

    public boolean isCancelled() {
        return this == CANCELLED || this == EXPIRED;
    }

    public boolean canBeCancelled() {
        return this == PENDING || this == CONFIRMED || this == WAITING_FOR_PAYMENT;
    }

    public boolean canBeModified() {
        return this == PENDING || this == CONFIRMED;
    }

    public boolean requiresPayment() {
        return this == WAITING_FOR_PAYMENT || this == PENDING;
    }

    // Méthode pour obtenir l'énumération à partir d'une chaîne
    public static EnumOrder fromString(String value) {
        if (value == null) return null;
        
        try {
            return EnumOrder.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Recherche par nom d'affichage
            for (EnumOrder status : EnumOrder.values()) {
                if (status.getDisplayName().equalsIgnoreCase(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Statut de commande inconnu: " + value);
        }
    }

    // Méthode pour obtenir tous les statuts actifs
    public static EnumOrder[] getActiveStatuses() {
        return new EnumOrder[]{PENDING, CONFIRMED, PREPARING, READY, OUT_FOR_DELIVERY, WAITING_FOR_PAYMENT};
    }

    // Méthode pour obtenir tous les statuts terminés
    public static EnumOrder[] getCompletedStatuses() {
        return new EnumOrder[]{COMPLETED, DELIVERED, CANCELLED, EXPIRED, REFUNDED};
    }

    // Méthode pour obtenir les statuts par catégorie
    public static EnumOrder[] getPreparationStatuses() {
        return new EnumOrder[]{PENDING, CONFIRMED, PREPARING, READY};
    }

    public static EnumOrder[] getDeliveryStatuses() {
        return new EnumOrder[]{OUT_FOR_DELIVERY, DELIVERED};
    }

    public static EnumOrder[] getPaymentStatuses() {
        return new EnumOrder[]{WAITING_FOR_PAYMENT, PAYMENT_CONFIRMED, PAYMENT_FAILED};
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}