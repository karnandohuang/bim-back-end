package com.inventory.webmodels.requests.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class ItemRequest {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    private String sku;
    private String name;
    private int price;
    private String location;
    private int qty;
    private String imageUrl;
}
