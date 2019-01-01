package com.inventory.services.utils.validators;

import static com.inventory.services.utils.constants.ValidationConstant.MEMBER_EMAIL_EMPTY;
import static com.inventory.services.utils.constants.ValidationConstant.MEMBER_PASSWORD_EMPTY;

public class MemberValidator extends EntityValidator {

    public boolean validateEmailFormatMember(String email) {
        if (email != null && email.endsWith("@gdn-commerce.com"))
            return true;
        return false;
    }

    public String validateNullFieldMember(String email, String password) {
        if (email == null)
            return MEMBER_EMAIL_EMPTY;
        else if (password == null)
            return MEMBER_PASSWORD_EMPTY;
        return null;
    }
}
