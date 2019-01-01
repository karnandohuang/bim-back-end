package com.inventory.services;

import com.inventory.models.entity.Admin;
import com.inventory.models.entity.Employee;
import com.inventory.services.admin.AdminService;
import com.inventory.services.employee.EmployeeService;
import com.inventory.services.member.MemberServiceImpl;
import com.inventory.services.security.JwtService;
import com.inventory.services.utils.exceptions.admin.AdminNotFoundException;
import com.inventory.services.utils.exceptions.employee.EmployeeNotFoundException;
import com.inventory.services.utils.validators.MemberValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberServiceImpl memberService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private AdminService adminService;

    @Mock
    private JwtService jwtService;

    @Mock
    private MemberValidator validator;

    @Test
    public void authenticateUserValidEmailAndValidPasswordAdminSuccess() {
        String email = "admin2@gdn-commerce.com";
        String password = "admin2";
        mockLoginAdmin(true, email, password);
        mockValidateEmailMember(true, email);
        mockGenerateToken();
        String token = memberService.authenticateUser(email, password);
        assertNotNull(token);

        verify(validator).validateEmailFormatMember(email);
        verify(validator).validateNullFieldMember(email, password);
        verify(adminService).login(email, password);
        verify(jwtService).generateToken(email);
        verifyNoMoreInteractions(validator);
        verifyZeroInteractions(employeeService);
        verifyNoMoreInteractions(adminService);
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    public void authenticateUserEmailNullFailed() {
        String email = null;
        String password = "admin2";
        mockValidateEmailMember(true, email);
        mockNullFieldMemberFound(true);
        try {
            memberService.authenticateUser(email, password);
        } catch (RuntimeException e) {
            verify(validator).validateNullFieldMember(email, password);
            verify(validator).validateEmailFormatMember(email);
            verifyZeroInteractions(adminService);
            verifyZeroInteractions(employeeService);
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(jwtService);
        }
    }

    @Test
    public void authenticateUserEmailNotValidFailed() {
        String email = "admin2@gn-commerce.com";
        String password = "admin2";
        mockValidateEmailMember(false, email);
        try {
            memberService.authenticateUser(email, password);
        } catch (RuntimeException e) {
            verify(validator).validateNullFieldMember(email, password);
            verify(validator).validateEmailFormatMember(email);
            verifyNoMoreInteractions(validator);
            verifyZeroInteractions(adminService);
            verifyZeroInteractions(employeeService);
            verifyZeroInteractions(jwtService);
        }
    }

    @Test
    public void authenticateUserEmailValidAndPasswordNullFailed() {
        String email = "admin2@gdn-commerce.com";
        String password = null;
        mockNullFieldMemberFound(true);
        try {
            memberService.authenticateUser(email, password);
        } catch (RuntimeException e) {
            verify(validator).validateNullFieldMember(email, password);
            verify(validator).validateEmailFormatMember(email);
            verifyNoMoreInteractions(validator);
            verifyZeroInteractions(adminService);
            verifyZeroInteractions(employeeService);
            verifyZeroInteractions(jwtService);
        }
    }

    @Test
    public void authenticateUserValidEmailAndValidPasswordEmployeeSuccess() {
        String email = "karnando@gdn-commerce.com";
        String password = "karnando";
        mockLoginAdmin(false, email, password);
        mockLoginEmployee(true, email, password);
        mockValidateEmailMember(true, email);
        mockGenerateToken();
        assertNotNull(memberService.authenticateUser(email, password));

        verify(validator).validateNullFieldMember(email, password);
        verify(validator).validateEmailFormatMember(email);
        verify(adminService).login(email, password);
        verify(employeeService).login(email, password);
        verify(jwtService).generateToken(email);
        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(adminService);
        verifyNoMoreInteractions(employeeService);
        verifyNoMoreInteractions(jwtService);
    }

    @Test
    public void authenticateUserValidEmailAndPasswordNotFoundFailed() {
        String email = "karnando@gdn-commerce.com";
        String password = "karnando";
        mockLoginAdmin(false, email, password);
        mockLoginEmployee(false, email, password);
        mockValidateEmailMember(true, email);
        try {
            memberService.authenticateUser(email, password);
        } catch (RuntimeException e) {
            verify(validator).validateEmailFormatMember(email);
            verify(validator).validateNullFieldMember(email, password);
            verify(adminService).login(email, password);
            verify(employeeService).login(email, password);
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(adminService);
            verifyNoMoreInteractions(employeeService);
            verifyZeroInteractions(jwtService);
        }
    }

    @Test
    public void getMemberRoleValidEmailEmployeeSuccess() {
        String email = "karnando@gdn-commerce.com";
        mockGetAdminByEmail(false, email);
        mockGetEmployeeByEmail(true, email);
        assertEquals("EMPLOYEE", memberService.getMemberRole(email));

        verify(adminService).getAdminByEmail(email);
        verify(employeeService).getEmployeeByEmail(email);
        verifyZeroInteractions(validator);
        verifyZeroInteractions(jwtService);
        verifyNoMoreInteractions(adminService);
        verifyNoMoreInteractions(employeeService);
    }

    @Test
    public void getMemberRoleValidEmailEmployeeNotFoundSuccess() {
        String email = "karnando@gdn-commerce.com";
        mockGetAdminByEmail(false, email);
        mockGetEmployeeByEmail(false, email);
        try {
            memberService.getMemberRole(email);
        } catch (RuntimeException e) {
            verify(adminService).getAdminByEmail(email);
            verify(employeeService).getEmployeeByEmail(email);
            verifyZeroInteractions(validator);
            verifyZeroInteractions(jwtService);
            verifyNoMoreInteractions(adminService);
            verifyNoMoreInteractions(employeeService);
        }
    }

    @Test
    public void getMemberRoleValidEmailSuperiorSuccess() {
        String email = "karnando@gdn-commerce.com";
        mockGetAdminByEmail(false, email);
        mockGetSuperiorByEmail(true, email);
        assertEquals("SUPERIOR", memberService.getMemberRole(email));

        verify(adminService).getAdminByEmail(email);
        verify(employeeService).getEmployeeByEmail(email);
        verifyZeroInteractions(validator);
        verifyZeroInteractions(jwtService);
        verifyNoMoreInteractions(adminService);
        verifyNoMoreInteractions(employeeService);
    }

    @Test
    public void getMemberRoleValidEmailAdminSuccess() {
        String email = "admin2@gdn-commerce.com";
        mockGetAdminByEmail(true, email);
        assertEquals("ADMIN", memberService.getMemberRole(email));

        verify(adminService).getAdminByEmail(email);
        verifyZeroInteractions(validator);
        verifyZeroInteractions(jwtService);
        verifyNoMoreInteractions(adminService);
        verifyZeroInteractions(employeeService);
    }

    private void mockGenerateToken() {
        when(jwtService.generateToken(anyString()))
                .thenReturn("token");
    }

    private void mockNullFieldMemberFound(boolean found) {
        when(validator.validateNullFieldMember(anyString(), anyString()))
                .thenReturn(found ? "something" : null);
    }

    private void mockValidateEmailMember(boolean valid, String email) {
        when(validator.validateEmailFormatMember(email))
                .thenReturn(valid ? true : false);
    }

    private void mockLoginAdmin(boolean valid, String email, String password) {
        when(adminService.login(email, password))
                .thenReturn(valid ? true : false);
    }

    private void mockLoginEmployee(boolean valid, String email, String password) {
        when(employeeService.login(email, password))
                .thenReturn(valid ? true : false);
    }

    private void mockGetAdminByEmail(boolean found, String email) {
        Admin admin = new Admin();
        admin.setEmail(email);
        admin.setPassword("admin");
        admin.setId("AD001");
        if (found)
            when(adminService.getAdminByEmail(email))
                    .thenReturn(admin);
        else
            when(adminService.getAdminByEmail(email))
                    .thenThrow(new AdminNotFoundException(email, "Email"));

    }

    private void mockGetEmployeeByEmail(boolean found, String email) {
        Employee employee = new Employee();
        employee.setId("EM001");
        employee.setEmail(email);
        employee.setPassword("employee");
        employee.setRole("EMPLOYEE");

        if (found)
            when(employeeService.getEmployeeByEmail(email))
                    .thenReturn(employee);
        else
            when(employeeService.getEmployeeByEmail(email))
                    .thenThrow(new EmployeeNotFoundException(email, "Email"));

    }

    private void mockGetSuperiorByEmail(boolean found, String email) {
        Employee employee = new Employee();
        employee.setId("EM002");
        employee.setEmail(email);
        employee.setPassword("superior");
        employee.setRole("SUPERIOR");
        if (found)
            when(employeeService.getEmployeeByEmail(email))
                    .thenReturn(employee);
        else
            when(employeeService.getEmployeeByEmail(email))
                    .thenThrow(new EmployeeNotFoundException(email, "Email"));
    }
}
