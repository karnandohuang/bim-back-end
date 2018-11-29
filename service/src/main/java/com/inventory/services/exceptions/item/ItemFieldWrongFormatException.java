package com.inventory.services.exceptions.item;

public class ItemFieldWrongFormatException extends RuntimeException {
    public ItemFieldWrongFormatException(String errorMessage) {
        super(errorMessage);
    }
}
