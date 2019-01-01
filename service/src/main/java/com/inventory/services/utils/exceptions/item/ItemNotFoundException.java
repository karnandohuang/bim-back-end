package com.inventory.services.utils.exceptions.item;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String value, String field) {
        super("item of " + field + " : " + value + " is not exist");
    }
}
