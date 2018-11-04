package com.inventory.services;

import com.inventory.models.Item;
import com.inventory.models.Paging;

import java.util.List;


public interface ItemService {

    Item getItem(String id);

    List<Item> getItemList(String name, Paging paging);

    Item saveItem(Item item);

    List<String> deleteItem(List<String> ids);

}
