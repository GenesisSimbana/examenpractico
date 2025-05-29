package com.examen.simbana.general.exception;

public class ProductoNotFoundException extends RuntimeException {

    private final Integer errorCode;

    public ProductoNotFoundException(String message) {
        super(message);
        this.errorCode = 2;
    }

    @Override
    public String getMessage() {
        return "Error code: " + this.errorCode + ", message: " + super.getMessage();
    }

    public Integer getErrorCode() {
        return errorCode;
    }
} 