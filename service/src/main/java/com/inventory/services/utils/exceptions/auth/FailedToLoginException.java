package com.inventory.services.utils.exceptions.auth;

public class FailedToLoginException extends RuntimeException {
    public FailedToLoginException(String message) {
        super(message);
    }
}
