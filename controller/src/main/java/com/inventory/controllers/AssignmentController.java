package com.inventory.controllers;

import com.inventory.mappers.GeneralMapper;
import com.inventory.mappers.ModelHelper;
import com.inventory.models.Assignment;
import com.inventory.models.Employee;
import com.inventory.models.Item;
import com.inventory.models.Paging;
import com.inventory.services.assignment.AssignmentService;
import com.inventory.services.employee.EmployeeService;
import com.inventory.services.item.ItemService;
import com.inventory.webmodels.requests.assignment.AssignmentRequest;
import com.inventory.webmodels.requests.assignment.ChangeAssignmentStatusRequest;
import com.inventory.webmodels.responses.BaseResponse;
import com.inventory.webmodels.responses.assignment.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.inventory.constants.API_PATH.*;

@RestController
@CrossOrigin
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private EmployeeService employeeService;

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
        BaseResponse response;
        try {
            List<Assignment> listOfAssignment = assignmentService.getEmployeeAssignmentList(employeeId, paging);
            List<EmployeeAssignmentResponse> listOfEmployeeAssignment = new ArrayList<>();
            for (Assignment assignment : listOfAssignment) {
                Item item = itemService.getItem(assignment.getItem().getId());
                EmployeeAssignmentResponse employeeAssignmentResponse =
                        helper.getMappedEmployeeAssignmentResponse(assignment, item);
                listOfEmployeeAssignment.add(employeeAssignmentResponse);
            }
            response = helper.getBaseResponse(
                    true, "", paging);
            response.setValue(listOfEmployeeAssignment);
        } catch (RuntimeException e) {
            response = helper.getBaseResponse(
                    false, e.getMessage(), new Paging());
            response.setValue(null);
        }

        return response;
    }

    @GetMapping(value = API_PATH_GET_ASSIGNMENT_COUNT_BY_EMPLOYEE_ID_AND_STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<AssignmentCountResponse> getAssignmentCount(@RequestParam String employeeId,
                                                                    @RequestParam String status) throws IOException{
        AssignmentCountResponse AssignmentCountResponse = new AssignmentCountResponse();
        BaseResponse response;
        try {
            Double count = assignmentService.getAssignmentCountByEmployeeIdAndStatus(employeeId, status);
            AssignmentCountResponse.setAssignmentCount(count);
            response = helper.getBaseResponse(true, "", new Paging());
            response.setValue(AssignmentCountResponse);
        } catch (RuntimeException e) {
            response = helper.getBaseResponse(false, e.getMessage(), new Paging());
            response.setValue(null);
        }
        return response;
    }

    @GetMapping(value = API_PATH_API_ASSIGNMENT_BY_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<AssignmentResponse> getAssignment(@PathVariable String id) throws IOException {
        AssignmentResponse AssignmentResponse = new AssignmentResponse();
        BaseResponse response;
        try {
            AssignmentResponse.setAssignment(assignmentService.getAssignment(id));
            response = helper.getBaseResponse(true, "", new Paging());
            response.setValue(AssignmentResponse);
        } catch (RuntimeException e) {
            response = helper.getBaseResponse(false, e.getMessage(), new Paging());
            response.setValue(null);
        }
        return response;
    }

    @RequestMapping(value = API_PATH_ASSIGNMENT, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.POST, RequestMethod.PUT})
    public BaseResponse<String> saveAssignment(@RequestBody AssignmentRequest requestBody) {
        Employee employee;
        Item item;
        try {
            employee = employeeService.getEmployee(requestBody.getEmployeeId());
            item = itemService.getItem(requestBody.getItemId());
        } catch (RuntimeException e) {
            return helper.getStandardBaseResponse(false, e.getMessage());
        }
        Assignment rb = helper.getMappedAssignment(requestBody, employee, item);
        try {
            itemService.changeItemQty(rb);
            assignmentService.saveAssignment(rb);
        } catch (RuntimeException e) {
            return helper.getStandardBaseResponse(false, e.getMessage());
        }
            return helper.getStandardBaseResponse(true, "");
    }

    @PutMapping(value = API_PATH_CHANGE_STATUS_ASSIGNMENT, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public BaseResponse<ChangeAssignmentStatusResponse> changeStatus(@RequestBody ChangeAssignmentStatusRequest AssignmentBody) {
        BaseResponse<ChangeAssignmentStatusResponse> response;
        Assignment Assignment = generalMapper.map(AssignmentBody, Assignment.class);
        ChangeAssignmentStatusResponse changeAssignmentStatusResponse = new ChangeAssignmentStatusResponse();
        Map<String, Integer> listOfRecoveredItems;
        try {
            String success = assignmentService.changeStatusAssignments(AssignmentBody.getIds(),
                    Assignment.getStatus(), Assignment.getNotes());
            if (Assignment.getStatus().equals("Rejected")) {
                listOfRecoveredItems = assignmentService.getRecoveredItems(AssignmentBody.getIds());
                String successItem = itemService.recoverItemQty(listOfRecoveredItems);
            }
            response = helper.getBaseResponse(true, "", new Paging());
            response.setValue(changeAssignmentStatusResponse);
        } catch (RuntimeException e) {
            response = helper.getBaseResponse(false, e.getMessage(), new Paging());
        }
        return response;
    }
}
