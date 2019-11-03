package com.inventory.services.utils.exceptions.admin;

public class AdminFieldWrongFormatException extends RuntimeException {
    public AdminFieldWrongFormatException(String errorMessage) {
        super(errorMessage);
    }
}
