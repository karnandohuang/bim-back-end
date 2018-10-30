package com.inventory.controllers;

import com.inventory.models.Employee;
import com.inventory.models.Item;
import com.inventory.webmodels.requests.EmployeeRequest;
import com.inventory.webmodels.requests.ItemRequest;
import com.inventory.webmodels.responses.BaseResponse;
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

    public BaseResponse<String> getStandardBaseResponse(boolean success, String errorMessage){
        BaseResponse<String> response = new BaseResponse<>();
        response.setCode(HttpStatus.OK.toString());
        response.setSuccess(success);
        response.setValue("");
        response.setErrorMessage(errorMessage);
        return response;
    }

    public BaseResponse getBaseResponse(boolean success, String errorMessage){
        BaseResponse response = new BaseResponse<>();
        response.setCode(HttpStatus.OK.toString());
        response.setSuccess(success);
        response.setErrorMessage(errorMessage);
        return response;
    }
}
