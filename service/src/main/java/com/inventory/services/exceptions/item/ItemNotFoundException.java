package com.inventory.services.exceptions.item;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
