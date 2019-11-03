package com.inventory.services.validator;

import com.inventory.models.entity.Item;
import com.inventory.services.utils.validators.ItemValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static com.inventory.services.utils.constants.ValidationConstant.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ItemValidatorTest {

    @InjectMocks
    ItemValidator validator;

    @Test
    public void validateNullFieldItemNotFoundSuccess() {
        Item item = setItem();
        assertNull(validator.validateNullFieldItem(item));
    }

    @Test
    public void validateNullFieldItemNameFoundFailed() {
        Item item = setItem();
        item.setName(null);
        assertEquals(ITEM_NAME_EMPTY, validator.validateNullFieldItem(item));
    }

    @Test
    public void validateNullFieldItemPriceFoundFailed() {
        Item item = setItem();
        item.setPrice(0);
        assertEquals(ITEM_PRICE_EMPTY, validator.validateNullFieldItem(item));
    }

    @Test
    public void validateNullFieldItemQtyFoundFailed() {
        Item item = setItem();
        item.setQty(0);
        assertEquals(ITEM_QTY_EMPTY, validator.validateNullFieldItem(item));
    }

    @Test
    public void validateNullFieldItemLocationFoundFailed() {
        Item item = setItem();
        item.setLocation(null);
        assertEquals(ITEM_LOCATION_EMPTY, validator.validateNullFieldItem(item));
    }

    @Test
    public void validateImageUrlNullStringSuccess() {
        String url = "null";
        assertTrue(validator.validateImageUrlItem(url));
    }

    @Test
    public void validateImageUrlNullSuccess() {
        String url = null;
        assertTrue(validator.validateImageUrlItem(url));
    }

    @Test
    public void validateImageUrlNotFoundFailed() {
        String url = "abc";
        assertFalse(validator.validateImageUrlItem(url));
    }

    @Test
    public void validateImageUrlNotExistSuccess() {
        String url = "abc";
        assertFalse(validator.validateImageUrlExist(url));
    }

    private Item setItem() {
        Item item = new Item();
        item.setId("IM001");
        item.setName("Example");
        item.setPrice(100);
        item.setQty(1);
        item.setLocation("Example");
        item.setImageUrl("null");
        return item;
    }
}
