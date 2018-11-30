package com.inventory.services.exceptions.assignment;

public class AssignmentStatusIsSameException extends RuntimeException {
    public AssignmentStatusIsSameException(String errorMessage) {
        super(errorMessage);
    }
}
