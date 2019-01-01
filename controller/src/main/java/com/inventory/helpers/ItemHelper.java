package com.inventory.helpers;

import com.inventory.models.entity.Item;
import com.inventory.webmodels.responses.BaseResponse;
import com.inventory.webmodels.responses.item.ItemResponse;
import com.inventory.webmodels.responses.item.UploadFileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ItemHelper extends ModelHelper {

    public ItemResponse getMappedItemResponse(Item item) {
        return new ItemResponse(item);
    }

    public BaseResponse<UploadFileResponse> getUploadBaseResponse(boolean success, String errorMessage) {
        BaseResponse<UploadFileResponse> response = new BaseResponse<>();
        response.setCode(HttpStatus.OK.toString());
        response.setSuccess(success);
        response.setErrorMessage(errorMessage);
        return response;
    }
}
