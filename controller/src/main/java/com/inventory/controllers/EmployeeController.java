package com.inventory.controllers;

import com.inventory.models.Employee;
import com.inventory.models.Paging;
import com.inventory.services.EmployeeService;
import com.inventory.webmodels.requests.DeleteRequest;
import com.inventory.webmodels.requests.EmployeeRequest;
import com.inventory.webmodels.requests.LoginRequest;
import com.inventory.webmodels.responses.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.inventory.controllers.API_PATH.*;

@CrossOrigin
@RestController
public class EmployeeController {

    @Autowired
    DataMapper mapper;
    @Autowired
    private EmployeeService employeeService;

    @GetMapping(value = API_PATH_EMPLOYEES, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ListOfEmployeeResponse> listOfEmployee() throws IOException {
        Paging paging = new Paging();
        ListOfEmployeeResponse list = new ListOfEmployeeResponse(employeeService.getEmployeeList(paging));
        BaseResponse<ListOfEmployeeResponse> response = mapper.getBaseResponse(true, "");
        response.setValue(list);
        return response;
    }

    @PostMapping(value = API_PATH_LOGIN, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<String> login(@RequestBody LoginRequest request) throws IOException {
        Paging paging = new Paging();
        Employee employee = employeeService.login(request.getEmail(), request.getPassword());
        if (employee == null)
            return mapper.getStandardBaseResponse(false, "Email or Password is wrong!");
        return mapper.getStandardBaseResponse(true, "");
    }

    @GetMapping(value = API_PATH_GET_SUPERIORS, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ListOfSuperiorResponse> listOfSuperior() throws IOException {
        Paging paging = new Paging();
        ListOfSuperiorResponse list = new ListOfSuperiorResponse(employeeService.getSuperiorList(paging));
        BaseResponse<ListOfSuperiorResponse> response = mapper.getBaseResponse(true, "");
        response.setValue(list);
        return response;
    }

    @GetMapping(value = API_PATH_GET_EMPLOYEE, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<EmployeeResponse> getEmployee(@PathVariable String id) throws IOException {
        EmployeeResponse employeeResponse = new EmployeeResponse(employeeService.getEmployee(id));
        BaseResponse<EmployeeResponse> response = mapper.getBaseResponse(true, "");
        response.setValue(employeeResponse);
        return response;
    }

    @RequestMapping(value = API_PATH_EMPLOYEES, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE, method = {RequestMethod.POST, RequestMethod.PUT})
    public BaseResponse<String> insertEmployee(@RequestBody EmployeeRequest request) {
        Employee employee = mapper.mapEmployee(request);

        if (employeeService.saveEmployee(employee) == (null)) {
            return mapper.getStandardBaseResponse(false, "save failed");
        } else {
            return mapper.getStandardBaseResponse(true, "");
        }
    }

    @DeleteMapping(value = API_PATH_EMPLOYEES, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<DeleteResponse> deleteEmployee(@RequestBody DeleteRequest request) {
        DeleteResponse deleteResponse = null;
        BaseResponse<DeleteResponse> response = null;
        List<String> error = employeeService.deleteEmployee(request.getIds());
        if (error.size() <= 0) {
            response = mapper.getBaseResponse(true, "");
        } else {
            response = mapper.getBaseResponse(false, "There is an error");
            deleteResponse.setValue(error);
            response.setValue(deleteResponse);
        }
        return response;
    }


}

