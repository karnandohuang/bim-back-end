package com.inventory.webmodels.responses.assignment;

import com.inventory.models.Assignment;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AssignmentResponse {

    private Assignment assignment;
    private String employeeName;
    private String itemSku;
    private String itemName;
}