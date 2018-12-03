package com.inventory.webmodels.requests.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class ItemRequest {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;

    private String name;
    private int price;
    private String location;
    private int qty;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String imageUrl;
}
