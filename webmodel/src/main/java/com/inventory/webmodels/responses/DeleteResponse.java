package com.inventory.webmodels.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DeleteResponse {

    @JsonProperty("list")
    private List<StandardResponse> value;
}
