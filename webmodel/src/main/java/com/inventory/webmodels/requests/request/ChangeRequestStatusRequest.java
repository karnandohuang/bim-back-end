package com.inventory.webmodels.requests.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class ChangeRequestStatusRequest {
    private List<String> ids;
    private String status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String notes;
}
