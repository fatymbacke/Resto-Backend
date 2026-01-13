package com.app.manage_restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

@SpringBootApplication
@OpenAPIDefinition // Ajout de l'annotation
public class ManageRestaurantApplication {

	
	public static void main(String[] args) {
		SpringApplication.run(ManageRestaurantApplication.class, args);
	}
	// Si tu veux grouper des endpoints par module    
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
