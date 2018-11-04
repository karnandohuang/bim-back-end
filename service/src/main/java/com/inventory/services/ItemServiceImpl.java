package com.inventory.services;

import com.inventory.models.Item;
import com.inventory.models.Paging;
import com.inventory.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Item> getItemList(String name, Paging paging) {
        List<Item> listOfSortedItem = new ArrayList<>();
        List<Item> listOfItem = new ArrayList<>();
        if(paging.getSortedType().matches("desc"))
            listOfItem = itemRepository.findAllByNameIsContainingDesc(name, paging.getSortedBy());
        else
            listOfItem = itemRepository.findAllByNameIsContainingAsc(name, paging.getSortedBy());
        long totalRecords = listOfItem.size();
        long offset = ((totalRecords / paging.getPageSize()) * paging.getPageNumber());
        for(long i = (offset+1); i < paging.getPageSize(); i++){
            listOfSortedItem.add(listOfItem.get((int)i));
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
