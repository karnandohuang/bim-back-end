package com.inventory.services.exceptions;

public class EntityNullFieldException extends RuntimeException {
    public EntityNullFieldException(String errorMessage) {
        super(errorMessage);
    }
}
