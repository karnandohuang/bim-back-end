package com.inventory.services.item;

import com.inventory.models.Assignment;
import com.inventory.models.Item;
import com.inventory.models.Paging;
import com.inventory.repositories.ItemRepository;
import com.inventory.services.assignment.AssignmentService;
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

import static com.inventory.services.ExceptionConstant.ID_WRONG_FORMAT_ERROR;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemValidator validator;

    @Autowired
    private AssignmentService assignmentService;

    private final static String ITEM_ID_PREFIX = "IM";

    @Override
    @Transactional
    public Item getItem(String id) throws RuntimeException {
        if (!validator.validateIdFormatEntity(id, ITEM_ID_PREFIX))
            throw new ItemFieldWrongFormatException(ID_WRONG_FORMAT_ERROR);
        try {
            return itemRepository.findById(id).get();
        } catch (RuntimeException e) {
            throw new ItemNotFoundException(id, "Id");
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

    private Item editItem(Item item) {
        Item newItem;
        try {
            newItem = itemRepository.findById(item.getId()).get();
        } catch (RuntimeException e) {
            throw new ItemNotFoundException(item.getId(), "Id");
        }
        newItem.setName(item.getName());
        newItem.setPrice(item.getPrice());
        newItem.setQty(item.getQty());
        newItem.setLocation(item.getLocation());
        newItem.setImageUrl(item.getImageUrl());
        return newItem;
    }

    @Override
    @Transactional
    public Item saveItem(Item request) throws RuntimeException {

        String nullFieldItem = validator.validateNullFieldItem(request);

        Item item;

        if (request.getId() != null) {
            item = editItem(request);
        } else {
            item = request;
        }

        if (item.getImageUrl() == null)
            item.setImageUrl("null");

        boolean isIdValid = true;

        boolean isImageUrlValid = validator.validateImageUrlItem(item.getImageUrl());

        if (item.getId() != null)
            isIdValid = validator.validateIdFormatEntity(item.getId(), ITEM_ID_PREFIX);

        if (nullFieldItem != null)
            throw new EntityNullFieldException(nullFieldItem);

        else if (!isIdValid)
            throw new ItemFieldWrongFormatException(ID_WRONG_FORMAT_ERROR);

        else if (!isImageUrlValid)
            throw new ImagePathWrongException();

        else {
            item = itemRepository.save(item);
            itemRepository.flush();
            return item;
        }
    }

    @Override
    @Transactional
    public Item changeItemQty(Assignment assignment) throws RuntimeException {
        Item item = itemRepository.findById(assignment.getItem().getId()).get();
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
    public String recoverItemQty(Map<String, Integer> listOfRecoveredItems) throws RuntimeException {
        List<String> errorOfItem = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : listOfRecoveredItems.entrySet()) {
            Item item;
            try {
                item = itemRepository.findById(entry.getKey()).get();
            } catch (RuntimeException e) {
                throw new ItemNotFoundException(entry.getKey(), "Id");
            }
            item.setQty(item.getQty() + entry.getValue());
            itemRepository.save(item);
        }
        return "Recover success";
    }

    @Override
    @Transactional
    public String deleteItem(List<String> ids) throws RuntimeException {
        for (String id: ids){
            boolean isIdValid = validator.validateIdFormatEntity(id, ITEM_ID_PREFIX);
                if (!isIdValid)
                    throw new ItemFieldWrongFormatException(ID_WRONG_FORMAT_ERROR);
                else if (assignmentService.getAssignmentCountByItemIdAndStatus(id, "Pending") > 0 ||
                        assignmentService.getAssignmentCountByItemIdAndStatus(id, "Approved") > 0 ||
                        assignmentService.getAssignmentCountByItemIdAndStatus(id, "Received") > 0 ||
                        assignmentService.getAssignmentCountByItemIdAndStatus(id, "Rejected") > 0)
                    throw new ItemStillHaveAssignmentException();
                else {
                    try {
                        itemRepository.findById(id).get();
                    } catch (RuntimeException e) {
                        throw new ItemNotFoundException(id, "Id");
                    }
                    itemRepository.deleteById(id);
                }
        }
        return "Delete Success";
    }

    @Override
    public String uploadFile(MultipartFile file, String itemId) throws RuntimeException {
        Item item;
        try {
            item = itemRepository.findById(itemId).get();
        } catch (RuntimeException e) {
            throw new ItemNotFoundException(itemId, "Id");
        }
        Calendar cal = Calendar.getInstance();
        File createdDir = new File("/Users/karnandohuang/Documents/Projects/blibli-inventory-system/bim-back-end/" +
                cal.get(cal.YEAR) + "/" + (cal.get(cal.MONTH) + 1) + "/" + itemId);
        File convertFile = new File(createdDir.getAbsolutePath() + "/" +
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
        item.setImageUrl(convertFile.getAbsolutePath());
        this.saveItem(item);
        return "Upload image success";
    }
}
