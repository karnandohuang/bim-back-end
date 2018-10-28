package com.inventory.controllers;

import com.inventory.models.Employee;
import com.inventory.models.Request;
import com.inventory.webmodels.requests.EmployeeRequest;
import com.inventory.webmodels.requests.RequestHTTPRequest;
import com.inventory.webmodels.responses.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class DataMapper {

    public Employee mapEmployee(EmployeeRequest request) {
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

    public Request mapRequest(RequestHTTPRequest requestBody) {
        Request request = new Request();
        request.setId(requestBody.getId());
        request.setEmployeeId(requestBody.getEmployeeId());
        request.setItemId(requestBody.getItemId());
        request.setQty(requestBody.getQty());
        request.setStatus(requestBody.getStatus());
        request.setNotes(requestBody.getNotes());
        return request;
    }


    public BaseResponse<String> getStandardBaseResponse(boolean success, String errorMessage) {
        BaseResponse<String> response = new BaseResponse<>();
        response.setCode(HttpStatus.OK.toString());
        response.setSuccess(success);
        response.setValue("");
        response.setErrorMessage(errorMessage);
        return response;
    }

    public BaseResponse getBaseResponse(boolean success, String errorMessage) {
        BaseResponse response = new BaseResponse<>();
        response.setCode(HttpStatus.OK.toString());
        response.setSuccess(success);
        response.setErrorMessage(errorMessage);
        return response;
    }
}
