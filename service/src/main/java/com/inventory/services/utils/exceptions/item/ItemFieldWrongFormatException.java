package com.inventory.services.utils.exceptions.item;

public class ItemFieldWrongFormatException extends RuntimeException {
    public ItemFieldWrongFormatException(String errorMessage) {
        super(errorMessage);
    }
}
