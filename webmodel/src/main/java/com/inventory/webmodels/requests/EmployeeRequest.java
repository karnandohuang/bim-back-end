package com.inventory.webmodels.requests;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmployeeRequest {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;

    private String name;
    private String superiorId;
    private String email;
    private String password;
    private String dob;
    private String position;
    private String division;
}
