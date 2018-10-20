package com.inventory.webmodels.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.models.Employee;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ListOfEmployeeResponse {

    @JsonProperty("list")
    private List<Employee> value;
}
