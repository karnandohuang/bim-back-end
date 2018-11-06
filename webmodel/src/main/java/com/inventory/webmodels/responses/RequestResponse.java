package com.inventory.webmodels.responses;

import com.inventory.models.Request;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequestResponse {

    private Request request;
    private String employeeName;
    private String itemSKU;
    private String itemName;
}
