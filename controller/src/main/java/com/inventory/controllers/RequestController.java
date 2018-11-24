package com.inventory.controllers;

import com.inventory.mappers.GeneralMapper;
import com.inventory.models.Employee;
import com.inventory.models.Item;
import com.inventory.models.Paging;
import com.inventory.models.Request;
import com.inventory.services.employee.EmployeeService;
import com.inventory.services.item.ItemService;
import com.inventory.services.request.RequestService;
import com.inventory.webmodels.requests.DeleteRequest;
import com.inventory.webmodels.requests.request.ChangeRequestStatusRequest;
import com.inventory.webmodels.requests.request.RequestHTTPRequest;
import com.inventory.webmodels.responses.BaseResponse;
import com.inventory.webmodels.responses.DeleteResponse;
import com.inventory.webmodels.responses.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.inventory.constants.API_PATH.*;
import static com.inventory.constants.ErrorConstant.NORMAL_ERROR;
import static com.inventory.constants.ErrorConstant.SAVE_ERROR;

@RestController
@CrossOrigin
public class RequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private GeneralMapper generalMapper;

    @GetMapping(value = API_PATH_REQUESTS, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ListOfRequestResponse> listOfRequest(
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String sortedType
    ) throws IOException {
        Paging paging = generalMapper.getPaging(pageNumber, pageSize, sortedBy, sortedType);
        List<Request> listOfRequest = requestService.getRequestList(paging);
        List<RequestResponse> listOfRequestResponse = new ArrayList<>();
        for (Request request : listOfRequest) {
            Employee employee = employeeService.getEmployee(request.getEmployeeId());
            Item item = itemService.getItem(request.getItemId());
            RequestResponse requestResponse = generalMapper.getMappedRequestResponse(request, employee, item);
            listOfRequestResponse.add(requestResponse);
        }
        ListOfRequestResponse list = new ListOfRequestResponse(listOfRequestResponse);
        BaseResponse<ListOfRequestResponse> response = generalMapper.getBaseResponse(true, "", paging);
        response.setValue(list);
        return response;
    }

    @GetMapping(value = API_PATH_EMPLOYEE_REQUESTS, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<List<EmployeeRequestResponse>> listOfEmployeeRequest(
            @RequestParam String employeeId,
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String sortedType
    ) throws IOException {
        Paging paging = generalMapper.getPaging(pageNumber, pageSize, sortedBy, sortedType);
        List<Request> listOfRequest = requestService.getEmployeeRequestList(employeeId, paging);
        List<EmployeeRequestResponse> listOfEmployeeRequest = new ArrayList<>();
        List<RequestResponse> list = new ArrayList<>();
        for (Request request : listOfRequest) {
            Item item = itemService.getItem(request.getItemId());
            EmployeeRequestResponse employeeRequestResponse = generalMapper.getMappedEmployeeRequestResponse(request, item);
            listOfEmployeeRequest.add(employeeRequestResponse);
        }
        BaseResponse<List<EmployeeRequestResponse>> response = generalMapper.getBaseResponse(
                true, "", paging);
        response.setValue(listOfEmployeeRequest);
        return response;
    }

    @GetMapping(value = API_PATH_GET_REQUEST_COUNT_BY_EMPLOYEE_ID_AND_STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<RequestCountResponse> getRequestCount(@RequestParam String id,
                                                              @RequestParam String status) throws IOException{
        RequestCountResponse requestCountResponse = new RequestCountResponse();
        requestCountResponse.setRequestCount(requestService.getRequestCountByEmployeeIdAndStatus(id, status));
        BaseResponse<RequestCountResponse> response = generalMapper.getBaseResponse(true, "", new Paging());
        response.setValue(requestCountResponse);
        return response;
    }

    @GetMapping(value = API_PATH_API_REQUEST_BY_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<RequestResponse> getRequest(@PathVariable String id) throws IOException {
        RequestResponse requestResponse = new RequestResponse();
        requestResponse.setRequest(requestService.getRequest(id));
        BaseResponse<RequestResponse> response = generalMapper.getBaseResponse(true, "", new Paging());
        response.setValue(requestResponse);
        return response;
    }

    @RequestMapping(value = API_PATH_REQUESTS, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.POST, RequestMethod.PUT})
    public BaseResponse<String> insertRequest(@RequestBody RequestHTTPRequest requestBody) {
        Request rb = generalMapper.getMappedRequest(requestBody);
        Request request;
        Item item = itemService.changeItemQty(rb);
        if (item == null)
            request = null;
        else {
            request = requestService.saveRequest(rb);
        }

        if (request == null || item == null) {
            return generalMapper.getStandardBaseResponse(false, SAVE_ERROR);
        } else {
            return generalMapper.getStandardBaseResponse(true, "");
        }
    }

    @PutMapping(value = API_PATH_CHANGE_STATUS_REQUEST, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public BaseResponse<ChangeRequestStatusResponse> changeStatus(@RequestBody ChangeRequestStatusRequest requestBody) {
        BaseResponse<ChangeRequestStatusResponse> response;
        Request request = generalMapper.getMappedRequest(requestBody);
        ChangeRequestStatusResponse changeRequestStatusResponse = new ChangeRequestStatusResponse();
        Map<String, Integer> listOfRecoveredItems = new HashMap<>();
        List<String> errorOfItem = new ArrayList<>();
        List<String> errors = requestService.changeStatusRequests(requestBody.getIds(),
                request.getStatus(), request.getNotes());
        if (request.getStatus().equals("Rejected")) {
            listOfRecoveredItems = requestService.getRecoveredItems(requestBody.getIds());
            errorOfItem = itemService.recoverItemQty(listOfRecoveredItems);
        }
        if (errors.size() <= 0 && errorOfItem.size() <= 0) {
            response = generalMapper.getBaseResponse(true, "", new Paging());
        } else {
            response = generalMapper.getBaseResponse(false, NORMAL_ERROR, new Paging());
            if (errors.size() > 0)
                changeRequestStatusResponse.setErrors(errors);
            else if (errors.size() > 0 && errorOfItem.size() > 0) {
                changeRequestStatusResponse.setErrorOfItem(errorOfItem);
                changeRequestStatusResponse.setErrors(errors);
            } else
                changeRequestStatusResponse.setErrorOfItem(errorOfItem);
        }
        response.setValue(changeRequestStatusResponse);
        return response;
    }

    @DeleteMapping(value = API_PATH_REQUESTS, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public BaseResponse<DeleteResponse> deleteRequest(@RequestBody DeleteRequest request) {
        DeleteResponse deleteResponse = null;
        BaseResponse<DeleteResponse> response = null;
        Map<String, Integer> listOfRecoveredItems = requestService.getRecoveredItems(request.getIds());
        List<String> error = requestService.deleteRequests(request.getIds());
        List<String> errorOfItem = itemService.recoverItemQty(listOfRecoveredItems);
        if (error.size() <= 0 && errorOfItem.size() <= 0) {
            response = generalMapper.getBaseResponse(true, "", new Paging());
        } else {
            response = generalMapper.getBaseResponse(false, NORMAL_ERROR, new Paging());
            if(error.size() > 0)
                deleteResponse.setError(error);
            else
                deleteResponse.setError(errorOfItem);
            response.setValue(deleteResponse);
        }
        return response;
    }
}
