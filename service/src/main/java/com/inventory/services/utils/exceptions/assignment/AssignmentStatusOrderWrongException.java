package com.inventory.services.utils.exceptions.assignment;

public class AssignmentStatusOrderWrongException extends RuntimeException {
    public AssignmentStatusOrderWrongException(String status, String changedStatus) {
        super("Status : " + status + " cannot be changed to " + changedStatus);
    }
}
