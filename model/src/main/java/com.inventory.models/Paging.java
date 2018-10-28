package com.inventory.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Paging {
    private long pageSize;
    private long pageNumber;
    private long totalRecords;
}
