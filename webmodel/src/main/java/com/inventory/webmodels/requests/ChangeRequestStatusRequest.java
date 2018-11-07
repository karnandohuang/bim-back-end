package com.inventory.webmodels.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class ChangeRequestStatusRequest {
    private String id;
    private String status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String notes;
}
