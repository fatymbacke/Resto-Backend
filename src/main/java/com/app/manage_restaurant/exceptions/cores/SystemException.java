package com.app.manage_restaurant.exceptions.cores;


public class SystemException extends BaseException {
    public SystemException(String message) {
        super(message, "SYSTEM_ERROR");
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }
}