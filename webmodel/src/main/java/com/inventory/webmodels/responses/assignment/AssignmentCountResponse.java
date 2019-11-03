package com.inventory.webmodels.responses.assignment;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class AssignmentCountResponse {

    private Map<String, Double> listOfCount;
}
