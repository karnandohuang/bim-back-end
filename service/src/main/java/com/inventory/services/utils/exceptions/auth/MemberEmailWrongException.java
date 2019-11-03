package com.inventory.services.utils.exceptions.auth;

import static com.inventory.services.utils.constants.ExceptionConstant.MEMBER_EMAIL_WRONG_FORMAT_ERROR;

public class MemberEmailWrongException extends RuntimeException {
    public MemberEmailWrongException() {
        super(MEMBER_EMAIL_WRONG_FORMAT_ERROR);
    }
}
