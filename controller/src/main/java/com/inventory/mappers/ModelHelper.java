package com.inventory.mappers;

import com.inventory.models.Item;
import com.inventory.models.Paging;
import com.inventory.models.Request;
import com.inventory.webmodels.responses.BaseResponse;
import com.inventory.webmodels.responses.item.UploadFileResponse;
import com.inventory.webmodels.responses.request.EmployeeRequestResponse;
import com.inventory.webmodels.responses.request.RequestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


@Component
public class ModelHelper {
    public BaseResponse<String> getStandardBaseResponse(boolean success, String errorMessage) {
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

    public BaseResponse getBaseResponse(boolean success, String errorMessage, Paging paging) {
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
            paging.setSortedType("desc");
        return paging;
    }

    public RequestResponse getMappedRequestResponse(Request request) {
        RequestResponse requestResponse = new RequestResponse();
        requestResponse.setRequest(request);
        return requestResponse;
    }

    public EmployeeRequestResponse getMappedEmployeeRequestResponse(Request request, Item item) {
        EmployeeRequestResponse employeeRequestResponse = new EmployeeRequestResponse();
        item.setQty(request.getQty());
        employeeRequestResponse.setItem(item);
        employeeRequestResponse.setStatus(request.getStatus());
        employeeRequestResponse.setRequestId(request.getId());
        return employeeRequestResponse;
    }
}
