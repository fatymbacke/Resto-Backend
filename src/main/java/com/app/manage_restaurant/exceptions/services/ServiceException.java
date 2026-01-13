package com.app.manage_restaurant.exceptions.services;

import com.app.manage_restaurant.exceptions.cores.TechnicalException;

public class ServiceException extends TechnicalException {
    private final String serviceName;

    public ServiceException(String serviceName, String message) {
        super(message);
        this.serviceName = serviceName;
    }

    public ServiceException(String serviceName, String message, Throwable cause) {
        super(message, cause);
        this.serviceName = serviceName;
    }

    public String getServiceName() { return serviceName; }
}