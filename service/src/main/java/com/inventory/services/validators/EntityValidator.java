package com.inventory.services.validators;

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


}
