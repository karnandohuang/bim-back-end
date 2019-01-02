package com.inventory.webmodels.requests.assignment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class ChangeAssignmentStatusRequest {
    private List<String> ids;
    private String status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String notes;
}
