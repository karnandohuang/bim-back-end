package com.inventory.mappers;

import com.inventory.models.Assignment;
import com.inventory.models.Employee;
import com.inventory.models.Item;
import com.inventory.webmodels.requests.assignment.AssignmentRequest;
import com.inventory.webmodels.responses.assignment.AssignmentCountResponse;
import com.inventory.webmodels.responses.assignment.AssignmentResponse;
import com.inventory.webmodels.responses.assignment.ChangeAssignmentStatusResponse;
import com.inventory.webmodels.responses.assignment.EmployeeAssignmentResponse;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AssignmentHelper extends ModelHelper {

    public AssignmentResponse getMappedAssignmentResponse(Assignment assignment) {
        AssignmentResponse assignmentResponse = new AssignmentResponse();
        assignmentResponse.setAssignment(assignment);
        return assignmentResponse;
    }

    public EmployeeAssignmentResponse getMappedEmployeeAssignmentResponse(Assignment assignment, Item item) {
        EmployeeAssignmentResponse employeeAssignmentResponse = new EmployeeAssignmentResponse();
        item.setQty(assignment.getQty());
        employeeAssignmentResponse.setItem(item);
        employeeAssignmentResponse.setStatus(assignment.getStatus());
        employeeAssignmentResponse.setAssignmentId(assignment.getId());
        return employeeAssignmentResponse;
    }

    public Assignment getMappedAssignment(AssignmentRequest request, Employee employee, Item item, int qty) {
        Assignment assignment = new Assignment();
        assignment.setEmployee(employee);
        assignment.setItem(item);
        assignment.setQty(qty);
        assignment.setNotes("");
        assignment.setStatus("Pending");
        return assignment;
    }

    public ChangeAssignmentStatusResponse getMappedResponse(String success, String successItem) {
        ChangeAssignmentStatusResponse response = new ChangeAssignmentStatusResponse();
        response.setSuccess(success);
        response.setSuccessItem(successItem);
        return response;
    }

    public AssignmentCountResponse getMappedAssignmentCountResponse(Map<String, Double> listOfCount) {
        AssignmentCountResponse response = new AssignmentCountResponse();
        response.setListOfCount(listOfCount);
        return response;
    }

}
