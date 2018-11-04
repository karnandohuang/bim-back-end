package com.inventory.services;

import com.inventory.models.Item;
import com.inventory.models.Paging;
import com.inventory.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Override
    @Transactional
    public Item getItem(String id) {
        return itemRepository.findById(id).get();
    }

    @Override
    @Transactional
    public List<Item> getItemList(Paging paging) {
        List<Item> listOfSortedItem = new ArrayList<>();
        List<Item> listOfItem = new ArrayList<>();
        if(paging.getSortedType().matches("desc")) {
            listOfItem = itemRepository.findAll(new Sort(Sort.Direction.DESC, paging.getSortedBy()));
        }else {
            listOfItem = itemRepository.findAll(new Sort(Sort.Direction.ASC, paging.getSortedBy()));
        }
        int totalRecords = listOfItem.size();
        paging.setTotalRecords(totalRecords);
        int offset = (paging.getPageSize() * (paging.getPageNumber()-1));
        for(int i = 0; i < paging.getPageSize(); i++){
            if ((offset + i) >= totalRecords) {
                break;
            }
            listOfSortedItem.add(listOfItem.get((offset + i)));
        }
        return listOfSortedItem;
    }

    @Override
    @Transactional
    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public List<String> deleteItem(List<String> ids) {
        List<String> listOfNotFoundIds = new ArrayList<>();
        for (String id: ids){
            try {
                itemRepository.deleteById(id);
            }catch (NullPointerException e){
                listOfNotFoundIds.add("id" + id + "not found");
            }
        }
        return listOfNotFoundIds;
    }
}
