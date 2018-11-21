package com.inventory.webmodels.requests.request;

import lombok.Data;

import java.util.List;

@Data
public class DeleteRequest {
    private List<String> ids;
}
