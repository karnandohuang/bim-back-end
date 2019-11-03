package com.inventory.services.utils.exceptions.assignment;

public class AssignmentAuthorizedDifferentSuperiorException extends RuntimeException {
    public AssignmentAuthorizedDifferentSuperiorException(String superiorId) {
        super("Assignment status cannot be changed by : " + superiorId);
    }
}
