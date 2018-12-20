package com.inventory.controllers;

import com.inventory.mappers.ItemHelper;
import com.inventory.models.Item;
import com.inventory.models.Paging;
import com.inventory.services.GeneralMapper;
import com.inventory.services.item.ItemService;
import com.inventory.webmodels.requests.DeleteRequest;
import com.inventory.webmodels.requests.item.ItemRequest;
import com.inventory.webmodels.responses.BaseResponse;
import com.inventory.webmodels.responses.item.ItemResponse;
import com.inventory.webmodels.responses.item.ListOfItemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.inventory.webmodels.API_PATH.*;

@CrossOrigin
@RestController
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private GeneralMapper generalMapper;

    @Autowired
    private ItemHelper helper;

    @GetMapping(value = API_PATH_ITEMS, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ListOfItemResponse> ListOfItem(
            @RequestParam(required = false) String name,
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String sortedType
    ) throws IOException {
        Paging paging = helper.getPaging(pageNumber, pageSize, sortedBy, sortedType);
        if (name == null)
            name = "";
        ListOfItemResponse list = new ListOfItemResponse(itemService.getItemList(name, paging));
        BaseResponse<ListOfItemResponse> response = helper.getListBaseResponse(true, "", paging);
        response.setValue(list);
        return response;
    }

    @GetMapping(value = API_PATH_GET_ITEM, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ItemResponse> getItem(@PathVariable String id) throws IOException {
        Item item;
        BaseResponse<ItemResponse> response;
        try {
            item = itemService.getItem(id);
            response = helper.getBaseResponse(true, "");
            response.setValue(helper.getMappedItemResponse(item));
        } catch (RuntimeException e) {
            response = helper.getBaseResponse(false, e.getMessage());
            response.setValue(null);
        }
        return response;
    }

    @RequestMapping(value = API_PATH_ITEMS, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.POST, RequestMethod.PUT})
    public BaseResponse<ItemResponse> saveItem(@RequestBody ItemRequest request) {
        Item item = generalMapper.map(request, Item.class);
        BaseResponse<ItemResponse> response;
        try {
            item = itemService.saveItem(item);
            response = helper.getBaseResponse(true, "");
            response.setValue(helper.getMappedItemResponse(item));
        } catch (RuntimeException e) {
            response = helper.getBaseResponse(false, e.getMessage());
            response.setValue(null);
        }
        return response;
    }

    @PostMapping(value = API_PATH_UPLOAD_IMAGE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("itemId") String itemId
    ) {
        BaseResponse<String> response;
        try {
            String success = itemService.uploadFile(file, itemId);
            response = helper.getStandardBaseResponse(true, success);
        } catch (RuntimeException e) {
            response = helper.getStandardBaseResponse(false, e.getMessage());
        }
        return response;
    }

    @DeleteMapping(value = API_PATH_ITEMS, consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<String> deleteItem(@RequestBody DeleteRequest request) {
        BaseResponse<String> response;
        try {
            String success = itemService.deleteItem(request.getIds());
            response = helper.getBaseResponse(true, success);
        } catch (RuntimeException e) {
            response = helper.getBaseResponse(false, e.getMessage());
        }
        return response;
    }
}
