package com.inventory.webmodels.requests.assignment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class AssignmentRequest {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;

    private String employeeId;
    private String itemId;
    private Integer qty;
}
