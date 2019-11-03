package com.inventory.services.utils.exceptions.item;

public class ItemQtyLimitReachedException extends RuntimeException {
    public ItemQtyLimitReachedException(String name) {
        super("Request for value " + name + " is more than the actual qty");
    }
}
