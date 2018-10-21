package com.inventory.webmodels.responses;
import com.inventory.models.Employee;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;


@Setter
@Getter
public class EmployeeResponse {

    private Employee value;
}
