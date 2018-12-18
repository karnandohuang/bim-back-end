package com.inventory.services.exceptions.admin;

public class AdminFieldWrongFormatException extends RuntimeException {
    public AdminFieldWrongFormatException(String errorMessage) {
        super(errorMessage);
    }
}
