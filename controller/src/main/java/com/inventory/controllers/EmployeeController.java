package com.inventory.controllers;

import com.inventory.models.Employee;
import com.inventory.models.Paging;
import com.inventory.services.EmployeeServiceImpl;
import com.inventory.webmodels.JsonMapper;
import com.inventory.webmodels.requests.DeleteRequest;
import com.inventory.webmodels.requests.EmployeeRequest;
import com.inventory.webmodels.requests.LoginRequest;
import com.inventory.webmodels.responses.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
public class EmployeeController {

    @Autowired
    private EmployeeServiceImpl employeeService;

    private JsonMapper mapper = new JsonMapper();

    @RequestMapping(value = "api/employees", produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET, headers = "Accept=application/json")
    public @ResponseBody
    String listOfEmployee() throws IOException {
        Paging paging = new Paging();
        ListOfEmployeeResponse list = employeeService.getEmployeeList(paging);
            return mapper.createJson(list);
    }

    @RequestMapping(value = "api/login", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST,
            headers = "Accept=application/json")
    public @ResponseBody
    String login(@RequestBody LoginRequest request) throws IOException {
        Paging paging = new Paging();
        StandardResponse response = employeeService.login(request.getEmail(), request.getPassword());
        return mapper.createJson(response);
    }

    @RequestMapping(value = "api/superiors", produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.GET, headers = "Accept=application/json")
    public @ResponseBody
    String listOfSuperior() throws IOException{
        Paging paging = new Paging();
        ListOfSuperiorResponse list = employeeService.getSuperiorList(paging);
        return mapper.createJson(list);
    }

    @RequestMapping(value = "api/employees/{id}", consumes = "application/json", produces = "application/json",
            method = RequestMethod.GET)
    public String EmployeeData(@PathVariable String id) throws IOException{
           EmployeeResponse response = employeeService.getEmployee(id);
            return mapper.createJson(response.getValue());
    }

    @RequestMapping(value = "api/employees", consumes = "application/json", produces = "application/json",
            method = RequestMethod.POST)
    public StandardResponse insertEmployee(@RequestBody EmployeeRequest request){

        Employee employee = setEmployee(request);

            if( employeeService.saveEmployee(employee).equals(null)) {
                return new StandardResponse("false", "save failed");
            } else{
                return new StandardResponse("true", "");
            }
    }

    private Employee setEmployee(@RequestBody EmployeeRequest request) {
        Employee employee = new Employee();
        employee.setId(request.getId());
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setPassword(request.getPassword());
        employee.setDob(request.getDob());
        employee.setPosition(request.getPosition());
        employee.setDivision(request.getDivision());
        employee.setSuperiorId(request.getSuperiorId());
        return employee;
    }

    @RequestMapping(value = "api/employees", consumes = "application/json", produces = "application/json",
            method = RequestMethod.PUT)
    public StandardResponse editEmployee(@RequestBody EmployeeRequest request){
        Employee employee = setEmployee(request);
        if( employeeService.saveEmployee(employee).equals(null)) {
            return new StandardResponse("false", "save failed");
        } else{
            return new StandardResponse("true", "");
        }
    }

    @RequestMapping(value = "api/employees", produces = MediaType.APPLICATION_JSON_VALUE,
            method = RequestMethod.DELETE, headers = "Accept=application/json")
    public DeleteResponse deleteEmployee(@RequestBody DeleteRequest request){
            return employeeService.deleteEmployee(request.getIds());
    }


}

