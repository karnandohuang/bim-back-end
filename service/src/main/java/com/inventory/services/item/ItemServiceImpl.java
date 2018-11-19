package com.inventory.services.item;

import com.inventory.models.Item;
import com.inventory.models.Paging;
import com.inventory.models.Request;
import com.inventory.repositories.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

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
        List<Item> listOfItem;
        if (paging.getSortedType().matches("desc")) {
            listOfItem = itemRepository.findAllByNameContainingIgnoreCase(name,
                    PageRequest.of(paging.getPageNumber() - 1,
                            paging.getPageSize(),
                            Sort.Direction.DESC,
                            paging.getSortedBy())).getContent();
        } else {
            listOfItem = itemRepository.findAllByNameContainingIgnoreCase(name,
                    PageRequest.of(paging.getPageNumber() - 1,
                            paging.getPageSize(),
                            Sort.Direction.ASC,
                            paging.getSortedBy())).getContent();
        }
        float totalRecords = itemRepository.countAllByNameContainingIgnoreCase(name);
        paging.setTotalRecords((int) totalRecords);
        double totalPage = (int) Math.ceil((totalRecords / paging.getPageSize()));
        paging.setTotalPage((int) totalPage);
        return listOfItem;
    }

    @Override
    @Transactional
    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item changeItemQty(Request request) {
        Item item = itemRepository.findById(request.getItemId()).get();
        int qty = item.getQty() - request.getQty();
        if (qty < 0)
            return null;
        item.setQty(qty);
        item = itemRepository.save(item);
        return item;
    }

    @Override
    @Transactional
    public List<String> recoverItemQty(Map<String, Integer> listOfRecoveredItems) {
        List<String> errorOfItem = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : listOfRecoveredItems.entrySet()) {
            Item item = itemRepository.findById(entry.getKey()).get();
            item.setQty(item.getQty() + entry.getValue());
            item = itemRepository.save(item);
            if (item == null) {
                errorOfItem.add("Failed saving item");
            }
        }
        return errorOfItem;
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
    public String uploadFile(MultipartFile file, String itemSku) {
        Calendar cal = Calendar.getInstance();
        File createdDir = new File("C:\\Users\\olive\\Desktop\\bim-back-end\\resources\\" +
                cal.get(cal.YEAR) + "\\" + cal.get(cal.MONTH) + "\\" + itemSku);
        File convertFile = new File(createdDir.getAbsolutePath() + "\\" +
                file.getOriginalFilename());
        try {
//            if(!convertFile.exists())
            convertFile.getParentFile().mkdirs();
//            else
//                convertFile.createNewFile();
        } catch (Exception e) {
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
