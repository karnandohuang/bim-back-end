package com.inventory.services.validator;

import com.inventory.models.entity.Employee;
import com.inventory.services.utils.validators.EmployeeValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static com.inventory.services.utils.constants.ValidationConstant.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeValidatorTest {

    @InjectMocks
    private EmployeeValidator validator;

    @Test
    public void assumeRoleEmployeeAsEmployeeSuccess() {
        Employee employee = setEmployee();
        assertEquals("EMPLOYEE", validator.assumeRoleEmployee(employee, false));
    }

    @Test
    public void assumeRoleEmployeeAsSuperiorSuccess() {
        Employee employee = setEmployee();
        assertEquals("SUPERIOR", validator.assumeRoleEmployee(employee, true));
    }

    @Test
    public void validateEmailEmployeeSuccess() {
        String email = setEmployee().getEmail();
        assertTrue(validator.validateEmailFormatMember(email));
    }

    @Test
    public void validateEmailEmployeeNotValidFailed() {
        String email = "example@gn-commerce.com";
        assertFalse(validator.validateEmailFormatMember(email));
    }

    @Test
    public void validateEmailEmployeeNullFailed() {
        String email = null;
        assertFalse(validator.validateEmailFormatMember(email));
    }

    @Test
    public void validateDobValidSuccess() {
        String dob = setEmployee().getDob();
        assertTrue(validator.validateDobFormatEmployee(dob));
    }

    @Test
    public void validateDobNotValidFailed() {
        String dob = "abc";
        assertFalse(validator.validateDobFormatEmployee(dob));
    }

    @Test
    public void validateNullFieldEmployeeNotFoundSuccess() {
        Employee employee = setEmployee();
        assertNull(validator.validateNullFieldEmployee(employee));
    }

    @Test
    public void validateNullFieldEmployeeNameFoundSFailed() {
        Employee employee = setEmployee();
        employee.setName(null);
        assertEquals(EMPLOYEE_NAME_EMPTY, validator.validateNullFieldEmployee(employee));
    }

    @Test
    public void validateNullFieldEmployeeEmailFoundFailed() {
        Employee employee = setEmployee();
        employee.setEmail(null);
        assertEquals(MEMBER_EMAIL_EMPTY, validator.validateNullFieldEmployee(employee));
    }

    @Test
    public void validateNullFieldEmployeePasswordAndIdFoundFailed() {
        Employee employee = setEmployee();
        employee.setId(null);
        employee.setPassword(null);
        assertEquals(MEMBER_PASSWORD_EMPTY, validator.validateNullFieldEmployee(employee));
    }

    @Test
    public void validateNullFieldEmployeeDobFoundFailed() {
        Employee employee = setEmployee();
        employee.setDob(null);
        assertEquals(EMPLOYEE_DOB_EMPTY, validator.validateNullFieldEmployee(employee));
    }

    @Test
    public void validateNullFieldEmployeeDivisionFoundFailed() {
        Employee employee = setEmployee();
        employee.setDivision(null);
        assertEquals(EMPLOYEE_DIVISION_EMPTY, validator.validateNullFieldEmployee(employee));
    }

    @Test
    public void validateNullFieldEmployeePositionFoundFailed() {
        Employee employee = setEmployee();
        employee.setPosition(null);
        assertEquals(EMPLOYEE_POSITION_EMPTY, validator.validateNullFieldEmployee(employee));
    }

    private Employee setEmployee() {
        Employee employee = new Employee();
        employee.setId("EM001");
        employee.setName("Example");
        employee.setEmail("example@gdn-commerce.com");
        employee.setPassword("example");
        employee.setDob("17/06/1998");
        employee.setPosition("Example");
        employee.setDivision("Example");
        return employee;
    }
}
