package com.example.loan_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;

public class LoanValidationException extends Exception{

    public LoanValidationException(String exception){
        super(exception);
    }




}
