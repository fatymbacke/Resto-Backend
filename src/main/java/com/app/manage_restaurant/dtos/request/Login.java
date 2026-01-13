package com.app.manage_restaurant.dtos.request;

import com.app.manage_restaurant.entities.EnumPerson;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Login {
	
    @NotBlank(message = "Le numéro de téléphone est obligatoire")
   @Pattern(regexp = "^\\+?[0-9]+$", message = "Le numéro de téléphone commence par + et constituer de chiffres")
    private String phone;
    
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 1, message = "Le mot de passe ne peut pas être vide")
    private String password;
    private String restoCode;
    private String restoLabel;
    @NotNull(message = "Le type d'utilisateur est obligatoire")
    private EnumPerson type;
    // Getters et setters...
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRestoCode() { return restoCode; }
    public void setRestoCode(String restoCode) { this.restoCode = restoCode; }

    public String getRestoLabel() {
		return restoLabel;
	}
	public void setRestoLabel(String restoLabel) {
		this.restoLabel = restoLabel;
	}
	
	
	public EnumPerson getType() {
		return type;
	}
	public void setType(EnumPerson type) {
		this.type = type;
	}
	// Méthode de validation
    public boolean isValid() {
        return phone != null && !phone.trim().isEmpty() &&
        	   type != null  &&
               password != null && !password.trim().isEmpty() &&
               restoCode != null && !restoCode.trim().isEmpty();
    }
}