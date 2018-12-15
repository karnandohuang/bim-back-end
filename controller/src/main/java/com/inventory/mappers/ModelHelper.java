package com.inventory.mappers;

import com.inventory.models.Assignment;
import com.inventory.models.Employee;
import com.inventory.models.Item;
import com.inventory.models.Paging;
import com.inventory.webmodels.requests.assignment.AssignmentRequest;
import com.inventory.webmodels.responses.BaseResponse;
import com.inventory.webmodels.responses.assignment.AssignmentResponse;
import com.inventory.webmodels.responses.assignment.EmployeeAssignmentResponse;
import com.inventory.webmodels.responses.item.UploadFileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


@Component
public class ModelHelper {
    public BaseResponse<String> getStandardBaseResponse(boolean success, String errorMessage) {
        BaseResponse<String> response = new BaseResponse<>();
        response.setCode(HttpStatus.OK.toString());
        response.setSuccess(success);
        response.setValue("");
        response.setErrorMessage(errorMessage);
        return response;
    }

    public BaseResponse<UploadFileResponse> getUploadBaseResponse(boolean success, String errorMessage) {
        BaseResponse<UploadFileResponse> response = new BaseResponse<>();
        response.setCode(HttpStatus.OK.toString());
        response.setSuccess(success);
        response.setErrorMessage(errorMessage);
        return response;
    }

    public BaseResponse getBaseResponse(boolean success, String errorMessage, Paging paging) {
        BaseResponse response = new BaseResponse<>();
        response.setCode(HttpStatus.OK.toString());
        response.setSuccess(success);
        response.setErrorMessage(errorMessage);
        response.setPaging(paging);
        return response;
    }

    public BaseResponse<byte[]> getPdfBaseResponse(boolean success, byte[] document) {
        BaseResponse<byte[]> response = new BaseResponse<>();
        response.setValue(document);
        response.setCode(HttpStatus.OK.toString());
        response.setSuccess(success);
        return response;
    }

    public Paging getPaging(int pageNumber, int pageSize, String sortedBy, String sortedType) {
        Paging paging = new Paging();
        paging.setPageNumber(pageNumber);
        paging.setPageSize(pageSize);
        if (sortedBy != null)
            paging.setSortedBy(sortedBy);
        else
            paging.setSortedBy("updatedDate");
        if (sortedType != null)
            paging.setSortedType(sortedType);
        else
            paging.setSortedType("desc");
        return paging;
    }

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
}
