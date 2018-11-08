package com.inventory.controllers;

import com.inventory.mappers.EmployeeMapper;
import com.inventory.mappers.ResponseMapper;
import com.inventory.models.Employee;
import com.inventory.models.Paging;
import com.inventory.services.EmployeeService;
import com.inventory.webmodels.requests.DeleteRequest;
import com.inventory.webmodels.requests.EmployeeRequest;
import com.inventory.webmodels.requests.LoginRequest;
import com.inventory.webmodels.responses.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.inventory.controllers.API_PATH.*;

@CrossOrigin
@RestController
public class EmployeeController {

    @Autowired
    ResponseMapper responseMapper;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    EmployeeMapper employeeMapper;

    @GetMapping(value = API_PATH_EMPLOYEES, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ListOfEmployeeResponse> listOfEmployee(
            @RequestParam(required = false) String name,
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String sortedType
    ) throws IOException {
        Paging paging = responseMapper.getPaging(pageNumber, pageSize, sortedBy, sortedType);
        if (name == null)
            name = "";
        ListOfEmployeeResponse list =
                new ListOfEmployeeResponse(employeeService.getEmployeeList(name, paging));
        BaseResponse<ListOfEmployeeResponse> response = responseMapper.getBaseResponse(true, "", paging);
        response.setValue(list);
        return response;
    }

    @PostMapping(value = API_PATH_LOGIN, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<String> login(@RequestBody LoginRequest request) throws IOException {
        Employee employee = employeeService.login(request.getEmail(), request.getPassword());
        if (employee == null)
            return responseMapper.getStandardBaseResponse(false, "Email or Password is wrong!");
        return responseMapper.getStandardBaseResponse(true, "");
    }

    @GetMapping(value = API_PATH_GET_SUPERIORS, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ListOfSuperiorResponse> listOfSuperior(
            @RequestParam(required = false) String name,
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String sortedType
    ) throws IOException {
        Paging paging = responseMapper.getPaging(pageNumber, pageSize, sortedBy, sortedType);
        if (name == null)
            name = "";
        ListOfSuperiorResponse list =
                new ListOfSuperiorResponse(employeeService.getSuperiorList(name, paging));
        BaseResponse<ListOfSuperiorResponse> response = responseMapper.getBaseResponse(true, "", paging);
        response.setValue(list);
        return response;
    }

    @GetMapping(value = API_PATH_GET_EMPLOYEE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<EmployeeResponse> getEmployee(@PathVariable String id) throws IOException {
        EmployeeResponse employeeResponse = new EmployeeResponse(employeeService.getEmployee(id));
        BaseResponse<EmployeeResponse> response = responseMapper.getBaseResponse(true, "", new Paging());
        response.setValue(employeeResponse);
        return response;
    }

    @RequestMapping(value = API_PATH_EMPLOYEES, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.POST, RequestMethod.PUT})
    public BaseResponse<String> insertEmployee(@RequestBody EmployeeRequest request) {
        Employee employee = employeeMapper.getMappedEmployee(request);

        if (employeeService.saveEmployee(employee) == (null)) {
            return responseMapper.getStandardBaseResponse(false, "save failed");
        } else {
            return responseMapper.getStandardBaseResponse(true, "");
        }
    }

    @DeleteMapping(value = API_PATH_EMPLOYEES, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<DeleteResponse> deleteEmployee(@RequestBody DeleteRequest request) {
        DeleteResponse deleteResponse = null;
        BaseResponse<DeleteResponse> response = null;
        List<String> error = employeeService.deleteEmployee(request.getIds());
        if (error.size() <= 0) {
            response = responseMapper.getBaseResponse(true, "", new Paging());
        } else {
            response = responseMapper.getBaseResponse(false, "There is an error", new Paging());
            deleteResponse.setValue(error);
            response.setValue(deleteResponse);
        }
        return response;
    }


}

