package com.inventory.services.member;

import com.inventory.models.abstract_entity.Member;
import com.inventory.models.entity.Employee;
import com.inventory.services.admin.AdminService;
import com.inventory.services.employee.EmployeeService;
import com.inventory.services.security.JwtService;
import com.inventory.services.utils.exceptions.auth.FailedToLoginException;
import com.inventory.services.utils.exceptions.auth.LoginEmptyException;
import com.inventory.services.utils.exceptions.auth.MemberEmailWrongException;
import com.inventory.services.utils.exceptions.auth.MemberNotFoundException;
import com.inventory.services.utils.validators.MemberValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtService jwtService;

    private static final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);
    @Autowired
    private MemberValidator validator;

    @Override
    public String authenticateUser(String email, String password) throws FailedToLoginException {
        logger.info("email : " + email);

        String nullFieldMember = validator.validateNullFieldMember(email, password);

        boolean isEmailValid = validator.validateEmailFormatMember(email);

        logger.info("null field : " + nullFieldMember);

        if (!isEmailValid)
            throw new MemberEmailWrongException();

        else if (nullFieldMember != null)
            throw new LoginEmptyException(nullFieldMember);

        else {
            boolean isAuthenticated = false;
            isAuthenticated = adminService.login(email, password);
            if (!isAuthenticated)
                isAuthenticated = employeeService.login(email, password);
            if (isAuthenticated) {
                return jwtService.generateToken(email);
            }
            throw new FailedToLoginException(String.format("unable to authenticate user [%s]", email));
        }
    }

    @Override
    public String getMemberRole(String email) {
        Member member;
        try {
            member = adminService.getAdminByEmail(email);
            return "ADMIN";
        } catch (RuntimeException e) {
            member = null;
        }

        if (member == null) {
            try {
                member = employeeService.getEmployeeByEmail(email);
            } catch (RuntimeException e) {
                throw new MemberNotFoundException(email);
            }
        }
            return ((Employee) member).getRole();
    }

    @Override
    public UserDetails getLoggedInUser(@AuthenticationPrincipal Principal principal) {
        return (UserDetails) ((Authentication) principal).getPrincipal();
    }
}
