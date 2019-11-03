package com.inventory.services.utils.exceptions;

public class EntityNullFieldException extends RuntimeException {
    public EntityNullFieldException(String errorMessage) {
        super(errorMessage);
    }
}
