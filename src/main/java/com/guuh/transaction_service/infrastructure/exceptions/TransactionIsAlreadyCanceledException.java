package com.guuh.transaction_service.infrastructure.exceptions;

public class TransactionIsAlreadyCanceledException extends RuntimeException {
    public TransactionIsAlreadyCanceledException(String message) {
        super(message);
    }
    public TransactionIsAlreadyCanceledException(String message, Throwable cause){super(message,cause);}
}
