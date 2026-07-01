package com.guuh.transaction_service.infrastructure.exceptions;

public class DateLimitException extends RuntimeException {
    public DateLimitException(String message) {
        super(message);
    }
    public DateLimitException(String message, Throwable cause){super(message,cause);}
}
