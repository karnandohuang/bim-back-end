package com.inventory.services.utils.exceptions.auth;

public class LoginEmptyException extends RuntimeException {
    public LoginEmptyException(String errorMessage) {
        super(errorMessage);
    }
}
