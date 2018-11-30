package com.inventory.services.exceptions.item;

public class ItemStillHavePendingAssignmentException extends RuntimeException {
    public ItemStillHavePendingAssignmentException() {
        super("Item still have pending assignment(s)");
    }
}
