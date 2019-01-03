package com.inventory.services.utils.validators;

import com.inventory.models.entity.Employee;
import org.springframework.stereotype.Component;

import static com.inventory.services.utils.constants.ValidationConstant.*;

@Component
public class EmployeeValidator extends EntityValidator {

    public String validateNullFieldEmployee(Employee employee) {
        if (employee.getName() == null)
            return EMPLOYEE_NAME_EMPTY;
        else if (employee.getEmail() == null)
            return MEMBER_EMAIL_EMPTY;
        else if (employee.getPassword() == null && employee.getId() == null)
            return MEMBER_PASSWORD_EMPTY;
        else if (employee.getDob() == null)
            return EMPLOYEE_DOB_EMPTY;
        else if (employee.getDivision() == null)
            return EMPLOYEE_DIVISION_EMPTY;
        else if (employee.getPosition() == null)
            return EMPLOYEE_POSITION_EMPTY;
        return null;
    }

    public boolean validateDobFormatEmployee(String dob) {
        if (dob.charAt(2) == '/' && dob.charAt(5) == '/')
            return true;
        else
            return false;
    }

    public String assumeRoleEmployee(Employee employee, boolean isHavingSubordinate) {
        if (employee.getId() != null && isHavingSubordinate)
            return "SUPERIOR";
        else
            return "EMPLOYEE";
    }

    public boolean validateEmailFormatMember(String email) {
        if (email != null && email.endsWith("@gdn-commerce.com"))
            return true;
        return false;
    }


}
