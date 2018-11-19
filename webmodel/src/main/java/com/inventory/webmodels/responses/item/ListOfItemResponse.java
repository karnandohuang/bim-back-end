package com.inventory.webmodels.responses.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inventory.models.Item;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListOfItemResponse {

    @JsonProperty("list")
    private List<Item> value;
}
