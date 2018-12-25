package com.inventory.services;

import com.inventory.models.entity.Employee;
import com.inventory.repositories.EmployeeRepository;
import com.inventory.services.assignment.AssignmentService;
import com.inventory.services.employee.EmployeeServiceImpl;
import com.inventory.services.helper.PagingHelper;
import com.inventory.services.validators.EmployeeValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeServiceTest {

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeValidator validator;

    @Mock
    private PagingHelper pagingHelper;

    @Mock
    private AssignmentService assignmentService;

    @Mock
    private BCryptPasswordEncoder encoder;

    @Mock
    private GeneralMapper mapper;

    private Employee employee = new Employee();

    private void setEmployee() {
        employee.setId("EM039");
        employee.setEmail("karnando@gdn-commerce.com");
    }

    @Test
    public void getEmployeeIdValidSuccess() {
        mockValidateId(true, "EM040");
        mockFindEmployeeById(true);
        try {
            employeeService.getEmployee("EM040");
        } catch (RuntimeException e) {
            verify(employeeRepository).findById("EM040");
        }
    }

    @Test
    public void getEmployeeIdValidFailed() {
        mockValidateId(true, "EM099");
        mockFindEmployeeById(false);
        try {
            employeeService.getEmployee("EM099");
        } catch (RuntimeException e) {
            verify(employeeRepository).findById("EM099");
        }
    }

    @Test
    public void getEmployeeIdNotValidFailed() {
        mockValidateId(false, "99");
        mockFindEmployeeById(false);
        try {
            employeeService.getEmployee("99");
        } catch (RuntimeException e) {
        }
    }

    @Test
    public void getEmployeeEmailValidSuccess() {
        mockValidateEmail(true, "karnando@gdn-commerce.com");
        mockFindEmployeeByEmail(true, "karnando@gdn-commerce.com");
        try {
            employeeService.getEmployeeByEmail("karnando@gdn-commerce.com");
        } catch (RuntimeException e) {
            verify(employeeRepository).findByEmail("karnando@gdn-commerce.com");
        }
    }

    @Test
    public void getEmployeeEmailValidFailed() {
        mockValidateEmail(true, "asd@gdn-commerce.com");
        mockFindEmployeeByEmail(false, "asd@gdn-commerce.com");
        try {
            employeeService.getEmployeeByEmail("asd@gdn-commerce.com");
        } catch (RuntimeException e) {
            verify(employeeRepository).findByEmail("asd@gdn-commerce.com");
        }
    }

    @Test
    public void getEmployeeEmailNotValidFailed() {
        mockValidateEmail(false, "karnando@gn-commerce.com");
        mockFindEmployeeByEmail(false, "karnando@gn-commerce.com");
        try {
            employeeService.getEmployeeByEmail("karnando@gn-commerce.com");
        } catch (RuntimeException e) {
        }
    }

    @Test
    public void loginEmployeeEmailValidAndPasswordMatchSuccess() {
        mockValidateEmail(true, "karnando@gdn-commerce.com");
        mockFindEmployeeByEmail(true, "karnando@gdn-commerce.com");
        mockLoginEmployee(true);
        assertTrue(employeeService.login("karnando@gdn-commerce.com", "karnando"));
        verify(employeeRepository, atLeast(2)).findByEmail("karnando@gdn-commerce.com");
    }

    @Test
    public void loginEmployeeEmailValidAndPasswordNotMatchFailed() {
        mockValidateEmail(true, "karnando@gdn-commerce.com");
        mockFindEmployeeByEmail(true, "karnando@gdn-commerce.com");
        mockLoginEmployee(false);
        assertFalse(employeeService.login("karnando@gdn-commerce.com", "karnando"));
        verify(employeeRepository, atLeast(2)).findByEmail("karnando@gdn-commerce.com");
    }

    @Test
    public void loginEmployeeEmailValidNotFoundFailed() {
        mockValidateEmail(true, "karnandoa@gdn-commerce.com");
        mockFindEmployeeByEmail(false, "karnandoa@gdn-commerce.com");
        try {
            employeeService.login("karnandoa@gdn-commerce.com", "karnando");
        } catch (RuntimeException e) {
            verify(employeeRepository).findByEmail("karnandoa@gdn-commerce.com");
        }
    }

    private void mockFindEmployeeById(boolean found) {
        if (found)
            when(employeeRepository.findById("EM040"))
                    .thenReturn(Optional.ofNullable(employee));
        else
            when(employeeRepository.findById("EM040"))
                    .thenReturn(null);
    }

    private void mockFindEmployeeByEmail(boolean found, String email) {
        if (found)
            when(employeeRepository.findByEmail(email))
                    .thenReturn(employee);
        else
            when(employeeRepository.findByEmail(email))
                    .thenReturn(null);
    }

    private void mockLoginEmployee(boolean valid) {
        when(employeeService.login("karnando@gdn-commerce.com", "karnando"))
                .thenReturn(valid ? true : false);
    }

    private void mockValidateId(boolean valid, String id) {
        when(validator.validateIdFormatEntity(id, "EM"))
                .thenReturn(valid ? true : false);
    }

    private void mockValidateEmail(boolean valid, String email) {
        when(validator.validateEmailFormatMember(email))
                .thenReturn(valid ? true : false);
    }

    private void mockMatchPassword(boolean match, String password, String passedPassword) {
        when(encoder.matches(passedPassword, password))
                .thenReturn(match ? true : false);
    }

    private void mockSaveEmployee() {
        when(employeeRepository.save(employee)).thenReturn(employee);
    }
}
