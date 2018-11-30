package com.inventory.services.assignment;

import com.inventory.models.Assignment;
import com.inventory.models.Paging;

import java.util.List;
import java.util.Map;

public interface AssignmentService {

    Assignment getAssignment(String id);

    List<Assignment> getAssignmentList(Paging paging);

    List<Assignment> getEmployeeAssignmentList(String employeeId, Paging paging);

    Double getAssignmentCountByEmployeeIdAndStatus(String employeeId, String status);

    Double getAssignmentCountByItemIdAndStatus(String itemId, String status);

    Assignment saveAssignment(Assignment Assignment);

    List<String> deleteAssignments(List<String> ids);

    List<String> changeStatusAssignments(List<String> ids, String status, String notes);

    Map<String, Integer> getRecoveredItems(List<String> ids);
}
