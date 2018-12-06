package com.inventory.services.exceptions.employee;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String value, String field) {
        super("employee of " + field + " : " + value + " is not exist");
    }
}
