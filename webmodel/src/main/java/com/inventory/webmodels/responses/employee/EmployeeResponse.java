package com.inventory.webmodels.responses.employee;

import com.inventory.models.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class EmployeeResponse {
    private Employee value;
}
