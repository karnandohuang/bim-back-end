package com.inventory.webmodels.responses.assignment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListOfAssignmentResponse {

    @JsonProperty("list")
    private List<AssignmentResponse> value;
}
