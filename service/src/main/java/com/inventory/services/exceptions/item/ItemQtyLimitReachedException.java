package com.inventory.services.exceptions.item;

public class ItemQtyLimitReachedException extends RuntimeException {
    public ItemQtyLimitReachedException(String name) {
        super("Request for item " + name + " is more than the actual qty");
    }
}
