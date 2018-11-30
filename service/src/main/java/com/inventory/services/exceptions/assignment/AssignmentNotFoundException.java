package com.inventory.services.exceptions.assignment;

public class AssignmentNotFoundException extends RuntimeException {
    public AssignmentNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
