package com.inventory.services.utils.exceptions.employee;

public class EmployeeAlreadyExistException extends RuntimeException {
    public EmployeeAlreadyExistException(String email) {
        super("Employee with email : " + email + " already exist");
    }
}
