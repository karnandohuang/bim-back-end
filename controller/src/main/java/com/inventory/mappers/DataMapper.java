//package com.inventory.mappers;
//
//import com.inventory.models.Employee;
//import com.inventory.models.Item;
//import com.inventory.models.Paging;
//import com.inventory.models.Request;
//import com.inventory.webmodels.requests.ChangeRequestStatusRequest;
//import com.inventory.webmodels.requests.EmployeeRequest;
//import com.inventory.webmodels.requests.ItemRequest;
//import com.inventory.webmodels.requests.RequestHTTPRequest;
//import com.inventory.webmodels.responses.BaseResponse;
//import com.inventory.webmodels.responses.UploadFileResponse;
//import ma.glasnost.orika.MapperFactory;
//import ma.glasnost.orika.impl.DefaultMapperFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DataMapper {
//
//    MapperFactory mapper = new DefaultMapperFactory.Builder().build();
//
//    public Item mapItem(ItemRequest request){
//        Item item = new Item();
//        item.setId(request.getId());
//        item.setName(request.getName());
//        item.setSku(request.getSku());
//        item.setPrice(request.getPrice());
//        item.setLocation(request.getLocation());
//        item.setQty(request.getQty());
//        item.setImageUrl(request.getImageUrl());
//        return item;
//    }
//
//    public Request mapRequest(RequestHTTPRequest requestBody){
//        Request request = new Request();
//        request.setId(requestBody.getId());
//        request.setEmployeeId(requestBody.getEmployeeId());
//        request.setItemId(requestBody.getItemId());
//        request.setQty(requestBody.getQty());
//        request.setNotes("");
//        request.setStatus("Pending");
//        return request;
//    }
//
//    public Request mapRequest(ChangeRequestStatusRequest requestBody) {
//        Request request = new Request();
//        request.setId(requestBody.getId());
//        if (requestBody.getNotes() == null)
//            request.setNotes("");
//        else
//            request.setNotes(requestBody.getNotes());
//        request.setStatus(requestBody.getStatus());
//        return request;
//    }
//
//    public BaseResponse<String> getStandardBaseResponse(boolean success, String errorMessage){
//        BaseResponse<String> response = new BaseResponse<>();
//        response.setCode(HttpStatus.OK.toString());
//        response.setSuccess(success);
//        response.setValue("");
//        response.setErrorMessage(errorMessage);
//        return response;
//    }
//
//    public BaseResponse<UploadFileResponse> getUploadBaseResponse(boolean success, String errorMessage) {
//        BaseResponse<UploadFileResponse> response = new BaseResponse<>();
//        response.setCode(HttpStatus.OK.toString());
//        response.setSuccess(success);
//        response.setErrorMessage(errorMessage);
//        return response;
//    }
//
//    public BaseResponse getBaseResponse(boolean success, String errorMessage, Paging paging){
//        BaseResponse response = new BaseResponse<>();
//        response.setCode(HttpStatus.OK.toString());
//        response.setSuccess(success);
//        response.setErrorMessage(errorMessage);
//        response.setPaging(paging);
//        return response;
//    }
//
//    public Paging getPaging(int pageNumber, int pageSize, String sortedBy, String sortedType) {
//        Paging paging = new Paging();
//        paging.setPageNumber(pageNumber);
//        paging.setPageSize(pageSize);
//        if (sortedBy != null)
//            paging.setSortedBy(sortedBy);
//        else
//            paging.setSortedBy("updatedDate");
//        if (sortedType != null)
//            paging.setSortedType(sortedType);
//        else
//            paging.setSortedType("asc");
//        return paging;
//    }
//}
