package com.inventory.services.exceptions.admin;

public class AdminAlreadyExistException extends RuntimeException {
    public AdminAlreadyExistException(String email) {
        super("Admin with email : " + email + " already exist");
    }
}
