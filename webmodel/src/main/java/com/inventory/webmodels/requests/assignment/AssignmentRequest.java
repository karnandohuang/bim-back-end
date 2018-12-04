package com.inventory.webmodels.requests.assignment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.inventory.webmodels.requests.item.ItemRequest;
import lombok.Data;

import java.util.List;

@Data
public class AssignmentRequest {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;

    private String employeeId;
    private List<ItemRequest> items;
}
