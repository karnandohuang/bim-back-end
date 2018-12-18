package com.inventory.services.validators;

import com.inventory.models.Admin;
import org.springframework.stereotype.Component;

import static com.inventory.services.constants.ValidationConstant.MEMBER_EMAIL_EMPTY;
import static com.inventory.services.constants.ValidationConstant.MEMBER_PASSWORD_EMPTY;

@Component
public class AdminValidator extends EntityValidator {

    public String validateNullFieldAdmin(Admin admin) {
        if (admin.getEmail() == null)
            return MEMBER_EMAIL_EMPTY;
        else if (admin.getPassword() == null && admin.getId() == null)
            return MEMBER_PASSWORD_EMPTY;
        return null;
    }

    public boolean validateEmailFormatMember(String email) {
        if (email != null && email.endsWith("@gdn-commerce.com"))
            return true;
        return false;
    }
}
