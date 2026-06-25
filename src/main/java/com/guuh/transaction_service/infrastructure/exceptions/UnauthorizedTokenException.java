package com.guuh.transaction_service.infrastructure.exceptions;

public class UnauthorizedTokenException extends RuntimeException {
    public UnauthorizedTokenException(String message) {
        super(message);
    }
    public UnauthorizedTokenException(String message, Throwable cause){super(message,cause);}
}
