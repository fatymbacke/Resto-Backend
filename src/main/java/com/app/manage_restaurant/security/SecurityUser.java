package com.app.manage_restaurant.security;

import java.util.UUID;

import com.app.manage_restaurant.entities.EnumPerson;
import com.app.manage_restaurant.entities.Person;

public class SecurityUser {
    private final Person user;
    private final UUID restoCode;
    private final UUID ownerCode;
    private final String role;
    private final EnumPerson type;
    private final UUID id;

    // Constructeur principal
    public SecurityUser(Person user, UUID restoCode, UUID ownerCode, String userRole, UUID currentUserId,EnumPerson type) {
        this.user = user;
        this.restoCode = restoCode;
        this.ownerCode = ownerCode;
        this.role = userRole;
        this.id = currentUserId;
        this.type = type;

    }

    // Constructeur par défaut pour les cas où l'utilisateur n'est pas authentifié
    public SecurityUser() {
        this.user = null;
        this.restoCode = null;
        this.ownerCode = null;
        this.role = null;
		this.type = null;
        this.id = null;
    }

    public Person getUser() {
        return user;
    }

    public UUID getRestoCode() {
        return restoCode;
    }

    public UUID getOwnerCode() {
        return ownerCode;
    }

    public String getRole() {
        return role;
    }

    public UUID getId() {
        return id;
    }

    
    public EnumPerson getType() {
		return type;
	}

	@Override
	public String toString() {
		return "SecurityUser [user=" + user + ", restoCode=" + restoCode + ", ownerCode=" + ownerCode + ", role=" + role
				+ ", type=" + type + ", id=" + id + "]";
	}

}