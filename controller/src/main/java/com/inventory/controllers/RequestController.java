package com.inventory.controllers;

import com.inventory.mappers.RequestMapper;
import com.inventory.mappers.ResponseMapper;
import com.inventory.models.Item;
import com.inventory.models.Paging;
import com.inventory.models.Request;
import com.inventory.services.EmployeeService;
import com.inventory.services.ItemService;
import com.inventory.services.RequestService;
import com.inventory.webmodels.requests.ChangeRequestStatusRequest;
import com.inventory.webmodels.requests.DeleteRequest;
import com.inventory.webmodels.requests.RequestHTTPRequest;
import com.inventory.webmodels.responses.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.inventory.controllers.API_PATH.*;

@RestController
@CrossOrigin
public class RequestController {

    @Autowired
    RequestService requestService;

    @Autowired
    ItemService itemService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    ResponseMapper responseMapper;

    @Autowired
    RequestMapper requestMapper;

    @GetMapping(value = API_PATH_REQUESTS, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ListOfRequestResponse> listOfRequest(
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String sortedType
    ) throws IOException {
        Paging paging = responseMapper.getPaging(pageNumber, pageSize, sortedBy, sortedType);
        List<Request> listOfRequest = requestService.getRequestList(paging);
        List<RequestResponse> listOfRequestResponse = new ArrayList<>();
        for (Request request : listOfRequest) {
            RequestResponse requestResponse = new RequestResponse();
            requestResponse.setRequest(request);
            requestResponse.setEmployeeName(employeeService.getEmployee(request.getEmployeeId()).getName());
            requestResponse.setItemSKU(itemService.getItem(request.getItemId()).getSku());
            requestResponse.setItemName(itemService.getItem(request.getItemId()).getName());
            listOfRequestResponse.add(requestResponse);
        }
        ListOfRequestResponse list = new ListOfRequestResponse(listOfRequestResponse);
        BaseResponse<ListOfRequestResponse> response = responseMapper.getBaseResponse(true, "", paging);
        response.setValue(list);
        return response;
    }

    @GetMapping(value = API_PATH_EMPLOYEE_REQUESTS, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ListOfItemResponse> listOfEmployeeRequest(
            @RequestParam String employeeId,
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String sortedType
    ) throws IOException {
        Paging paging = responseMapper.getPaging(pageNumber, pageSize, sortedBy, sortedType);
        List<Request> listOfRequest = requestService.getEmployeeRequestList(employeeId, paging);
        List<Item> listOfItem = new ArrayList<>();
        for (Request request : listOfRequest) {
            Item item = itemService.getItem(request.getItemId());
            listOfItem.add(item);
        }
        BaseResponse<ListOfItemResponse> response = responseMapper.getBaseResponse(true, "", paging);
        ListOfItemResponse list = new ListOfItemResponse(listOfItem);
        response.setValue(list);
        return response;
    }

    @GetMapping(value = API_PATH_GET_REQUEST, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<RequestResponse> getRequest(@PathVariable String id) throws IOException {
        RequestResponse requestResponse = new RequestResponse();
        requestResponse.setRequest(requestService.getRequest(id));
        BaseResponse<RequestResponse> response = responseMapper.getBaseResponse(true, "", new Paging());
        response.setValue(requestResponse);
        return response;
    }

    @RequestMapping(value = API_PATH_REQUESTS, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.POST, RequestMethod.PUT})
    public BaseResponse<String> insertRequest(@RequestBody RequestHTTPRequest requestBody) {
        Request rb = requestMapper.getMappedRequest(requestBody);
        Request request;
        Item item = itemService.getItem(rb.getItemId());
        int qty = item.getQty()-rb.getQty();
        if(qty < 0)
            request = null;
        else {
            item.setQty(qty);
            item = itemService.saveItem(item);
            request = requestService.saveRequest(rb);
        }

        if (request == null || item == null) {
            return responseMapper.getStandardBaseResponse(false, "save failed");
        } else {
            return responseMapper.getStandardBaseResponse(true, "");
        }
    }

    @PutMapping(value = API_PATH_CHANGE_STATUS_REQUEST, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<String> changeStatus(@RequestBody ChangeRequestStatusRequest requestBody) {
        Request rb = requestMapper.getMappedRequest(requestBody);
        Request request = requestService.getRequest(rb.getId());
        request.setStatus(rb.getStatus());
        request = requestService.saveRequest(request);
        if (request == null) {
            return responseMapper.getStandardBaseResponse(false, "save failed");
        } else {
            return responseMapper.getStandardBaseResponse(true, "");
        }
    }

    @DeleteMapping(value = API_PATH_REQUESTS, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public BaseResponse<DeleteResponse> deleteRequest(@RequestBody DeleteRequest request) {
        DeleteResponse deleteResponse = null;
        BaseResponse<DeleteResponse> response = null;
        Map<String, Integer> listOfRecoveredItems = requestService.getRecoveredItems(request.getIds());
        List<String> error = requestService.deleteRequests(request.getIds());
        List<String> errorOfItem = new ArrayList<>();
        Iterator i = listOfRecoveredItems.entrySet().iterator();
        while(i.hasNext()){
            Map.Entry data = (Map.Entry)i.next();
            Item item = itemService.getItem((String)data.getKey());
            item.setQty(item.getQty() + (Integer)data.getValue());
            item = itemService.saveItem(item);
            if(item == null){
                errorOfItem.add("Failed saving item");
            }
        }
        if (error.size() <= 0 && errorOfItem.size() <= 0) {
            response = responseMapper.getBaseResponse(true, "", new Paging());
        } else {
            response = responseMapper.getBaseResponse(false, "There is an error", new Paging());
            if(error.size() > 0)
                deleteResponse.setValue(error);
            else
                deleteResponse.setValue(errorOfItem);
            response.setValue(deleteResponse);
        }
        return response;
    }
}
