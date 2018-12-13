package com.inventory.services.member;

import com.inventory.services.JwtService;
import com.inventory.services.admin.AdminService;
import com.inventory.services.employee.EmployeeService;
import com.inventory.services.exceptions.auth.FailedToLoginException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtService jwtService;

    @Override
    public String authenticateUser(String email, String password) throws FailedToLoginException {
        boolean isAuthenticated = false;
        try {
            isAuthenticated = adminService.login(email, password);
        } catch (NullPointerException e) {
            isAuthenticated = false;
        }
        if (!isAuthenticated)
            try {
                isAuthenticated = employeeService.login(email, password);
            } catch (NullPointerException e) {
                isAuthenticated = false;
            }
        if (isAuthenticated) {
            try {
                return jwtService.generateToken(email);
            } catch (URISyntaxException | IOException e) {
                throw new FailedToLoginException(e.getMessage());
            }
        }
        throw new FailedToLoginException(String.format("unable to authenticate user [%s]", email));
    }
}
