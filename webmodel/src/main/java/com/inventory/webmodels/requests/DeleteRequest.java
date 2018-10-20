package com.inventory.webmodels.requests;

import lombok.Data;

@Data
public class DeleteRequest {
    private String[] ids;
}
