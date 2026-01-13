package com.app.manage_restaurant.exceptions.cores;

public abstract class BaseException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String errorCode;
    private final String details;

    public BaseException(String message) {
        super(message);
        this.errorCode = "GENERIC_ERROR";
        this.details = null;
    }

    public BaseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.details = null;
    }

    public BaseException(String message, String errorCode, String details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "GENERIC_ERROR";
        this.details = null;
    }

    // Getters
    public String getErrorCode() { return errorCode; }
    public String getDetails() { return details; }
}
