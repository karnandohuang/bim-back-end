package com.inventory.services.utils.exceptions.employee;

public class EmployeeStillHavePendingAssignmentException extends RuntimeException {
    public EmployeeStillHavePendingAssignmentException() {
        super("Employee still have pending assignment(s)");
    }
}
