package com.inventory.webmodels.responses.request;

import lombok.Data;

import java.util.List;

@Data
public class ChangeRequestStatusResponse {
    private List<String> errors;
    private List<String> errorOfItem;
}
