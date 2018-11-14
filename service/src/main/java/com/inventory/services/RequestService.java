package com.inventory.services;

import com.inventory.models.Paging;
import com.inventory.models.Request;

import java.util.List;
import java.util.Map;

public interface RequestService {

    Request getRequest(String id);

    List<Request> getRequestList(Paging paging);

    List<Request> getEmployeeRequestList(String employeeId, Paging paging);

    Request saveRequest(Request request);

    List<String> deleteRequests(List<String> ids);

    List<String> changeStatusRequests(List<String> ids, String status, String notes);

    Map<String, Integer> getRecoveredItems(List<String> ids);
}
