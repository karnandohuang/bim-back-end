package com.inventory.services.utils.exceptions.assignment;

public class AssignmentFieldWrongFormatException extends RuntimeException {
    public AssignmentFieldWrongFormatException(String errorMessage) {
        super(errorMessage);
    }
}
