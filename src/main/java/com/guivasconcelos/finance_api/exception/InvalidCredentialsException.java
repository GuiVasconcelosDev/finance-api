package com.guivasconcelos.finance_api.exception;

public class InvalidCredentialsException extends RuntimeException{

    public InvalidCredentialsException() {
        super("E-mail ou senha inválidos");
    }
}
