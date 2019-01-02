package com.inventory.services.utils.validators;

import com.inventory.models.entity.Admin;
import org.springframework.stereotype.Component;

import static com.inventory.services.utils.constants.ValidationConstant.MEMBER_EMAIL_EMPTY;
import static com.inventory.services.utils.constants.ValidationConstant.MEMBER_PASSWORD_EMPTY;

@Component
public class AdminValidator extends MemberValidator {

    public String validateNullFieldAdmin(Admin admin) {
        if (admin.getEmail() == null)
            return MEMBER_EMAIL_EMPTY;
        else if (admin.getPassword() == null && admin.getId() == null)
            return MEMBER_PASSWORD_EMPTY;
        return null;
    }
}