package com.inventory.webmodels.responses.assignment;

import lombok.Data;

import java.util.List;

@Data
public class ChangeAssignmentStatusResponse {
    private List<String> errors;
    private List<String> errorOfItem;
}
