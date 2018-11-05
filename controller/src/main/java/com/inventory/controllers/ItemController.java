package com.inventory.controllers;

import com.inventory.models.Item;
import com.inventory.models.Paging;
import com.inventory.services.ItemService;
import com.inventory.webmodels.requests.DeleteRequest;
import com.inventory.webmodels.requests.ItemRequest;
import com.inventory.webmodels.responses.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.inventory.controllers.API_PATH.*;

@CrossOrigin
@RestController
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    DataMapper mapper;

    @GetMapping(value = API_PATH_ITEMS, produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ListOfItemResponse> getItemList(
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String sortedType
    ) throws IOException {
        Paging paging = mapper.getPaging(pageNumber, pageSize, sortedBy, sortedType);
        ListOfItemResponse list = new ListOfItemResponse(itemService.getItemList(paging));
        BaseResponse<ListOfItemResponse> response = mapper.getBaseResponse(true, "", paging);
        response.setValue(list);
        return response;
    }

    @GetMapping(value = API_PATH_GET_ITEM, consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ItemResponse> ItemData(@PathVariable String id) throws IOException{
        ItemResponse itemResponse = new ItemResponse(itemService.getItem(id));
        BaseResponse<ItemResponse> response = mapper.getBaseResponse(true, "", new Paging());
        response.setValue(itemResponse);
        return response;
    }

    @RequestMapping(value = API_PATH_ITEMS, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.POST, RequestMethod.PUT})
    public BaseResponse<String> insertItem(@RequestBody ItemRequest request) {
        Item item = mapper.mapItem(request);
        if (itemService.saveItem(item) == null)
            return mapper.getStandardBaseResponse(false, "save failed");
        return mapper.getStandardBaseResponse(true, "success");
    }

    @PostMapping(value = API_PATH_UPLOAD_IMAGE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<UploadFileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        String imagePath = itemService.uploadFile(file);
        UploadFileResponse value = new UploadFileResponse(imagePath);
        BaseResponse<UploadFileResponse> response = null;
        if (imagePath == null)
            response = mapper.getUploadBaseResponse(false, "save failed");
        else
            response = mapper.getUploadBaseResponse(true, "success");
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
            response = mapper.getBaseResponse(true, "", new Paging());
        }else {
            response = mapper.getBaseResponse(false, "There is an error", new Paging());
            deleteResponse.setValue(error);
            response.setValue(deleteResponse);
        }

        return response;
    }

}
