package com.app.manage_restaurant.services;

import java.util.UUID;

import com.app.manage_restaurant.cores.BaseService;
import com.app.manage_restaurant.dtos.request.PaymentMethodRequest;
import com.app.manage_restaurant.dtos.response.PaymentMethodResponse;
import com.app.manage_restaurant.entities.PaymentMethod;

public interface PaymentMethodService extends BaseService<PaymentMethod, PaymentMethodRequest, PaymentMethodResponse, UUID> {
   
}
