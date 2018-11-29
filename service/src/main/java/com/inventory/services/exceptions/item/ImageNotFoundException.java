package com.inventory.services.exceptions.item;

public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(String name) {
        super(name + " not found");
    }
}
