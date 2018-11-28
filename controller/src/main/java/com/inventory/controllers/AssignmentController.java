package com.inventory.controllers;

import com.inventory.mappers.GeneralMapper;
import com.inventory.mappers.ModelHelper;
import com.inventory.models.Assignment;
import com.inventory.models.Item;
import com.inventory.models.Paging;
import com.inventory.services.assignment.AssignmentService;
import com.inventory.services.item.ItemService;
import com.inventory.webmodels.requests.DeleteRequest;
import com.inventory.webmodels.requests.assignment.AssignmentRequest;
import com.inventory.webmodels.requests.assignment.ChangeAssignmentStatusRequest;
import com.inventory.webmodels.responses.BaseResponse;
import com.inventory.webmodels.responses.DeleteResponse;
import com.inventory.webmodels.responses.assignment.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.inventory.constants.API_PATH.*;
import static com.inventory.constants.ErrorConstant.NORMAL_ERROR;
import static com.inventory.constants.ErrorConstant.SAVE_ERROR;

@RestController
@CrossOrigin
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ModelHelper helper;

    @Autowired
    private GeneralMapper generalMapper;

    @GetMapping(value = API_PATH_ASSIGNMENT, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ListOfAssignmentResponse> listOfAssignment(
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String sortedType
    ) throws IOException {
        Paging paging = helper.getPaging(pageNumber, pageSize, sortedBy, sortedType);
        List<Assignment> listOfAssignment = assignmentService.getAssignmentList(paging);
        List<AssignmentResponse> listOfAssignmentResponse = new ArrayList<>();
        for (Assignment assignment : listOfAssignment) {
            AssignmentResponse AssignmentResponse = helper.getMappedAssignmentResponse(assignment);
            listOfAssignmentResponse.add(AssignmentResponse);
        }
        ListOfAssignmentResponse list = new ListOfAssignmentResponse(listOfAssignmentResponse);
        BaseResponse<ListOfAssignmentResponse> response = helper.getBaseResponse(true, "", paging);
        response.setValue(list);
        return response;
    }

    @GetMapping(value = API_PATH_EMPLOYEE_ASSIGNMENT, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<List<EmployeeAssignmentResponse>> listOfEmployeeAssignment(
            @RequestParam String employeeId,
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String sortedType
    ) throws IOException {
        Paging paging = helper.getPaging(pageNumber, pageSize, sortedBy, sortedType);
        List<Assignment> listOfAssignment = assignmentService.getEmployeeAssignmentList(employeeId, paging);
        List<EmployeeAssignmentResponse> listOfEmployeeAssignment = new ArrayList<>();
        List<AssignmentResponse> list = new ArrayList<>();
        for (Assignment Assignment : listOfAssignment) {
            Item item = itemService.getItem(Assignment.getItemId());
            EmployeeAssignmentResponse employeeAssignmentResponse = helper.getMappedEmployeeAssignmentResponse(Assignment, item);
            listOfEmployeeAssignment.add(employeeAssignmentResponse);
        }
        BaseResponse<List<EmployeeAssignmentResponse>> response = helper.getBaseResponse(
                true, "", paging);
        response.setValue(listOfEmployeeAssignment);
        return response;
    }

    @GetMapping(value = API_PATH_GET_ASSIGNMENT_COUNT_BY_EMPLOYEE_ID_AND_STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<AssignmentCountResponse> getAssignmentCount(@RequestParam String id,
                                                              @RequestParam String status) throws IOException{
        AssignmentCountResponse AssignmentCountResponse = new AssignmentCountResponse();
        AssignmentCountResponse.setAssignmentCount(assignmentService.getAssignmentCountByEmployeeIdAndStatus(id, status));
        BaseResponse<AssignmentCountResponse> response = helper.getBaseResponse(true, "", new Paging());
        response.setValue(AssignmentCountResponse);
        return response;
    }

    @GetMapping(value = API_PATH_API_ASSIGNMENT_BY_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<AssignmentResponse> getAssignment(@PathVariable String id) throws IOException {
        AssignmentResponse AssignmentResponse = new AssignmentResponse();
        AssignmentResponse.setAssignment(assignmentService.getAssignment(id));
        BaseResponse<AssignmentResponse> response = helper.getBaseResponse(true, "", new Paging());
        response.setValue(AssignmentResponse);
        return response;
    }

    @RequestMapping(value = API_PATH_ASSIGNMENT, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.POST, RequestMethod.PUT})
    public BaseResponse<String> saveAssignment(@RequestBody AssignmentRequest requestBody) {
        Assignment rb = generalMapper.getMappedAssignment(requestBody);
        Assignment Assignment;
        Item item = itemService.changeItemQty(rb);
        if (item == null)
            Assignment = null;
        else {
            Assignment = assignmentService.saveAssignment(rb);
        }

        if (Assignment == null || item == null) {
            return helper.getStandardBaseResponse(false, SAVE_ERROR);
        } else {
            return helper.getStandardBaseResponse(true, "");
        }
    }

    @PutMapping(value = API_PATH_CHANGE_STATUS_ASSIGNMENT, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public BaseResponse<ChangeAssignmentStatusResponse> changeStatus(@RequestBody ChangeAssignmentStatusRequest AssignmentBody) {
        BaseResponse<ChangeAssignmentStatusResponse> response;
        Assignment Assignment = generalMapper.getMappedAssignment(AssignmentBody);
        ChangeAssignmentStatusResponse changeAssignmentStatusResponse = new ChangeAssignmentStatusResponse();
        Map<String, Integer> listOfRecoveredItems = new HashMap<>();
        List<String> errorOfItem = new ArrayList<>();
        List<String> errors = assignmentService.changeStatusAssignments(AssignmentBody.getIds(),
                Assignment.getStatus(), Assignment.getNotes());
        if (Assignment.getStatus().equals("Rejected")) {
            listOfRecoveredItems = assignmentService.getRecoveredItems(AssignmentBody.getIds());
            errorOfItem = itemService.recoverItemQty(listOfRecoveredItems);
        }
        if (errors.size() <= 0 && errorOfItem.size() <= 0) {
            response = helper.getBaseResponse(true, "", new Paging());
        } else {
            response = helper.getBaseResponse(false, NORMAL_ERROR, new Paging());
            if (errors.size() > 0)
                changeAssignmentStatusResponse.setErrors(errors);
            else if (errors.size() > 0 && errorOfItem.size() > 0) {
                changeAssignmentStatusResponse.setErrorOfItem(errorOfItem);
                changeAssignmentStatusResponse.setErrors(errors);
            } else
                changeAssignmentStatusResponse.setErrorOfItem(errorOfItem);
        }
        response.setValue(changeAssignmentStatusResponse);
        return response;
    }

    @DeleteMapping(value = API_PATH_ASSIGNMENT, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public BaseResponse<DeleteResponse> deleteAssignment(@RequestBody DeleteRequest request) {
        DeleteResponse deleteResponse = null;
        BaseResponse<DeleteResponse> response = null;
        Map<String, Integer> listOfRecoveredItems = assignmentService.getRecoveredItems(request.getIds());
        List<String> error = assignmentService.deleteAssignments(request.getIds());
        List<String> errorOfItem = itemService.recoverItemQty(listOfRecoveredItems);
        if (error.size() <= 0 && errorOfItem.size() <= 0) {
            response = helper.getBaseResponse(true, "", new Paging());
        } else {
            response = helper.getBaseResponse(false, NORMAL_ERROR, new Paging());
            if(error.size() > 0)
                deleteResponse.setError(error);
            else
                deleteResponse.setError(errorOfItem);
            response.setValue(deleteResponse);
        }
        return response;
    }
}
