package com.inventory.services.exceptions.item;

public class ItemStillHaveAssignmentException extends RuntimeException {
    public ItemStillHaveAssignmentException() {
        super("Item already in assignment(s)");
    }
}
