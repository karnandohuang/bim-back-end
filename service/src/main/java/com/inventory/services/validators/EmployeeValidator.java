package com.inventory.services.validators;

import com.inventory.models.Employee;
import org.springframework.stereotype.Component;

import static com.inventory.services.ValidationConstant.*;

@Component
public class EmployeeValidator extends EntityValidator {

    public String validateNullFieldEmployee(Employee employee) {
        if (employee.getName() == null)
            return EMPLOYEE_NAME_EMPTY;
        if (employee.getEmail() == null)
            return EMPLOYEE_EMAIL_EMPTY;
        if (employee.getPassword() == null && employee.getId() == null)
            return EMPLOYEE_PASSWORD_EMPTY;
        if (employee.getDob() == null)
            return EMPLOYEE_DOB_EMPTY;
        if (employee.getDivision() == null)
            return EMPLOYEE_DIVISION_EMPTY;
        if (employee.getPosition() == null)
            return EMPLOYEE_POSITION_EMPTY;
        return null;
    }

    public boolean isDobValid(String dob) {
        if (dob.charAt(2) != '/')
            return false;
        else if (dob.charAt(5) != '/')
            return false;
        return true;
    }

    public String assumeRoleEmployee(String superiorId) {
        if (superiorId.equals("null"))
            return "SUPERIOR";
        else
            return "EMPLOYEE";
    }

    public boolean validateEmailFormatEmployee(String email) {
        if (email == null)
            return false;
        else {
            if (!email.endsWith("@gdn-commerce.com"))
                return false;
            return true;
        }
    }



}
