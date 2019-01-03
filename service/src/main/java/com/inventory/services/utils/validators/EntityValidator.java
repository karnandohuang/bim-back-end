package com.inventory.services.utils.validators;

public abstract class EntityValidator {

    public boolean validateIdFormatEntity(String id, String prefix) {
        if ((id != null && !id.startsWith(prefix)) || id == null || prefix == null)
            return false;
        return true;
    }
}
