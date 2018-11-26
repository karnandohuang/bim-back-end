package com.inventory.services.validators;

import com.inventory.models.BaseEntity;
import com.inventory.models.Employee;

public interface EntityValidator {
    String validateNullFieldEmployee(Employee employee);

    String assumeRoleEmployee(String superiorId);

    boolean validateEmailFormatEmployee(String email);

    boolean validateIdFormatEntity(String id, String prefix);

    boolean validateNullEntity(BaseEntity entity);
}
