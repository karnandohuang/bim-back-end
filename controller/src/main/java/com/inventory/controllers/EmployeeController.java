package com.inventory.controllers;

import com.inventory.mappers.GeneralMapper;
import com.inventory.mappers.ModelHelper;
import com.inventory.models.Employee;
import com.inventory.models.Paging;
import com.inventory.services.employee.EmployeeService;
import com.inventory.services.exceptions.employee.EmployeeNotFoundException;
import com.inventory.services.member.MemberService;
import com.inventory.webmodels.requests.DeleteRequest;
import com.inventory.webmodels.requests.employee.EmployeeRequest;
import com.inventory.webmodels.requests.employee.LoginRequest;
import com.inventory.webmodels.responses.BaseResponse;
import com.inventory.webmodels.responses.assignment.AuthenticationResponse;
import com.inventory.webmodels.responses.employee.EmployeeResponse;
import com.inventory.webmodels.responses.employee.ListOfEmployeeResponse;
import com.inventory.webmodels.responses.employee.ListOfSuperiorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static com.inventory.constants.API_PATH.*;

@CrossOrigin
@RestController
public class EmployeeController {

    @Autowired
    private GeneralMapper generalMapper;

    @Autowired
    private ModelHelper helper;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private MemberService memberService;

    @GetMapping(value = API_PATH_EMPLOYEES, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ListOfEmployeeResponse> listOfEmployee(
            @RequestParam(required = false) String name,
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String sortedType
    ) {
        Paging paging = helper.getPaging(pageNumber, pageSize, sortedBy, sortedType);
        if (name == null)
            name = "";
        ListOfEmployeeResponse list =
                new ListOfEmployeeResponse(employeeService.getEmployeeList(name, paging));
        BaseResponse<ListOfEmployeeResponse> response = helper.getBaseResponse(true, "", paging);
        response.setValue(list);
        return response;
    }

    @PostMapping(value = API_PATH_LOGIN, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public BaseResponse<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        BaseResponse<AuthenticationResponse> response;
        if (request == null || request.getEmail() == null || request.getPassword() == null)
            return helper.getBaseResponse(false, "", new Paging());
        String token = memberService.authenticateUser(request.getEmail(), request.getPassword());
        if (token != null) {
            AuthenticationResponse authenticationResponse = new AuthenticationResponse();
            authenticationResponse.setUsername(request.getEmail());
            authenticationResponse.setToken(token);
            response = helper.getBaseResponse(true, "", new Paging());
            response.setValue(authenticationResponse);
        } else {
            response = helper.getBaseResponse(false, "", new Paging());
        }
        return response;
    }

    @GetMapping(value = API_PATH_GET_SUPERIORS, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ListOfSuperiorResponse> listOfSuperior(
            @RequestParam(required = false) String superiorId,
            @RequestParam(required = false) String name,
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String sortedType
    ) {
        Paging paging = helper.getPaging(pageNumber, pageSize, sortedBy, sortedType);
        BaseResponse<ListOfSuperiorResponse> response;
        ListOfSuperiorResponse list =
                null;
        try {
            list = new ListOfSuperiorResponse(employeeService.getSuperiorList(superiorId, name, paging));
            response = helper.getBaseResponse(true, "", paging);
        } catch (RuntimeException e) {
            list = null;
            response = helper.getBaseResponse(false, e.getMessage(), paging);
        }

        response.setValue(list);
        return response;
    }

    @GetMapping(value = API_PATH_GET_EMPLOYEE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<EmployeeResponse> getEmployee(@PathVariable String id) {
        Employee employee;
        BaseResponse<EmployeeResponse> response;
        EmployeeResponse employeeResponse;
        try {
            employee = employeeService.getEmployee(id);
            employeeResponse = new EmployeeResponse(employee);
            response = helper.getBaseResponse(true, "", new Paging());
        } catch (EmployeeNotFoundException e) {
            employeeResponse = new EmployeeResponse(null);
            response = helper.getBaseResponse(true, e.getMessage(), new Paging());
        }
        response.setValue(employeeResponse);
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

