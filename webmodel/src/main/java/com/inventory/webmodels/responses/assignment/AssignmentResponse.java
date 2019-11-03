package com.inventory.webmodels.responses.assignment;

import com.inventory.models.entity.Assignment;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AssignmentResponse {

    private Assignment assignment;
}
