package com.inventory.services.exceptions;

public class EmployeeFieldWrongFormatException extends RuntimeException {
    public EmployeeFieldWrongFormatException(String errorMessage) {
        super(errorMessage);
    }
}
