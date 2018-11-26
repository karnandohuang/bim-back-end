package com.inventory.services.validators;

import com.inventory.models.BaseEntity;
import com.inventory.models.Employee;
import org.springframework.stereotype.Service;

import static com.inventory.services.ValidationConstant.*;

@Service
public class EmployeeValidatorImpl implements EntityValidator {

    @Override
    public String validateNullFieldEmployee(Employee employee) {
        if (employee.getName() == null)
            return EMPLOYEE_NAME_EMPTY;
        if (employee.getEmail() == null)
            return EMPLOYEE_EMAIL_EMPTY;
        if (employee.getPassword() == null)
            return EMPLOYEE_PASSWORD_EMPTY;
        if (employee.getDob() == null)
            return EMPLOYEE_DOB_EMPTY;
        if (employee.getDivision() == null)
            return EMPLOYEE_DIVISION_EMPTY;
        if (employee.getPosition() == null)
            return EMPLOYEE_POSITION_EMPTY;
        return null;
    }

    @Override
    public String assumeRoleEmployee(String superiorId) {
        if (superiorId.equals("null"))
            return "SUPERIOR";
        else
            return "EMPLOYEE";
    }

    @Override
    public boolean validateEmailFormatEmployee(String email) {
        if (email == null)
            return false;
        else {
            if (!email.endsWith("@gdn-commerce.com"))
                return false;
            return true;
        }
    }

    @Override
    public boolean validateIdFormatEntity(String id, String prefix) {
        if (id == null)
            return true;
        else {
            if (!id.startsWith(prefix)) {
                return false;
            }
            return true;
        }
    }

    @Override
    public boolean validateNullEntity(BaseEntity entity) {
        if (entity == null)
            return true;
        return false;
    }

}
