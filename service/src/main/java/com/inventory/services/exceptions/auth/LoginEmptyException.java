package com.inventory.services.exceptions.auth;

public class LoginEmptyException extends RuntimeException {
    public LoginEmptyException(String attribute) {
        super(attribute + " is empty");
    }
}
