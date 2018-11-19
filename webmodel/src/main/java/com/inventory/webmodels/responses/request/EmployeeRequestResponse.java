package com.inventory.webmodels.responses.request;

import com.inventory.models.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequestResponse {
    private String requestId;
    private Item item;
    private String status;
}
