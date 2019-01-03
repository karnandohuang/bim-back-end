package com.inventory.services.helper;

import com.inventory.models.Paging;
import org.springframework.stereotype.Component;

@Component
public class PagingHelper {
    public void setPagingTotalRecordsAndTotalPage(Paging paging, float totalRecords) {
        paging.setTotalRecords((int) totalRecords);
        double totalPage = (int) Math.ceil((totalRecords / paging.getPageSize()));
        paging.setTotalPage((int) totalPage);
    }
}
