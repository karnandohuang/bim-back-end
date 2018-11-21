package com.inventory.models;

import lombok.Data;

@Data
public class Paging {
    private int pageSize;
    private int pageNumber;
    private int totalRecords;
    private String sortedBy;
    private String sortedType;
    private int totalPage;
}
