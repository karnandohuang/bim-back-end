package com.inventory.services.item;

import com.inventory.models.Assignment;
import com.inventory.models.Item;
import com.inventory.models.Paging;
import com.inventory.repositories.ItemRepository;
import com.inventory.services.exceptions.EntityNullFieldException;
import com.inventory.services.exceptions.item.*;
import com.inventory.services.validators.ItemValidator;
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

    @Autowired
    private ItemValidator validator;

    @Override
    @Transactional
    public Item getItem(String id) throws RuntimeException {
        if (!validator.validateIdFormatEntity(id, "IM"))
            throw new ItemNotFoundException("id not valid");
        try {
            return itemRepository.findById(id).get();
        } catch (RuntimeException e) {
            throw new ItemNotFoundException("id not valid");
        }
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
        setPagingTotalRecordsAndTotalPage(paging, totalRecords);
        return listOfItem;
    }

    private void setPagingTotalRecordsAndTotalPage(Paging paging, float totalRecords) {
        paging.setTotalRecords((int) totalRecords);
        double totalPage = (int) Math.ceil((totalRecords / paging.getPageSize()));
        paging.setTotalPage((int) totalPage);
    }

    @Override
    @Transactional
    public Item saveItem(Item item) throws RuntimeException {
        String nullFieldItem = validator.validateNullFieldEmployee(item);
        boolean isIdValid = true;
        boolean isImageUrlValid = validator.validateImageUrlItem(item.getImageUrl());
        if (item.getId() != null)
            isIdValid = validator.validateIdFormatEntity(item.getId(), "IM");
        if (nullFieldItem != null)
            throw new EntityNullFieldException(nullFieldItem);
        else if (!isIdValid)
            throw new ItemFieldWrongFormatException("id is not in the right format");
        else if (!isImageUrlValid)
            throw new ImagePathWrongException();
        else
            return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item changeItemQty(Assignment assignment) throws RuntimeException {
        Item item = itemRepository.findById(assignment.getItemId()).get();
        int qty = item.getQty() - assignment.getQty();
        if (item.getQty() <= 0)
            throw new ItemOutOfQtyException(item.getName());
        if (qty < 0)
            throw new ItemQtyLimitReachedException(item.getName());
        item.setQty(qty);
        item = itemRepository.save(item);
        return item;
    }

    @Override
    @Transactional
    public List<String> recoverItemQty(Map<String, Integer> listOfRecoveredItems) throws RuntimeException {
        List<String> errorOfItem = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : listOfRecoveredItems.entrySet()) {
            Item item;
            try {
                item = itemRepository.findById(entry.getKey()).get();
            } catch (RuntimeException e) {
                throw new ItemNotFoundException("Item : " + entry.getKey() + " is not exist");
            }
            item.setQty(item.getQty() + entry.getValue());
            itemRepository.save(item);
        }
        return errorOfItem;
    }

    @Override
    @Transactional
    public List<String> deleteItem(List<String> ids) throws RuntimeException {
        List<String> listOfNotFoundIds = new ArrayList<>();
        for (String id: ids){
            try {
                boolean isIdValid = validator.validateIdFormatEntity(id, "IM");
                if (!isIdValid)
                    throw new ItemFieldWrongFormatException("Id is not in the right format");
                itemRepository.deleteById(id);
            } catch (RuntimeException e) {
                throw new ItemNotFoundException("id : " + id + " is not exist");
            }
        }
        return listOfNotFoundIds;
    }

    @Override
    public String uploadFile(MultipartFile file, String itemId) throws RuntimeException {
        try {
            itemRepository.findById(itemId).get();
        } catch (RuntimeException e) {
            throw new ItemNotFoundException("item for sku : " + itemId + " is not exist");
        }
        Calendar cal = Calendar.getInstance();
        File createdDir = new File("C:\\Users\\olive\\Desktop\\bim-back-end\\resources\\" +
                cal.get(cal.YEAR) + "\\" + (cal.get(cal.MONTH) + 1) + "\\" + itemId);
        File convertFile = new File(createdDir.getAbsolutePath() + "\\" +
                file.getOriginalFilename());
        try {
            if (!convertFile.exists())
            convertFile.getParentFile().mkdirs();
            else
                convertFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(convertFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            try {
                fout.write(file.getBytes());
            } catch (NullPointerException e) {
                throw new ImageNotFoundException(file.getOriginalFilename());
            }
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convertFile.getAbsolutePath();
    }
}
