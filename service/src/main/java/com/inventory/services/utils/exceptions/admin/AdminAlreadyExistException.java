package com.inventory.services.utils.exceptions.admin;

public class AdminAlreadyExistException extends RuntimeException {
    public AdminAlreadyExistException(String email) {
        super("Admin with email : " + email + " already exist");
    }
}
