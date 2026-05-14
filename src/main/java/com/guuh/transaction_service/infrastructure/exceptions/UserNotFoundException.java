package com.guuh.transaction_service.infrastructure.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String mensagem){super(mensagem);}

    public UserNotFoundException(String mensagem, Throwable cause){super(mensagem,cause);}
}
