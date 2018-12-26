package com.inventory.services.exceptions.employee;

public class EmployeeSuperiorSameIdException extends RuntimeException {
    public EmployeeSuperiorSameIdException() {
        super("Employee cannot supervise themselves!");
    }
}
