package com.inventory.services.exceptions.employee;

public class EmployeeFieldWrongFormatException extends RuntimeException {
    public EmployeeFieldWrongFormatException(String errorMessage) {
        super(errorMessage);
    }
}
