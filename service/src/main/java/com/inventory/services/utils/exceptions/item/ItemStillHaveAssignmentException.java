package com.inventory.services.utils.exceptions.item;

public class ItemStillHaveAssignmentException extends RuntimeException {
    public ItemStillHaveAssignmentException() {
        super("Item have pending assignment(s)");
    }
}
