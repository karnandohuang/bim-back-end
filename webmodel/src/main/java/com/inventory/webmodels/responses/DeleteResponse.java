package com.inventory.webmodels.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class DeleteResponse extends StandardResponse{

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("list")
    private List<StandardResponse> value;

    public DeleteResponse(String success, String errorMessage) {
        super(success, errorMessage);
    }
}
