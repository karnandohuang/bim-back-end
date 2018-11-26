package com.inventory.webmodels.responses.request;

import com.inventory.models.Request;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequestResponse {

    private Request request;
    private String employeeName;
    private String itemSku;
    private String itemName;
}
