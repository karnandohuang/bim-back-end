package com.inventory.webmodels.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class RequestHTTPRequest {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;

    private String employeeId;
    private String itemId;
    private Integer qty;
}
