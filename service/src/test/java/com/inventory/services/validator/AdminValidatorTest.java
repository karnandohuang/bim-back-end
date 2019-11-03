package com.inventory.services.validator;

import com.inventory.models.entity.Admin;
import com.inventory.services.utils.validators.AdminValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static com.inventory.services.utils.constants.ValidationConstant.MEMBER_EMAIL_EMPTY;
import static com.inventory.services.utils.constants.ValidationConstant.MEMBER_PASSWORD_EMPTY;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AdminValidatorTest {

    @InjectMocks
    private AdminValidator validator;

    @Test
    public void validateNullFieldEmailMemberFailed() {
        Admin admin = setAdmin();
        admin.setEmail(null);
        assertEquals(MEMBER_EMAIL_EMPTY, validator.validateNullFieldAdmin(admin));
    }

    @Test
    public void validateNullFieldPasswordMemberFailed() {
        Admin admin = setAdmin();
        admin.setPassword(null);
        assertEquals(MEMBER_PASSWORD_EMPTY, validator.validateNullFieldAdmin(admin));
    }

    @Test
    public void validateNullFieldNotFoundMemberSuccess() {
        Admin admin = setAdmin();
        assertNull(validator.validateNullFieldAdmin(admin));
    }

    @Test
    public void validateEmailValidNotNullSuccess() {
        String email = "example@gdn-commerce.com";
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

    private Admin setAdmin() {
        Admin admin = new Admin();
        admin.setEmail("admin@gdn-commerce.com");
        admin.setPassword("password");
        return admin;
    }
}
