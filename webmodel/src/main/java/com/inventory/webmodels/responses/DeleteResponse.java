package com.inventory.webmodels.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DeleteResponse{

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("list")
    private List<String> value;
}
