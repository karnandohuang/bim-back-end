package com.inventory.services.utils.validators;

import com.inventory.models.entity.Item;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.inventory.services.utils.constants.ValidationConstant.*;

@Component
public class ItemValidator extends EntityValidator {

    public boolean validateImageUrlItem(String url) {
        if ((url != null && !url.equals("null") && new File(url).exists()) || url == null || url.equals("null"))
            return true;
        return false;
    }

    public boolean validateImageUrlExist(String url) {
        if (new File(url).exists() && url != null && !url.equals("null"))
            return true;
        return false;
    }

    public String validateNullFieldItem(Item item) {
        if (item.getName() == null)
            return ITEM_NAME_EMPTY;
        else if (item.getQty() == 0)
            return ITEM_QTY_EMPTY;
        else if (item.getPrice() == 0)
            return ITEM_PRICE_EMPTY;
        else if (item.getLocation() == null)
            return ITEM_LOCATION_EMPTY;
        return null;
    }
}