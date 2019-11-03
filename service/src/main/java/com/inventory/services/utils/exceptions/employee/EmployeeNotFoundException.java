package com.inventory.services.utils.exceptions.employee;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String value, String field) {
        super("value of " + field + " : " + value + " is not exist");
    }
}
