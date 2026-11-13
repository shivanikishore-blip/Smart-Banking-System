package com.smartbank.exception;

import org.springframework.http.HttpStatus;

public class BankException extends RuntimeException {
    private final HttpStatus status;

    public BankException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public BankException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }

    public HttpStatus getStatus() { return status; }
}
