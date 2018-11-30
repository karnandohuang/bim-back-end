package com.inventory.services.item;

import com.inventory.models.Assignment;
import com.inventory.models.Item;
import com.inventory.models.Paging;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


public interface ItemService {

    Item getItem(String id);

    List<Item> getItemList(String name, Paging paging);

    Item saveItem(Item item);

    String deleteItem(List<String> ids);

    String uploadFile(MultipartFile file, String itemSku);

    Item changeItemQty(Assignment assignment);

    List<String> recoverItemQty(Map<String, Integer> listOfRecoveredItems);

}
