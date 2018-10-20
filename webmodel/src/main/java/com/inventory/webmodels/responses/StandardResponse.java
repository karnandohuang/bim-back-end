package com.inventory.webmodels.responses;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({"success", "errorMessage"})
public class StandardResponse {
    private String success;
    private String errorMessage;

    public StandardResponse(String success, String errorMessage){
        this.success = success;
        this.errorMessage = errorMessage;
    }
}
