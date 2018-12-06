package com.inventory.services.exceptions.assignment;

public class AssignmentNotFoundException extends RuntimeException {
    public AssignmentNotFoundException(String value, String field) {
        super("assignment of " + field + " : " + value + " is not exist");
    }
}
