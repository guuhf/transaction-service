package com.guuh.transaction_service.infrastructure.exceptions;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String mensagem){super(mensagem);}

    public UserAlreadyExistsException(String mensagem, Throwable cause){super(mensagem,cause);}
}
