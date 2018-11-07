package com.inventory.services;

import com.inventory.models.Item;
import com.inventory.models.Paging;
import com.inventory.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

    private List<Item> getItemListFromRepository(String name, Paging paging) {
        List<Item> listOfItem;
        if(paging.getSortedType().matches("desc")) {
            listOfItem = itemRepository.findAllByNameContaining(name, new Sort(Sort.Direction.DESC, paging.getSortedBy()));
        }else {
            listOfItem = itemRepository.findAllByNameContaining(name, new Sort(Sort.Direction.ASC, paging.getSortedBy()));
        }
        return listOfItem;
    }

    @Override
    @Transactional
    public List<Item> getItemList(String name, Paging paging) {
        List<Item> listOfSortedItem = new ArrayList<>();
        List<Item> listOfItem = getItemListFromRepository(name, paging);
        int totalRecords = listOfItem.size();
        paging.setTotalRecords(totalRecords);
        int offset = (paging.getPageSize() * (paging.getPageNumber()-1));
        int totalPage = (int) Math.ceil((float) (totalRecords / paging.getPageSize()));
        paging.setTotalPage(totalPage);
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

    @Override
    public String uploadFile(MultipartFile file) {
        File convertFile = new File("C:\\Users\\olive\\Desktop\\bim-back-end\\resources\\" +
                file.getOriginalFilename());
        try {
            convertFile.createNewFile();
        } catch (IOException e) {
            //throw custom exception;
            e.printStackTrace();
        }
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(convertFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            fout.write(file.getBytes());
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convertFile.getAbsolutePath();
    }
}
