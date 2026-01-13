package com.app.manage_restaurant.exceptions.web;

import org.springframework.http.HttpStatus;

import com.app.manage_restaurant.exceptions.cores.BaseException;

public abstract class ApiException extends BaseException {
    private final HttpStatus httpStatus;

    public ApiException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public ApiException(HttpStatus httpStatus, String message, String errorCode) {
        super(message, errorCode);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() { return httpStatus; }
}