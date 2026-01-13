package com.app.manage_restaurant.exceptions.web;


import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

    public NotFoundException(String resource, Object id) {
        super(HttpStatus.NOT_FOUND, String.format("%s non trouvé avec l'id : %s", resource, id));
    }
}