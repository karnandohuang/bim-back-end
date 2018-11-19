package com.inventory.mappers;

import com.inventory.models.Employee;
import com.inventory.models.Item;
import com.inventory.models.Paging;
import com.inventory.models.Request;
import com.inventory.webmodels.requests.employee.EmployeeRequest;
import com.inventory.webmodels.requests.item.ItemRequest;
import com.inventory.webmodels.requests.request.ChangeRequestStatusRequest;
import com.inventory.webmodels.requests.request.RequestHTTPRequest;
import com.inventory.webmodels.responses.BaseResponse;
import com.inventory.webmodels.responses.item.UploadFileResponse;
import com.inventory.webmodels.responses.request.EmployeeRequestResponse;
import com.inventory.webmodels.responses.request.RequestResponse;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class GeneralMapper {

    MapperFactory factory = new DefaultMapperFactory.Builder().build();

    protected void configure() {
        factory.classMap(EmployeeRequest.class, Employee.class)
                .byDefault()
                .mapNulls(true)
                .register();
        factory.classMap(ItemRequest.class, Item.class)
                .byDefault()
                .mapNulls(true)
                .register();
        factory.classMap(RequestHTTPRequest.class, Request.class)
                .byDefault()
                .mapNulls(true)
                .register();
        factory.classMap(ChangeRequestStatusRequest.class, Request.class)
                .byDefault()
                .mapNulls(true)
                .register();
    }

    public Employee getMappedEmployee(EmployeeRequest request) {
        return factory.getMapperFacade().map(request, Employee.class);
    }

    public Item getMappedItem(ItemRequest request) {
        return factory.getMapperFacade().map(request, Item.class);
    }

    public Request getMappedRequest(RequestHTTPRequest request) {
        Request requestObj = factory.getMapperFacade().map(request, Request.class);
        requestObj.setNotes("");
        requestObj.setStatus("Pending");
        return requestObj;
    }

    public Request getMappedRequest(ChangeRequestStatusRequest request) {
        Request requestObj = factory.getMapperFacade().map(request, Request.class);
        if (requestObj.getNotes() == null)
            requestObj.setNotes("");
        return requestObj;
    }

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
            paging.setSortedType("asc");
        return paging;
    }

    public RequestResponse getMappedRequestResponse(Request request, Employee employee, Item item) {
        RequestResponse requestResponse = new RequestResponse();
        requestResponse.setRequest(request);
        requestResponse.setEmployeeName(employee.getName());
        requestResponse.setItemSKU(item.getSku());
        requestResponse.setItemName(item.getName());
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
