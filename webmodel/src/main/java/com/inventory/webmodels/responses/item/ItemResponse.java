package com.inventory.webmodels.responses.item;

import com.inventory.models.Item;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemResponse {

    private Item value;

}
