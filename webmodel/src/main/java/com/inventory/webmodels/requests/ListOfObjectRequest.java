package com.inventory.webmodels.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class ListOfObjectRequest {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;

    private int pageNumber;
    private int pageSize;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sortedBy;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sortedType;
}
