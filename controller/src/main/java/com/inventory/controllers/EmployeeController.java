package com.inventory.controllers;

import com.inventory.mappers.EmployeeHelper;
import com.inventory.models.Paging;
import com.inventory.models.entity.Employee;
import com.inventory.services.GeneralMapper;
import com.inventory.services.employee.EmployeeService;
import com.inventory.services.exceptions.employee.EmployeeNotFoundException;
import com.inventory.webmodels.requests.DeleteRequest;
import com.inventory.webmodels.requests.employee.EmployeeRequest;
import com.inventory.webmodels.responses.BaseResponse;
import com.inventory.webmodels.responses.employee.EmployeeResponse;
import com.inventory.webmodels.responses.employee.ListOfEmployeeResponse;
import com.inventory.webmodels.responses.employee.ListOfSuperiorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.inventory.webmodels.API_PATH.*;

@CrossOrigin
@RestController
public class EmployeeController {

    @Autowired
    private GeneralMapper generalMapper;

    @Autowired
    private EmployeeHelper helper;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping(value = API_PATH_EMPLOYEES, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ListOfEmployeeResponse> listOfEmployee(
            @RequestParam(required = false) String name,
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String sortedType
    ) {
        Paging paging = helper.getPaging(pageNumber, pageSize, sortedBy, sortedType);
        ListOfEmployeeResponse list =
                new ListOfEmployeeResponse(employeeService.getEmployeeList(name, paging));
        BaseResponse<ListOfEmployeeResponse> response = helper.getListBaseResponse(true, "", paging);
        response.setValue(list);
        return response;
    }

    @GetMapping(value = API_PATH_GET_SUPERIORS, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ListOfSuperiorResponse> listOfSuperior(
            @AuthenticationPrincipal Principal principal,
            @RequestParam(required = false) String name,
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String sortedType
    ) {
        Paging paging = helper.getPaging(pageNumber, pageSize, sortedBy, sortedType);
        BaseResponse<ListOfSuperiorResponse> response;
        ListOfSuperiorResponse list;
        String email = ((UserDetails) (((Authentication) principal).getPrincipal())).getUsername();
        String employeeId = employeeService.getEmployeeByEmail(email).getId();
        try {
            list = new ListOfSuperiorResponse(employeeService.getSuperiorList(employeeId, name, paging));
            response = helper.getListBaseResponse(true, "", paging);
        } catch (RuntimeException e) {
            list = null;
            response = helper.getListBaseResponse(false, e.getMessage(), paging);
        }

        response.setValue(list);
        return response;
    }

    @GetMapping(value = API_PATH_GET_EMPLOYEE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PostAuthorize("hasRole('ADMIN') OR returnObject.value.value.email == principal.username")
    public BaseResponse<EmployeeResponse> getEmployee(
            @PathVariable String id) {
        BaseResponse<EmployeeResponse> response;
        try {
            Employee employee = null;
            if (id != null && !id.equals("null"))
                employee = employeeService.getEmployee(id);
            response = helper.getBaseResponse(true, "");
            response.setValue(new EmployeeResponse(employee));
        } catch (EmployeeNotFoundException e) {
            response = helper.getBaseResponse(true, e.getMessage());
            response.setValue(null);
        }
        return response;
    }

    @RequestMapping(value = API_PATH_EMPLOYEES, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.POST, RequestMethod.PUT})
    @Transactional
    public BaseResponse<String> saveEmployee(@RequestBody EmployeeRequest request) {
        Employee employee = generalMapper.map(request, Employee.class);
        try {
            employeeService.saveEmployee(employee);
            return helper.getStandardBaseResponse(true, "");
        } catch (RuntimeException e) {
            return helper.getStandardBaseResponse(false, e.getMessage());
        }
    }

    @DeleteMapping(value = API_PATH_EMPLOYEES, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public BaseResponse<String> deleteEmployee(@RequestBody DeleteRequest request) {
        BaseResponse<String> response;
        try {
            String success = employeeService.deleteEmployee(request.getIds());
            response = helper.getStandardBaseResponse(true, success);
        } catch (RuntimeException e) {
            response = helper.getStandardBaseResponse(false, e.getMessage());
        }
        return response;
    }


}

