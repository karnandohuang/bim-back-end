package com.inventory.services;

import com.inventory.models.entity.Admin;
import com.inventory.models.entity.Employee;
import com.inventory.services.admin.AdminService;
import com.inventory.services.employee.EmployeeService;
import com.inventory.services.security.MemberDetailsService;
import com.inventory.services.utils.exceptions.admin.AdminNotFoundException;
import com.inventory.services.utils.exceptions.employee.EmployeeNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MemberDetailsServiceTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private AdminService adminService;

    @InjectMocks
    private MemberDetailsService memberDetailsService;

    @Test
    public void loadUserByUsernameAdminMock() {
        String username = "admin";
        User expected = new User("admin", "admin123", mockAdminAuthorities());
        assertEquals(expected, memberDetailsService.loadUserByUsername(username));
    }

    @Test
    public void loadUserByUsernameEmployeeMock() {
        String username = "david@gdn-commerce.com";
        User expected = new User("david@gdn-commerce.com", "password", mockEmployeeAuthorities());
        mockEmployeeGetByEmailService(true, username, "EMPLOYEE");
        assertEquals(expected, memberDetailsService.loadUserByUsername(username));
        verify(employeeService).getEmployeeByEmail(username);
        verifyNoMoreInteractions(employeeService);
        verifyZeroInteractions(adminService);
    }

    @Test
    public void loadUserByUsernameSuperiorMock() {
        String username = "david@gdn-commerce.com";
        User expected = new User("david@gdn-commerce.com", "password", mockSuperiorAuthorities());
        mockEmployeeGetByEmailService(true, username, "SUPERIOR");
        assertEquals(expected, memberDetailsService.loadUserByUsername(username));
        verify(employeeService).getEmployeeByEmail(username);
        verifyNoMoreInteractions(employeeService);
        verifyZeroInteractions(adminService);
    }

    @Test
    public void loadUserByUsernameAdmin() {
        String username = "admin2@gdn-commerce.com";
        User expected = new User("admin2@gdn-commerce.com", "password", mockAdminAuthorities());
        mockEmployeeGetByEmailService(false, username, "ADMIN");
        mockAdminGetByEmailService(true, username);
        assertEquals(expected, memberDetailsService.loadUserByUsername(username));
        verify(adminService).getAdminByEmail(username);
        verify(employeeService).getEmployeeByEmail(username);
        verifyNoMoreInteractions(adminService);
        verifyNoMoreInteractions(employeeService);
    }

    @Test
    public void loadUserByUsernameNotFound() {
        String username = "admin2@gdn-commerce.com";
        mockEmployeeGetByEmailService(false, username, "ADMIN");
        mockAdminGetByEmailService(false, username);
        assertNull(memberDetailsService.loadUserByUsername(username));
        verify(employeeService).getEmployeeByEmail(username);
        verify(adminService).getAdminByEmail(username);
        verifyNoMoreInteractions(adminService);
        verifyNoMoreInteractions(employeeService);
    }

    private Collection<? extends GrantedAuthority> mockAdminAuthorities() {
        Collection<? extends GrantedAuthority> authorities;
        authorities = asList(() -> "ROLE_ADMIN");
        return authorities;
    }

    private void mockEmployeeGetByEmailService(boolean found, String email, String role) {
        if (found)
            when(employeeService.getEmployeeByEmail(email))
                    .thenReturn(setEmployee(email, role));
        else
            when(employeeService.getEmployeeByEmail(email))
                    .thenThrow(new EmployeeNotFoundException(email, "Email"));
    }

    private void mockAdminGetByEmailService(boolean found, String email) {
        if (found)
            when(adminService.getAdminByEmail(email))
                    .thenReturn(setAdmin(email));
        else
            when(adminService.getAdminByEmail(email))
                    .thenThrow(new AdminNotFoundException(email, "Email"));
    }

    private Admin setAdmin(String email) {
        Admin admin = new Admin();
        admin.setId("AD001");
        admin.setEmail(email);
        admin.setPassword("password");
        return admin;
    }

    private Employee setEmployee(String email, String role) {
        Employee employee = new Employee();
        employee.setId("EM040");
        employee.setEmail(email);
        employee.setPassword("password");
        employee.setRole(role);
        return employee;
    }

    private Collection<? extends GrantedAuthority> mockSuperiorAuthorities() {
        Collection<? extends GrantedAuthority> authorities;
        authorities = asList(() -> "ROLE_SUPERIOR");
        return authorities;
    }

    private Collection<? extends GrantedAuthority> mockEmployeeAuthorities() {
        Collection<? extends GrantedAuthority> authorities;
        authorities = asList(() -> "ROLE_EMPLOYEE");
        return authorities;
    }
}
