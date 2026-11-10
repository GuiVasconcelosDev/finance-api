package com.guivasconcelos.finance_api.exception;

public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String email) {
        super("Já existe uma conta com este email");
    }
}
