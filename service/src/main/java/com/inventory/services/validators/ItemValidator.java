package com.inventory.services.validators;

import com.inventory.models.Item;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.inventory.services.ValidationConstant.*;

@Component
public class ItemValidator extends EntityValidator {

    public boolean validateImageUrlItem(String url) {
        if (url == null)
            return false;
        else if (url.equals("null"))
            return true;
        else {
            File dir = new File(url);
            if (!dir.exists())
                return false;
            return true;
        }
    }

    public String validateNullFieldItem(Item item) {
        if (item.getName() == null)
            return ITEM_NAME_EMPTY;
        if (item.getQty() == 0)
            return ITEM_QTY_EMPTY;
        if (item.getPrice() == 0)
            return ITEM_PRICE_EMPTY;
        if (item.getLocation() == null)
            return ITEM_LOCATION_EMPTY;
        if (item.getImageUrl() == null)
            return ITEM_IMAGE_EMPTY;
        return null;
    }
}
