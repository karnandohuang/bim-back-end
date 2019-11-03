package com.inventory.services.assignment;

import com.inventory.models.Paging;
import com.inventory.models.entity.Assignment;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface AssignmentService {

    Assignment getAssignment(String id);

    List<Assignment> getAssignmentList(String filterStatus, Paging paging);

    List<Assignment> getEmployeeAssignmentList(String employeeId, String filterStatus, Paging paging);

    @Transactional
    List<Assignment> getEmployeeSuperiorAssignmentList(String superiorId, String filterStatus, Paging paging);

    Map<String, Double> getAssignmentCountByEmployeeId(String employeeId);

    Double getAssignmentCountByItemIdAndStatus(String itemId, String status);

    Assignment saveAssignment(Assignment Assignment);

    String changeStatusAssignments(List<String> ids, String status, String notes, String employeeId);

    Map<String, Integer> getRecoveredItems(List<String> ids);
}
