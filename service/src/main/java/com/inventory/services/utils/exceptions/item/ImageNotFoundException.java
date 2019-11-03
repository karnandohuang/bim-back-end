package com.inventory.services.utils.exceptions.item;

public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(String name) {
        super(name + " not found");
    }
}
