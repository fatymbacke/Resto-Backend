package com.app.manage_restaurant.cores;

import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

public abstract class AuditableEntity<U> implements HasOwnerAndResto<U> {

    @Id
    protected U id;

    @Column
    protected boolean active;

    @CreatedBy
    protected U createdBy;

    @CreatedDate
    protected long createdDate;

    @LastModifiedBy
    protected U modifiedBy;

    @LastModifiedDate
    protected long modifiedDate;

    @Column("resto_code")
    protected U restoCode;
    
    @Column("owner_code")
    protected U ownerCode;

    // ===== Getters / Setters =====

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public U getCreatedBy() { return createdBy; }
    public void setCreatedBy(U createdBy) { this.createdBy = createdBy; }

    public long getCreatedDate() { return createdDate; }
    public void setCreatedDate(long createdDate) { this.createdDate = createdDate; }

    public U getModifiedBy() { return modifiedBy; }
    public void setModifiedBy(U modifiedBy) { this.modifiedBy = modifiedBy; }

    public long getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(long modifiedDate) { this.modifiedDate = modifiedDate; }

    @Override
    public U getId() { return id; }
    @Override
    public void setId(U id) { this.id = id; }

    @Override
    public U getOwnerCode() { return ownerCode; }
    
    @Override
    public void setOwnerCode(U ownerCode) { this.ownerCode = ownerCode; }
    
    @Override
    public U getRestoCode() { return restoCode; }
    
    @Override
    public void setRestoCode(U restoCode) { this.restoCode = restoCode; }

    @Override
    public String toString() {
        return "AuditableEntity [id=" + id + ", active=" + active + ", createdBy=" + createdBy + ", createdDate="
                + createdDate + ", modifiedBy=" + modifiedBy + ", modifiedDate=" + modifiedDate 
                + ", restoCode=" + restoCode + ", ownerCode=" + ownerCode + "]";
    }

    @Component
    public static class AuditableEntityCallback implements BeforeConvertCallback<AuditableEntity<UUID>> {

        @Override
        public AuditableEntity<UUID> onBeforeConvert(AuditableEntity<UUID> entity) {
            long now = System.currentTimeMillis();

            if (entity.getId() == null) { // insertion
                entity.setCreatedDate(now);
                entity.setActive(true);
                
                // Vous pouvez ajouter ici une logique pour setter automatiquement restoCode si nécessaire
                // Par exemple, à partir du contexte de sécurité
                // if (entity.getRestoCode() == null) {
                //     entity.setRestoCode(getRestoCodeFromSecurityContext());
                // }
            }

            // mise à jour
            entity.setModifiedDate(now);
            return entity;
        }
        
        // Méthode utilitaire optionnelle pour récupérer le restoCode du contexte
        private UUID getRestoCodeFromSecurityContext() {
            // Implémentez la logique pour récupérer le restoCode du contexte de sécurité
            // Par exemple :
            // SecurityContext context = SecurityContextHolder.getContext();
            // Authentication authentication = context.getAuthentication();
            // if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            //     return ((CustomUserDetails) authentication.getPrincipal()).getRestoCode();
            // }
            return null;
        }
    }
}