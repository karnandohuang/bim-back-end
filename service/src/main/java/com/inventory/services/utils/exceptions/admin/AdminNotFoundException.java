package com.inventory.services.utils.exceptions.admin;

public class AdminNotFoundException extends RuntimeException {
    public AdminNotFoundException(String value, String field) {
        super("admin of " + field + " : " + value + " is not exist");
    }
}
