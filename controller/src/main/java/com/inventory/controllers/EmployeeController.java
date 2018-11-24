package com.inventory.controllers;

import com.inventory.mappers.GeneralMapper;
import com.inventory.models.Employee;
import com.inventory.models.Paging;
import com.inventory.services.employee.EmployeeService;
import com.inventory.webmodels.requests.DeleteRequest;
import com.inventory.webmodels.requests.employee.EmployeeRequest;
import com.inventory.webmodels.requests.employee.LoginRequest;
import com.inventory.webmodels.responses.BaseResponse;
import com.inventory.webmodels.responses.DeleteResponse;
import com.inventory.webmodels.responses.employee.EmployeeResponse;
import com.inventory.webmodels.responses.employee.ListOfEmployeeResponse;
import com.inventory.webmodels.responses.employee.ListOfSuperiorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.inventory.constants.API_PATH.*;
import static com.inventory.constants.ErrorConstant.*;

@CrossOrigin
@RestController
public class EmployeeController {

    @Autowired
    private GeneralMapper generalMapper;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping(value = API_PATH_EMPLOYEES, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ListOfEmployeeResponse> listOfEmployee(
            @RequestParam(required = false) String name,
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String sortedType
    ) throws IOException {
        Paging paging = generalMapper.getPaging(pageNumber, pageSize, sortedBy, sortedType);
        if (name == null)
            name = "";
        ListOfEmployeeResponse list =
                new ListOfEmployeeResponse(employeeService.getEmployeeList(name, paging));
        BaseResponse<ListOfEmployeeResponse> response = generalMapper.getBaseResponse(true, "", paging);
        response.setValue(list);
        return response;
    }

    @PostMapping(value = API_PATH_LOGIN, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<String> login(@RequestBody LoginRequest request) throws IOException {
        Employee employee = employeeService.login(request.getEmail(), request.getPassword());
        if (employee == null)
            return generalMapper.getStandardBaseResponse(false, LOGIN_ERROR);
        return generalMapper.getStandardBaseResponse(true, "");
    }

    @GetMapping(value = API_PATH_GET_SUPERIORS, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ListOfSuperiorResponse> listOfSuperior(
            @RequestParam(required = false) String superiorId,
            @RequestParam(required = false) String name,
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String sortedType
    ) throws IOException {
        Paging paging = generalMapper.getPaging(pageNumber, pageSize, sortedBy, sortedType);
        ListOfSuperiorResponse list =
                new ListOfSuperiorResponse(employeeService.getSuperiorList(superiorId, name, paging));
        BaseResponse<ListOfSuperiorResponse> response = generalMapper.getBaseResponse(true, "", paging);
        response.setValue(list);
        return response;
    }

    @GetMapping(value = API_PATH_GET_EMPLOYEE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<EmployeeResponse> getEmployee(@PathVariable String id) throws IOException {
        EmployeeResponse employeeResponse = new EmployeeResponse(employeeService.getEmployee(id));
        BaseResponse<EmployeeResponse> response = generalMapper.getBaseResponse(true, "", new Paging());
        response.setValue(employeeResponse);
        return response;
    }

    @RequestMapping(value = API_PATH_EMPLOYEES, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.POST, RequestMethod.PUT})
    public BaseResponse<String> saveEmployee(@RequestBody EmployeeRequest request) {
        Employee employee = generalMapper.getMappedEmployee(request);
        if (employeeService.saveEmployee(employee) == (null)) {
            return generalMapper.getStandardBaseResponse(false, SAVE_ERROR);
        } else {
            return generalMapper.getStandardBaseResponse(true, "");
        }
    }

    @DeleteMapping(value = API_PATH_EMPLOYEES, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<DeleteResponse> deleteEmployee(@RequestBody DeleteRequest request) {
        DeleteResponse deleteResponse = null;
        BaseResponse<DeleteResponse> response = null;
        List<String> error = employeeService.deleteEmployee(request.getIds());
        if (error.size() <= 0) {
            response = generalMapper.getBaseResponse(true, "", new Paging());
        } else {
            response = generalMapper.getBaseResponse(false, NORMAL_ERROR, new Paging());
            deleteResponse.setError(error);
            response.setValue(deleteResponse);
        }
        return response;
    }


}

