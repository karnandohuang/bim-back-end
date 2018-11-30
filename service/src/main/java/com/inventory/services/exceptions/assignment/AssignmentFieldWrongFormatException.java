package com.inventory.services.exceptions.assignment;

public class AssignmentFieldWrongFormatException extends RuntimeException {
    public AssignmentFieldWrongFormatException(String errorMessage) {
        super(errorMessage);
    }
}
