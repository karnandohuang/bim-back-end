package com.inventory.webmodels.responses.assignment;

import com.inventory.models.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAssignmentResponse {
    private String assignmentId;
    private Item item;
    private String status;
}
