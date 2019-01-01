package com.inventory.controllers;

import com.inventory.helpers.AssignmentHelper;
import com.inventory.models.Paging;
import com.inventory.models.entity.Assignment;
import com.inventory.models.entity.Employee;
import com.inventory.models.entity.Item;
import com.inventory.services.assignment.AssignmentService;
import com.inventory.services.employee.EmployeeService;
import com.inventory.services.item.ItemService;
import com.inventory.services.member.MemberService;
import com.inventory.services.utils.GeneralMapper;
import com.inventory.webmodels.requests.assignment.AssignmentRequest;
import com.inventory.webmodels.requests.assignment.ChangeAssignmentStatusRequest;
import com.inventory.webmodels.requests.item.ItemRequest;
import com.inventory.webmodels.responses.BaseResponse;
import com.inventory.webmodels.responses.assignment.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.inventory.webmodels.API_PATH.*;

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
    private MemberService memberService;

    @Autowired
    private AssignmentHelper helper;

    @Autowired
    private GeneralMapper generalMapper;

    @GetMapping(value = API_PATH_ASSIGNMENT, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ListOfAssignmentResponse> listOfAssignment(
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String sortedType,
            @RequestParam(required = false) String filterStatus
    ) {
        Paging paging = helper.getPaging(pageNumber, pageSize, sortedBy, sortedType);
        List<Assignment> listOfAssignment = assignmentService.getAssignmentList(filterStatus, paging);
        List<AssignmentResponse> listOfAssignmentResponse = new ArrayList<>();
        for (Assignment assignment : listOfAssignment) {
            AssignmentResponse assignmentResponse = helper.getMappedAssignmentResponse(assignment);
            listOfAssignmentResponse.add(assignmentResponse);
        }
        ListOfAssignmentResponse list = new ListOfAssignmentResponse(listOfAssignmentResponse);
        BaseResponse<ListOfAssignmentResponse> response =
                helper.getListBaseResponse(true, "", paging);
        response.setValue(list);
        return response;
    }

    @GetMapping(value = API_PATH_EMPLOYEE_ASSIGNMENT, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ListOfEmployeeAssignmentResponse> listOfEmployeeAssignment(
            @AuthenticationPrincipal Principal principal,
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String sortedType,
            @RequestParam(required = false) String filterStatus
    ) {
        Paging paging = helper.getPaging(pageNumber, pageSize, sortedBy, sortedType);
        BaseResponse response;
        try {
            UserDetails member = memberService.getLoggedInUser(principal);
            Employee employee = employeeService.getEmployeeByEmail(member.getUsername());
            List<Assignment> listOfAssignment = assignmentService.getEmployeeAssignmentList(
                    employee.getId(),
                    filterStatus,
                    paging);
            List<EmployeeAssignmentResponse> listOfEmployeeAssignment = new ArrayList<>();
            for (Assignment assignment : listOfAssignment) {
                EmployeeAssignmentResponse employeeAssignmentResponse =
                        helper.getMappedEmployeeAssignmentResponse(assignment, assignment.getItem(), assignment.getEmployee());
                listOfEmployeeAssignment.add(employeeAssignmentResponse);
            }
            ListOfEmployeeAssignmentResponse listResponse = new ListOfEmployeeAssignmentResponse();
            listResponse.setValue(listOfEmployeeAssignment);
            response = helper.getListBaseResponse(
                    true, "", paging);
            response.setValue(listResponse);
        } catch (RuntimeException e) {
            response = helper.getListBaseResponse(
                    false, e.getMessage(), helper.getEmptyPaging());
            response.setValue(null);
        }

        return response;
    }

    @GetMapping(value = API_PATH_SUPERIOR_EMPLOYEE_ASSIGNMENT, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<List<EmployeeAssignmentResponse>> listOfEmployeeSuperiorAssignment(
            @AuthenticationPrincipal Principal principal,
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String sortedType,
            @RequestParam(required = false) String filterStatus
    ) {
        Paging paging = helper.getPaging(pageNumber, pageSize, sortedBy, sortedType);
        BaseResponse response;
        try {
            UserDetails member = memberService.getLoggedInUser(principal);
            Employee employee = employeeService.getEmployeeByEmail(member.getUsername());
            List<Assignment> listOfAssignment = assignmentService.getEmployeeSuperiorAssignmentList(
                    employee.getId(),
                    filterStatus,
                    paging);
            List<EmployeeAssignmentResponse> listOfEmployeeAssignment = new ArrayList<>();
            for (Assignment assignment : listOfAssignment) {
                EmployeeAssignmentResponse employeeAssignmentResponse =
                        helper.getMappedEmployeeAssignmentResponse(assignment, assignment.getItem(), assignment.getEmployee());
                listOfEmployeeAssignment.add(employeeAssignmentResponse);
            }
            response = helper.getListBaseResponse(
                    true, "", paging);
            response.setValue(listOfEmployeeAssignment);
        } catch (RuntimeException e) {
            response = helper.getListBaseResponse(
                    false, e.getMessage(), helper.getEmptyPaging());
            response.setValue(null);
        }

        return response;
    }

    @GetMapping(value = API_PATH_GET_ASSIGNMENT_COUNT_BY_EMPLOYEE_ID_AND_STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<AssignmentCountResponse> getAssignmentCount(@AuthenticationPrincipal Principal principal) {
        BaseResponse response;
        UserDetails member = memberService.getLoggedInUser(principal);
        if (memberService.getMemberRole(member.getUsername()).equals("ADMIN")) {
            try {
                Map<String, Double> listOfCount = assignmentService.getAssignmentCountByEmployeeId("ADMIN");
                response = helper.getBaseResponse(true, "");
                response.setValue(helper.getMappedAssignmentCountResponse(listOfCount));
            } catch (RuntimeException e) {
                response = helper.getBaseResponse(false, e.getMessage());
                response.setValue(null);
            }
        } else {
            Employee employee = employeeService.getEmployeeByEmail(member.getUsername());
            try {
                Map<String, Double> listOfCount = assignmentService.getAssignmentCountByEmployeeId(employee.getId());
                response = helper.getBaseResponse(true, "");
                response.setValue(helper.getMappedAssignmentCountResponse(listOfCount));
            } catch (RuntimeException e) {
                response = helper.getBaseResponse(false, e.getMessage());
                response.setValue(null);
            }
        }
        return response;
    }

    @GetMapping(value = API_PATH_API_ASSIGNMENT_BY_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<AssignmentResponse> getAssignment(@PathVariable String id) {
        BaseResponse response;
        try {
            response = helper.getBaseResponse(true, "");
            response.setValue(helper.getMappedAssignmentResponse(assignmentService.getAssignment(id)));
        } catch (RuntimeException e) {
            response = helper.getBaseResponse(false, e.getMessage());
            response.setValue(null);
        }
        return response;
    }

    @RequestMapping(value = API_PATH_ASSIGNMENT, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.POST, RequestMethod.PUT})
    @Transactional
    public BaseResponse<String> saveAssignment(@RequestBody AssignmentRequest requestBody,
                                               @AuthenticationPrincipal Principal principal) {
        Employee employee;
        Item item;
        try {
            UserDetails member = memberService.getLoggedInUser(principal);
            employee = employeeService.getEmployeeByEmail(member.getUsername());
        } catch (RuntimeException e) {
            return helper.getStandardBaseResponse(false, e.getMessage());
        }
        for (ItemRequest itemRequest : requestBody.getItems()) {
            item = generalMapper.map(itemRequest, Item.class);
            int qty = item.getQty();
            try {
                item = itemService.getItem(item.getId());
            } catch (RuntimeException e) {
                return helper.getStandardBaseResponse(false, e.getMessage());
            }
            Assignment rb = helper.getMappedAssignment(employee, item, qty);
            try {
                itemService.changeItemQty(rb);
                assignmentService.saveAssignment(rb);
            } catch (RuntimeException e) {
                return helper.getStandardBaseResponse(false, e.getMessage());
            }
        }
            return helper.getStandardBaseResponse(true, "");
    }

    @PutMapping(value = API_PATH_CHANGE_STATUS_ASSIGNMENT, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public BaseResponse<ChangeAssignmentStatusResponse> changeStatus(
            @RequestBody ChangeAssignmentStatusRequest assignmentBody) {
        BaseResponse<ChangeAssignmentStatusResponse> response;
        Assignment assignment = generalMapper.map(assignmentBody, Assignment.class);
        Map<String, Integer> listOfRecoveredItems;
        try {
            String success = assignmentService.changeStatusAssignments(assignmentBody.getIds(),
                    assignment.getStatus(), assignment.getNotes(), assignmentBody.getEmployeeId());
            String successItem = "";
            if (assignment.getStatus().equals("Rejected")) {
                listOfRecoveredItems = assignmentService.getRecoveredItems(assignmentBody.getIds());
                successItem = itemService.recoverItemQty(listOfRecoveredItems);
            }
            response = helper.getBaseResponse(true, "");
            response.setValue(helper.getMappedResponse(success, successItem));
        } catch (RuntimeException e) {
            response = helper.getBaseResponse(false, e.getMessage());
            response.setValue(null);
        }
        return response;
    }
}
