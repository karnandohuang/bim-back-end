package com.inventory.webmodels.requests;

import lombok.Data;

import java.util.List;

@Data
public class DeleteRequest {
    private List<String> ids;
}
