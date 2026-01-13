package com.app.manage_restaurant.exceptions.cores;


public class BusinessException extends BaseException {
    public BusinessException(String message) {
        super(message, "BUSINESS_ERROR");
    }

    public BusinessException(String message, String errorCode) {
        super(message, errorCode);
    }

    public BusinessException(String message, String errorCode, String details) {
        super(message, errorCode, details);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}