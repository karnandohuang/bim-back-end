package com.inventory.webmodels.responses;

import com.inventory.models.Request;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestResponse {

    private Request request;
}
