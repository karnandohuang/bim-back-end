package com.inventory.services.utils.exceptions.item;

public class ImagePathWrongException extends RuntimeException {
    public ImagePathWrongException() {
        super("image path not found");
    }
}
