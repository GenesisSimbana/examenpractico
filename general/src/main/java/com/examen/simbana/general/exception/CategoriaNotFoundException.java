package com.examen.simbana.general.exception;

public class CategoriaNotFoundException extends RuntimeException {

    private final Integer errorCode;

    public CategoriaNotFoundException(String message) {
        super(message);
        this.errorCode = 1;
    }

    @Override
    public String getMessage() {
        return "Error code: " + this.errorCode + ", message: " + super.getMessage();
    }

    public Integer getErrorCode() {
        return errorCode;
    }
} 