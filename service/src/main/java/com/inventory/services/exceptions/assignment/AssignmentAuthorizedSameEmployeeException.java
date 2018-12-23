package com.inventory.services.exceptions.assignment;

public class AssignmentAuthorizedSameEmployeeException extends RuntimeException {
    public AssignmentAuthorizedSameEmployeeException(String assignmentId, String employeeId) {
        super("Assignment status of : " + assignmentId + " cannot be changed by " + employeeId);
    }
}
