package com.inventory.controllers;

import com.inventory.configurations.MemberDetailsService;
import com.inventory.mappers.GeneralMapper;
import com.inventory.mappers.ModelHelper;
import com.inventory.models.Employee;
import com.inventory.models.Paging;
import com.inventory.services.JwtService;
import com.inventory.services.employee.EmployeeService;
import com.inventory.services.exceptions.employee.EmployeeNotFoundException;
import com.inventory.services.member.MemberService;
import com.inventory.webmodels.requests.DeleteRequest;
import com.inventory.webmodels.requests.employee.EmployeeRequest;
import com.inventory.webmodels.requests.employee.LoginRequest;
import com.inventory.webmodels.responses.BaseResponse;
import com.inventory.webmodels.responses.assignment.AuthenticationResponse;
import com.inventory.webmodels.responses.employee.CheckUserResponse;
import com.inventory.webmodels.responses.employee.EmployeeResponse;
import com.inventory.webmodels.responses.employee.ListOfEmployeeResponse;
import com.inventory.webmodels.responses.employee.ListOfSuperiorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Principal;

import static com.inventory.constants.API_PATH.*;
import static java.util.stream.Collectors.toList;

@CrossOrigin
@RestController
public class EmployeeController {

    @Autowired
    private GeneralMapper generalMapper;

    private UserDetails member;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ModelHelper helper;

    @Autowired
    private MemberDetailsService memberDetailsService;

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
    public BaseResponse<AuthenticationResponse> login(@RequestBody LoginRequest request, HttpServletResponse res) {
        BaseResponse<AuthenticationResponse> response;
        if (request == null || request.getEmail() == null || request.getPassword() == null)
            return helper.getBaseResponse(false, "", new Paging());
        String token = memberService.authenticateUser(request.getEmail(), request.getPassword());
        if (token != null) {
            AuthenticationResponse authenticationResponse = new AuthenticationResponse();
            authenticationResponse.setUsername(request.getEmail());
            authenticationResponse.setToken(token);
            Cookie userCookie = new Cookie("USERCOOKIE", token);
            userCookie.setSecure(false);
            userCookie.setHttpOnly(true);
            res.addCookie(userCookie);
            response = helper.getBaseResponse(true, "", new Paging());
            response.setValue(authenticationResponse);
        } else {
            response = helper.getBaseResponse(false, "", new Paging());
        }
        return response;
    }

    @GetMapping(value = "/api/employee/check")
    public BaseResponse<CheckUserResponse> checkUserRole(@AuthenticationPrincipal Principal principal) {
        this.member = (UserDetails) ((Authentication) principal).getPrincipal();
        BaseResponse<CheckUserResponse> response = helper.getBaseResponse(true, "", new Paging());
        CheckUserResponse user = new CheckUserResponse();
        user.setUsername(member.getUsername());
        user.setRole(member.getAuthorities().stream()
                .map(a -> (a).getAuthority()).collect(toList()));
        response.setValue(user);
        return response;
    }

    @GetMapping("/api/logout")
    public void logout(HttpServletResponse response) {
        response.addCookie(new Cookie("USERCOOKIE", null));
    }

    @GetMapping(value = API_PATH_GET_SUPERIORS, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<ListOfSuperiorResponse> listOfSuperior(
            @RequestParam(required = false) String superiorId,
            @RequestParam(required = false) String name,
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String sortedType,
            @CookieValue("USERCOOKIE") String token
    ) {
        String email = "";
        try {
            email = jwtService.verifyToken(token);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        if (superiorId == null)
            superiorId = employeeService.getEmployeeByEmail(email).getId();
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
    public BaseResponse<EmployeeResponse> getEmployee(
            @PathVariable String id,
            @CookieValue(required = false, name = "USERCOOKIE") String token) {
        String email = "";
        if (token != null) {
            try {
                email = jwtService.verifyToken(token);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }

        Employee employee = null;
        BaseResponse<EmployeeResponse> response;
        EmployeeResponse employeeResponse;
        try {
            if (id != null && !id.equals("null"))
                employee = employeeService.getEmployee(id);
            else if (id.equals("null"))
                employee = employeeService.getEmployeeByEmail(email);
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

