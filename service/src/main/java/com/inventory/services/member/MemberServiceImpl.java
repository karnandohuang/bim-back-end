package com.inventory.services.member;

import com.inventory.models.Employee;
import com.inventory.models.Member;
import com.inventory.services.JwtService;
import com.inventory.services.admin.AdminService;
import com.inventory.services.employee.EmployeeService;
import com.inventory.services.exceptions.auth.FailedToLoginException;
import com.inventory.services.exceptions.auth.LoginEmptyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Principal;

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
            if (email.equals("admin") && password.equals("admin123"))
                isAuthenticated = true;
            else
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

    @Override
    public String getMemberRole(String email) {
        Member member;
        try {
            member = adminService.getAdminByEmail(email);
        } catch (RuntimeException e) {
            member = null;
        }

        if (member == null) {
            try {
                member = employeeService.getEmployeeByEmail(email);
            } catch (RuntimeException e) {
                throw new FailedToLoginException(String.format("unable to authenticate user [%s]", email));
            }
        }

        if (member instanceof Employee)
            return ((Employee) member).getRole();
        return "ADMIN";
    }

    @Override
    public void validateUser(String email, String password) {
        if (email == null)
            throw new LoginEmptyException("Email");
        else if (password == null)
            throw new LoginEmptyException("Password");
    }

    @Override
    public UserDetails getLoggedInUser(@AuthenticationPrincipal Principal principal) {
        return (UserDetails) ((Authentication) principal).getPrincipal();
    }
}
