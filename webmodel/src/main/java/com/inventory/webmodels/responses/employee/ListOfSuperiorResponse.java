package com.inventory.webmodels.responses.employee;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inventory.models.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListOfSuperiorResponse {

    @JsonProperty("list")
    private List<Employee> value;

}
