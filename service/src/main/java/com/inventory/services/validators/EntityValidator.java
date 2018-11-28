package com.inventory.services.validators;

import com.inventory.models.BaseEntity;

public class EntityValidator {

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

    public boolean validateNullEntity(BaseEntity entity) {
        if (entity == null)
            return true;
        return false;
    }
}
