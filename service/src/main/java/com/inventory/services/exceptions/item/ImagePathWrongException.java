package com.inventory.services.exceptions.item;

public class ImagePathWrongException extends RuntimeException {
    public ImagePathWrongException() {
        super("image path not found");
    }
}
