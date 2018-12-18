package com.inventory.webmodels.requests.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class AdminRequest {

    private String email;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String password;

}
