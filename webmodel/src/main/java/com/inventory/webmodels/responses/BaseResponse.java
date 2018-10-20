package com.inventory.webmodels.responses;

import com.inventory.models.Employee;
import com.inventory.models.Paging;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BaseResponse<T> {
    private String success;
    private String code;
    private T value;
    private Paging paging;

}