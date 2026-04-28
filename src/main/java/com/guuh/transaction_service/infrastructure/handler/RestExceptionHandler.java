package com.guuh.transaction_service.infrastructure.handler;

import com.guuh.transaction_service.infrastructure.exceptions.InvalidAmountException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(InvalidAmountException.class)
    private ResponseEntity<RestErrorMessage> InvalidAmountHandler(InvalidAmountException e){
        RestErrorMessage threatResponse = new RestErrorMessage(HttpStatus.CONFLICT, e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(threatResponse);
    }

}
