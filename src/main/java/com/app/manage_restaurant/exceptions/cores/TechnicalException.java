package com.app.manage_restaurant.exceptions.cores;

public class TechnicalException extends BaseException {
    public TechnicalException(String message) {
        super(message, "TECHNICAL_ERROR");
    }

    public TechnicalException(String message, Throwable cause) {
        super(message, cause);
    }

    public TechnicalException(String message, String errorCode, Throwable cause) {
        super(message, errorCode);
    }
}