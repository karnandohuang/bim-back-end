package com.inventory.mappers;

import com.inventory.models.Paging;
import com.inventory.webmodels.responses.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


@Component
public abstract class ModelHelper {
    public BaseResponse<String> getStandardBaseResponse(boolean success, String errorMessage) {
        BaseResponse<String> response = new BaseResponse<>();
        response.setCode(HttpStatus.OK.toString());
        response.setSuccess(success);
        response.setValue("");
        response.setErrorMessage(errorMessage);
        return response;
    }

    public BaseResponse getListBaseResponse(boolean success, String errorMessage, Paging paging) {
        BaseResponse response = new BaseResponse<>();
        response.setCode(HttpStatus.OK.toString());
        response.setSuccess(success);
        response.setErrorMessage(errorMessage);
        response.setPaging(paging);
        return response;
    }

    public BaseResponse getBaseResponse(boolean success, String errorMessage) {
        BaseResponse response = new BaseResponse<>();
        response.setCode(HttpStatus.OK.toString());
        response.setSuccess(success);
        response.setErrorMessage(errorMessage);
        response.setPaging(null);
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

    public Paging getEmptyPaging() {
        Paging paging = new Paging();
        paging.setTotalRecords(0);
        paging.setTotalPage(0);
        paging.setSortedBy("updatedDate");
        paging.setSortedType("desc");
        paging.setPageNumber(0);
        paging.setPageSize(0);
        return paging;
    }

}
