package com.inventory.services.exceptions.employee;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
