package com.inventory.services.exceptions.item;

public class ItemOutOfQtyException extends RuntimeException {
    public ItemOutOfQtyException(String name) {
        super(name + " is out of stock");
    }
}
