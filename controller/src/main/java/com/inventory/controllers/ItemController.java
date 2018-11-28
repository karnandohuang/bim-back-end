package com.inventory.controllers;

import com.inventory.mappers.GeneralMapper;
import com.inventory.mappers.ModelHelper;
import com.inventory.models.Item;
import com.inventory.models.Paging;
import com.inventory.services.item.ItemService;
import com.inventory.webmodels.requests.DeleteRequest;
import com.inventory.webmodels.requests.item.ItemRequest;
import com.inventory.webmodels.responses.BaseResponse;
import com.inventory.webmodels.responses.DeleteResponse;
import com.inventory.webmodels.responses.item.ItemResponse;
import com.inventory.webmodels.responses.item.ListOfItemResponse;
import com.inventory.webmodels.responses.item.UploadFileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        Item item = generalMapper.getMappedItem(request);
        try {
            itemService.saveItem(item);
            return helper.getStandardBaseResponse(true, "");
        } catch (RuntimeException e) {
            return helper.getStandardBaseResponse(false, e.getMessage());
        }
    }

    @PostMapping(value = API_PATH_UPLOAD_IMAGE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<UploadFileResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("sku") String itemSku
    ) {
        String imagePath;
        BaseResponse<UploadFileResponse> response;
        try {
            imagePath = itemService.uploadFile(file, itemSku);
            response = helper.getUploadBaseResponse(true, "");
        } catch (RuntimeException e) {
            imagePath = null;
            response = helper.getUploadBaseResponse(false, e.getMessage());
        }
        UploadFileResponse value = new UploadFileResponse(imagePath);
        response.setValue(value);
        return response;
    }

    @DeleteMapping(value = API_PATH_ITEMS, consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<DeleteResponse> deleteItem(@RequestBody DeleteRequest request){
        DeleteResponse deleteResponse = new DeleteResponse();
        BaseResponse<DeleteResponse> response;
        List<String> error = new ArrayList<>();
        try {
            error = itemService.deleteItem(request.getIds());
            response = helper.getBaseResponse(true, "", new Paging());
        } catch (RuntimeException e) {
            response = helper.getBaseResponse(false, e.getMessage(), new Paging());
        }
        if (error.size() > 0) {
            deleteResponse.setError(error);
            response.setValue(deleteResponse);
        }
        return response;
    }
}
