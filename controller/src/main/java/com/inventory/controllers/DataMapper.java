package com.inventory.controllers;

import com.inventory.models.Employee;
import com.inventory.models.Item;
import com.inventory.models.Paging;
import com.inventory.models.Request;
import com.inventory.webmodels.requests.EmployeeRequest;
import com.inventory.webmodels.requests.ItemRequest;
import com.inventory.webmodels.requests.RequestHTTPRequest;
import com.inventory.webmodels.responses.BaseResponse;
import com.inventory.webmodels.responses.UploadFileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class DataMapper {

    public Employee mapEmployee(EmployeeRequest request){
        Employee employee = new Employee();
        employee.setId(request.getId());
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setPassword(request.getPassword());
        employee.setDob(request.getDob());
        employee.setPosition(request.getPosition());
        employee.setDivision(request.getDivision());
        employee.setSuperiorId(request.getSuperiorId());
        return employee;
    }

    public Item mapItem(ItemRequest request){
        Item item = new Item();
        item.setId(request.getId());
        item.setName(request.getName());
        item.setSku(request.getSku());
        item.setPrice(request.getPrice());
        item.setLocation(request.getLocation());
        item.setQty(request.getQty());
        item.setImageUrl(request.getImageUrl());
        return item;
    }

    public Request mapRequest(RequestHTTPRequest requestBody){
        Request request = new Request();
        request.setId(requestBody.getId());
        request.setEmployeeId(requestBody.getEmployeeId());
        request.setItemId(requestBody.getItemId());
        request.setQty(requestBody.getQty());
        request.setNotes("");
        request.setStatus("Pending");
        return request;
    }

    public BaseResponse<String> getStandardBaseResponse(boolean success, String errorMessage){
        BaseResponse<String> response = new BaseResponse<>();
        response.setCode(HttpStatus.OK.toString());
        response.setSuccess(success);
        response.setValue("");
        response.setErrorMessage(errorMessage);
        return response;
    }

    public BaseResponse<UploadFileResponse> getUploadBaseResponse(boolean success, String errorMessage) {
        BaseResponse<UploadFileResponse> response = new BaseResponse<>();
        response.setCode(HttpStatus.OK.toString());
        response.setSuccess(success);
        response.setErrorMessage(errorMessage);
        return response;
    }

    public BaseResponse getBaseResponse(boolean success, String errorMessage, Paging paging){
        BaseResponse response = new BaseResponse<>();
        response.setCode(HttpStatus.OK.toString());
        response.setSuccess(success);
        response.setErrorMessage(errorMessage);
        response.setPaging(paging);
        return response;
    }

    public Paging getPaging(int pageNumber, int pageSize, String sortedBy, String sortedType) {
        Paging paging = new Paging();
        paging.setPageNumber(pageNumber);
        paging.setPageSize(pageSize);
        if (sortedBy != null)
            paging.setSortedBy(sortedBy);
        else
            paging.setSortedBy("updatedDate");
        if (sortedType != null)
            paging.setSortedType(sortedType);
        else
            paging.setSortedType("asc");
        return paging;
    }
}
