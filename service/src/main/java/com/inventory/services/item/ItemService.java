package com.inventory.services.item;

import com.inventory.models.Item;
import com.inventory.models.Paging;
import com.inventory.models.Request;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


public interface ItemService {

    Item getItem(String id);

    List<Item> getItemList(String name, Paging paging);

    Item saveItem(Item item);

    List<String> deleteItem(List<String> ids);

    String uploadFile(MultipartFile file, String itemSku);

    Item changeItemQty(Request request);

    List<String> recoverItemQty(Map<String, Integer> listOfRecoveredItems);

}
