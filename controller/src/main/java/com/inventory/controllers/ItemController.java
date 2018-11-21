package com.inventory.controllers;

import com.inventory.mappers.GeneralMapper;
import com.inventory.models.Item;
import com.inventory.models.Paging;
import com.inventory.services.item.ItemService;
import com.inventory.webmodels.requests.item.ItemRequest;
import com.inventory.webmodels.requests.request.DeleteRequest;
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
import java.util.List;

import static com.inventory.constants.API_PATH.*;
import static com.inventory.constants.ErrorConstant.NORMAL_ERROR;
import static com.inventory.constants.ErrorConstant.SAVE_ERROR;

@CrossOrigin
@RestController
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private GeneralMapper generalMapper;

    @GetMapping(value = API_PATH_ITEMS, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ListOfItemResponse> getItemList(
            @RequestParam(required = false) String name,
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String sortedType
    ) throws IOException {
        Paging paging = generalMapper.getPaging(pageNumber, pageSize, sortedBy, sortedType);
        if (name == null)
            name = "";
        ListOfItemResponse list = new ListOfItemResponse(itemService.getItemList(name, paging));
        BaseResponse<ListOfItemResponse> response = generalMapper.getBaseResponse(true, "", paging);
        response.setValue(list);
        return response;
    }

    @GetMapping(value = API_PATH_GET_ITEM, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ItemResponse> ItemData(@PathVariable String id) throws IOException{
        ItemResponse itemResponse = new ItemResponse(itemService.getItem(id));
        BaseResponse<ItemResponse> response = generalMapper.getBaseResponse(true, "", new Paging());
        response.setValue(itemResponse);
        return response;
    }

    @RequestMapping(value = API_PATH_ITEMS, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.POST, RequestMethod.PUT})
    public BaseResponse<String> insertItem(@RequestBody ItemRequest request) {
        Item item = generalMapper.getMappedItem(request);
        if (itemService.saveItem(item) == null)
            return generalMapper.getStandardBaseResponse(false, SAVE_ERROR);
        return generalMapper.getStandardBaseResponse(true, "");
    }

    @PostMapping(value = API_PATH_UPLOAD_IMAGE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<UploadFileResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("sku") String itemSku
    ) {
        String imagePath = itemService.uploadFile(file, itemSku);
        UploadFileResponse value = new UploadFileResponse(imagePath);
        BaseResponse<UploadFileResponse> response = null;
        if (imagePath == null)
            response = generalMapper.getUploadBaseResponse(false, SAVE_ERROR);
        else
            response = generalMapper.getUploadBaseResponse(true, "");
        response.setValue(value);
        return response;
    }

    @DeleteMapping(value = API_PATH_ITEMS, consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<DeleteResponse> deleteItem(@RequestBody DeleteRequest request){
        DeleteResponse deleteResponse = null;
        BaseResponse<DeleteResponse> response = null;

        List<String> error = itemService.deleteItem(request.getIds());

        if(error.size() <= 0){
            response = generalMapper.getBaseResponse(true, "", new Paging());
        }else {
            response = generalMapper.getBaseResponse(false, NORMAL_ERROR, new Paging());
            deleteResponse.setValue(error);
            response.setValue(deleteResponse);
        }

        return response;
    }

}
