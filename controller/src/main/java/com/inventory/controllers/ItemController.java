package com.inventory.controllers;

import com.inventory.models.Item;
import com.inventory.models.Paging;
import com.inventory.services.ItemService;
import com.inventory.webmodels.requests.DeleteRequest;
import com.inventory.webmodels.requests.ItemRequest;
import com.inventory.webmodels.responses.BaseResponse;
import com.inventory.webmodels.responses.DeleteResponse;
import com.inventory.webmodels.responses.ItemResponse;
import com.inventory.webmodels.responses.ListOfItemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
    public BaseResponse<ListOfItemResponse> listOfItem() throws IOException{
        Paging paging = new Paging();
        ListOfItemResponse list = new ListOfItemResponse(itemService.getItemList(paging));
        BaseResponse<ListOfItemResponse> response = new BaseResponse<>();
        response.setCode(HttpStatus.OK.toString());
        response.setValue(list);
        response.setErrorMessage("");
        response.setSuccess(true);
        response.setPaging(paging);
        return response;
    }

    @GetMapping(value = API_PATH_GET_ITEM, consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ItemResponse> ItemData(@PathVariable String id) throws IOException{
        ItemResponse itemResponse = new ItemResponse(itemService.getItem(id));
        BaseResponse<ItemResponse> response = mapper.getBaseResponse(true, "");
        response.setValue(itemResponse);
        return response;
    }

    @RequestMapping(value = API_PATH_ITEMS, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.POST, RequestMethod.PUT})
    public BaseResponse<String> insertItem(@RequestBody ItemRequest request){
        Item item = mapper.mapItem(request);

        if (itemService.saveItem(item) == null)
            return mapper.getStandardBaseResponse(false, "save failed");
        return mapper.getStandardBaseResponse(true, "success");

    }

    @DeleteMapping(value = API_PATH_ITEMS, consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<DeleteResponse> deleteItem(@RequestBody DeleteRequest request){
        DeleteResponse deleteResponse = null;
        BaseResponse<DeleteResponse> response = null;

        List<String> error = itemService.deleteItem(request.getIds());

        if(error.size() <= 0){
            response = mapper.getBaseResponse(true, "");
        }else {
            response = mapper.getBaseResponse(false, "There is an error");
            deleteResponse.setValue(error);
            response.setValue(deleteResponse);
        }

        return response;
    }

}
