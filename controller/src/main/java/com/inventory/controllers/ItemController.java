package com.inventory.controllers;

import com.inventory.mappers.GeneralMapper;
import com.inventory.mappers.ModelHelper;
import com.inventory.models.Item;
import com.inventory.models.Paging;
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

import static com.inventory.constants.API_PATH.*;

@CrossOrigin
@RestController
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private GeneralMapper generalMapper;

    @Autowired
    private ModelHelper helper;

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
        BaseResponse<ListOfItemResponse> response = helper.getBaseResponse(true, "", paging);
        response.setValue(list);
        return response;
    }

    @GetMapping(value = API_PATH_GET_ITEM, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ItemResponse> getItem(@PathVariable String id) throws IOException {
        Item item;
        BaseResponse<ItemResponse> response;
        ItemResponse itemResponse;
        try {
            item = itemService.getItem(id);
            itemResponse = new ItemResponse(item);
            response = helper.getBaseResponse(true, "", new Paging());
        } catch (RuntimeException e) {
            itemResponse = new ItemResponse(null);
            response = helper.getBaseResponse(false, e.getMessage(), new Paging());
        }
        response.setValue(itemResponse);
        return response;
    }

    @RequestMapping(value = API_PATH_ITEMS, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.POST, RequestMethod.PUT})
    public BaseResponse<String> saveItem(@RequestBody ItemRequest request) {
        Item item = generalMapper.map(request, Item.class);
        try {
            itemService.saveItem(item);
            return helper.getStandardBaseResponse(true, "");
        } catch (RuntimeException e) {
            return helper.getStandardBaseResponse(false, e.getMessage());
        }
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
            response = helper.getBaseResponse(true, success, new Paging());
        } catch (RuntimeException e) {
            response = helper.getBaseResponse(false, e.getMessage(), new Paging());
        }
        return response;
    }
}
