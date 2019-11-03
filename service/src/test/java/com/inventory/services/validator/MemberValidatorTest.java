package com.inventory.services.validator;

import com.inventory.services.utils.validators.MemberValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static com.inventory.services.utils.constants.ValidationConstant.MEMBER_EMAIL_EMPTY;
import static com.inventory.services.utils.constants.ValidationConstant.MEMBER_PASSWORD_EMPTY;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class MemberValidatorTest {

    @InjectMocks
    private MemberValidator validator;

    @Test
    public void validateEmailValidNotNullSuccess() {
        String email = "example@gdn-commerce.com";
        boolean valid = validator.validateEmailFormatMember(email);
        assertTrue(validator.validateEmailFormatMember(email));
    }

    @Test
    public void validateEmailNotValidNotNullFailed() {
        String email = "example@gn-commerce.com";
        assertFalse(validator.validateEmailFormatMember(email));
    }

    @Test
    public void validateEmailNullFailed() {
        String email = null;
        assertFalse(validator.validateEmailFormatMember(email));
    }

    @Test
    public void validateNullFieldNotFoundMemberSuccess() {
        String email = "example@gdn-commerce.com";
        String password = "password";
        assertNull(validator.validateNullFieldMember(email, password));
    }

    @Test
    public void validateNullFieldEmailMemberFailed() {
        String email = null;
        String password = "password";
        assertEquals(MEMBER_EMAIL_EMPTY, validator.validateNullFieldMember(email, password));
    }

    @Test
    public void validateNullFieldPasswordMemberFailed() {
        String email = "example@gdn-commerce.com";
        String password = null;
        assertEquals(MEMBER_PASSWORD_EMPTY, validator.validateNullFieldMember(email, password));
    }

    @Test
    public void validateIdFormatValidSuccess() {
        String prefix = "EM";
        String id = "EM001";
        assertTrue(validator.validateIdFormatEntity(id, prefix));
    }

    @Test
    public void validateIdFormatNotValidFailed() {
        String prefix = "EM";
        String id = "AD001";
        assertFalse(validator.validateIdFormatEntity(id, prefix));
    }

    @Test
    public void validateIdFormatNullFailed() {
        assertFalse(validator.validateIdFormatEntity(null, "EM"));
    }

    @Test
    public void validateIdFormatAllFieldNullFailed() {
        assertFalse(validator.validateIdFormatEntity(null, null));
    }
}
