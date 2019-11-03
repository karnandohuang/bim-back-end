package com.inventory.services.utils.exceptions.employee;

public class EmployeeFieldWrongFormatException extends RuntimeException {
    public EmployeeFieldWrongFormatException(String errorMessage) {
        super(errorMessage);
    }
}
