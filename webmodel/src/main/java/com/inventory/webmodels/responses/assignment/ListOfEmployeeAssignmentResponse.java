package com.inventory.webmodels.responses.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ListOfEmployeeAssignmentResponse {
    @JsonProperty("list")
    private List<EmployeeAssignmentResponse> value;
}
