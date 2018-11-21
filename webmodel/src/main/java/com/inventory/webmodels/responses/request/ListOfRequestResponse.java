package com.inventory.webmodels.responses.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListOfRequestResponse {

    @JsonProperty("list")
    private List<RequestResponse> value;
}
