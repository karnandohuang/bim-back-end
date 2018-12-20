package com.inventory.services.item;

import com.inventory.models.Assignment;
import com.inventory.models.Item;
import com.inventory.models.Paging;
import com.inventory.repositories.ItemRepository;
import com.inventory.services.GeneralMapper;
import com.inventory.services.assignment.AssignmentService;
import com.inventory.services.exceptions.EntityNullFieldException;
import com.inventory.services.exceptions.item.*;
import com.inventory.services.helper.PagingHelper;
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

import static com.inventory.services.constants.ExceptionConstant.ID_WRONG_FORMAT_ERROR;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemValidator validator;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private GeneralMapper mapper;

    @Autowired
    private PagingHelper pagingHelper;

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
    public List<Item> getItemList(String search, Paging paging) {
        List<Item> listOfItem;
        PageRequest pageRequest;
        if (paging.getSortedType().matches("desc")) {
            pageRequest = PageRequest.of(
                    paging.getPageNumber() - 1,
                    paging.getPageSize(),
                    Sort.Direction.DESC,
                    paging.getSortedBy());
        } else {
            pageRequest = PageRequest.of(
                    paging.getPageNumber() - 1,
                    paging.getPageSize(),
                    Sort.Direction.ASC,
                    paging.getSortedBy());
        }
        listOfItem = itemRepository.findAllByNameContainingIgnoreCaseOrIdContainingIgnoreCase(search, search, pageRequest).getContent();
        float totalRecords = itemRepository.countAllByNameContainingIgnoreCaseOrIdContainingIgnoreCase(search, search);
        pagingHelper.setPagingTotalRecordsAndTotalPage(paging, totalRecords);
        return listOfItem;
    }



    private Item editItem(Item request) {
        this.getItem(request.getId());
        return mapper.map(request, Item.class);
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

        item = itemRepository.save(item);
        itemRepository.flush();
        return item;
    }

    @Override
    @Transactional
    public Item changeItemQty(Assignment assignment) throws RuntimeException {
        Item item = this.getItem(assignment.getItem().getId());
        int qty = item.getQty() - assignment.getQty();
        if (item.getQty() <= 0)
            throw new ItemOutOfQtyException(item.getName());
        else if (qty < 0)
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

    private Boolean checkItemAssignmentCount(String itemId) {
        return assignmentService.getAssignmentCountByItemIdAndStatus(itemId, "Pending") > 0;
    }

    @Override
    @Transactional
    public String deleteItem(List<String> ids) throws RuntimeException {
        for (String id: ids){
            boolean isIdValid = validator.validateIdFormatEntity(id, ITEM_ID_PREFIX);
                if (!isIdValid)
                    throw new ItemFieldWrongFormatException(ID_WRONG_FORMAT_ERROR);
                else if (checkItemAssignmentCount(id))
                    throw new ItemStillHaveAssignmentException();
                else {
                    try {
                        this.getItem(id);
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
        item = this.getItem(itemId);
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
        item.setImageUrl(convertFile.getAbsolutePath());
        this.saveItem(item);
        return "Upload image success";
    }
}
