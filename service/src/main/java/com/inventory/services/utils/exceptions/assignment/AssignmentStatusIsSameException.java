package com.inventory.services.utils.exceptions.assignment;

public class AssignmentStatusIsSameException extends RuntimeException {
    public AssignmentStatusIsSameException(String status) {
        super("Status is already " + status);
    }
}
