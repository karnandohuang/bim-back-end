package com.inventory.webmodels.responses;

import com.inventory.models.Paging;
import lombok.Data;

@Data
public class BaseResponse<T> {
    private Boolean success;
    private String code;
    private T value;
    private Paging paging;
    private String errorMessage;

}