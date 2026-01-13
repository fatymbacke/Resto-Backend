package com.app.manage_restaurant.controllers;

import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.manage_restaurant.cores.BaseController;
import com.app.manage_restaurant.dtos.request.PaymentMethodRequest;
import com.app.manage_restaurant.dtos.response.PaymentMethodResponse;
import com.app.manage_restaurant.entities.PaymentMethod;
import com.app.manage_restaurant.services.PaymentMethodService;
import com.app.manage_restaurant.utilities.ReactiveExceptionHandler;
@RestController
@RequestMapping("/api/payment-methods")
@CrossOrigin(origins = "*")
public class PaymentMethodController extends BaseController<PaymentMethod, PaymentMethodRequest, PaymentMethodResponse, UUID> {    
    private PaymentMethodService service;
    private final ReactiveExceptionHandler exceptionHandler;
    public PaymentMethodController( PaymentMethodService service, ReactiveExceptionHandler exceptionHandler) {
        super(service, exceptionHandler, "PaymentMethod");
        this.service = service;
        this.exceptionHandler = exceptionHandler;
    }
    
}