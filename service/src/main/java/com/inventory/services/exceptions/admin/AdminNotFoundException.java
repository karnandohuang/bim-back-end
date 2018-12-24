package com.inventory.services.exceptions.admin;

public class AdminNotFoundException extends RuntimeException {
    public AdminNotFoundException(String value, String field) {
        super("admin of " + field + " : " + value + " is not exist");
    }
}
