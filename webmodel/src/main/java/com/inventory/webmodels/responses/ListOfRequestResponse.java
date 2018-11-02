package com.inventory.webmodels.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inventory.models.Request;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListOfRequestResponse {

    @JsonProperty("list")
    private List<Request> value;
}
