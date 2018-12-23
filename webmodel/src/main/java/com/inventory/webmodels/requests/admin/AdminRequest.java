package com.inventory.webmodels.requests.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class AdminRequest {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    private String email;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String password;

}
