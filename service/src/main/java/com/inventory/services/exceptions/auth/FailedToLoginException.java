package com.inventory.services.exceptions.auth;

public class FailedToLoginException extends RuntimeException {
    public FailedToLoginException(String message) {
        super(message);
    }
}
