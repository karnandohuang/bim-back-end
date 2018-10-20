package com.inventory.webmodels.responses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory.models.Employee;
import lombok.Data;

@Data
public class EmployeeResponse {

    private Employee value;

    ObjectMapper mapper = new ObjectMapper();
    String valueString;

    public String getJsonValue() {
        {
            try {
                valueString = mapper.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        }
        return valueString;
    }
}
