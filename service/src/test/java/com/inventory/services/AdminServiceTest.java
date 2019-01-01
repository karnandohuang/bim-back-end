package com.inventory.services;

import com.inventory.models.entity.Admin;
import com.inventory.repositories.AdminRepository;
import com.inventory.services.admin.AdminServiceImpl;
import com.inventory.services.utils.GeneralMapper;
import com.inventory.services.utils.validators.AdminValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @Mock
    private AdminValidator validator;

    @Mock
    private GeneralMapper mapper;

    @InjectMocks
    private AdminServiceImpl adminService;


    @Test
    public void getAdminIdValidSuccess() {
        Admin admin = setAdminWithId();
        mockValidateId(true, "AD002");
        mockFindAdminById(true, "AD002");
        Admin a = adminService.getAdmin("AD002");
        assertEquals(admin, a);

        verify(validator).validateIdFormatEntity(anyString(), anyString());
        verify(adminRepository).findById("AD002");
        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(adminRepository);
    }

    @Test
    public void getAdminIdValidNotFound() {
        Admin admin = setAdminWithId();
        mockValidateId(true, "AD002");
        mockFindAdminById(false, "AD002");
        try {
            Admin a = adminService.getAdmin("AD002");
        } catch (RuntimeException e) {
            verify(validator).validateIdFormatEntity(anyString(), anyString());
            verify(adminRepository).findById("AD002");
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(adminRepository);
        }
    }

    @Test
    public void getAdminIdNotValidFailed() {
        Admin admin = setAdminWithId();
        mockValidateId(false, "AD002");
        try {
            Admin a = adminService.getAdmin("AD002");
        } catch (RuntimeException e) {
            verify(validator).validateIdFormatEntity(anyString(), anyString());
            verifyNoMoreInteractions(validator);
            verifyZeroInteractions(adminRepository);
        }
    }

    @Test
    public void getAdminEmailValidSuccess() {
        Admin admin = setAdminWithId();
        mockValidateEmail(true, admin.getEmail());
        mockFindAdminByEmail(true, admin.getEmail());
        Admin a = adminService.getAdminByEmail(admin.getEmail());
        assertEquals(admin, a);

        verify(validator).validateEmailFormatMember(admin.getEmail());
        verify(adminRepository).findByEmail(admin.getEmail());
        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(adminRepository);
    }

    @Test
    public void getAdminEmailNotValidFailed() {
        Admin admin = setAdminWithId();
        mockValidateEmail(false, admin.getEmail());
        try {
            Admin a = adminService.getAdminByEmail(admin.getEmail());
        } catch (RuntimeException e) {

            verify(validator).validateEmailFormatMember(admin.getEmail());
            verifyNoMoreInteractions(validator);
            verifyZeroInteractions(adminRepository);
        }
    }

    @Test
    public void getAdminEmailValidNotFound() {
        Admin admin = setAdminWithId();
        mockValidateEmail(true, admin.getEmail());
        mockFindAdminByEmail(false, admin.getEmail());
        try {
            Admin a = adminService.getAdminByEmail(admin.getEmail());
        } catch (RuntimeException e) {
            verify(adminRepository).findByEmail(admin.getEmail());
            verify(validator).validateEmailFormatMember(admin.getEmail());
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(adminRepository);
        }
    }

    @Test
    public void loginAdminEmailValidAndPasswordMatchSuccess() {
        mockValidateEmail(true, "admin2@gdn-commerce.com");
        mockFindAdminByEmail(true, "admin2@gdn-commerce.com");
        mockLoginAdmin(true);
        assertTrue(adminService.login("admin2@gdn-commerce.com", "admin2"));
        verify(adminRepository, times(2)).findByEmail("admin2@gdn-commerce.com");
        verifyNoMoreInteractions(adminRepository);
    }

    @Test
    public void loginAdminEmailValidAndPasswordNotMatchFailed() {
        mockValidateEmail(true, "admin2@gdn-commerce.com");
        mockFindAdminByEmail(true, "admin2@gdn-commerce.com");
        mockLoginAdmin(false);
        assertFalse(adminService.login("admin2@gdn-commerce.com", "a"));
        verify(adminRepository, times(2)).findByEmail("admin2@gdn-commerce.com");
        verifyNoMoreInteractions(adminRepository);
    }

    @Test
    public void loginAdminEmailValidNotFoundFailed() {
        mockValidateEmail(true, "admin@gdn-commerce.com");
        mockFindAdminByEmail(false, "admin@gdn-commerce.com");
        try {
            adminService.login("admin@gdn-commerce.com", "admin");
        } catch (RuntimeException e) {
            verify(adminRepository).findByEmail("admin@gdn-commerce.com");
            verifyNoMoreInteractions(adminRepository);
        }
    }

    @Test
    public void loginAdminEmailNotValidFailed() {
        mockValidateEmail(false, "admin@gn-commerce.com");
        try {
            adminService.login("admin@gn-commerce.com", "admin");
        } catch (RuntimeException e) {
            verify(validator).validateEmailFormatMember("admin@gn-commerce.com");
            verifyNoMoreInteractions(validator);
            verifyZeroInteractions(adminRepository);
        }
    }

    private Admin setAdminWithId() {
        Admin admin = new Admin();
        admin.setId("AD002");
        admin.setEmail("admin2@gdn-commerce.com");
        return admin;
    }

    private void mockFindAdminById(boolean found, String id) {
        Admin employee = setAdminWithId();
        employee.setPassword(encoder.encode("admin2"));
        if (found)
            when(adminRepository.findById(id))
                    .thenReturn(Optional.ofNullable(employee));
        else
            when(adminRepository.findById(id))
                    .thenReturn(null);
    }

    private void mockFindAdminByEmail(boolean found, String email) {
        Admin admin = setAdminWithId();
        admin.setPassword(encoder.encode("admin2"));
        when(adminRepository.findByEmail(email))
                .thenReturn(found ? admin : null);
    }

    private void mockValidateId(boolean valid, String id) {
        when(validator.validateIdFormatEntity(id, "AD"))
                .thenReturn(valid ? true : false);
    }

    private void mockValidateEmail(boolean valid, String email) {
        when(validator.validateEmailFormatMember(email))
                .thenReturn(valid ? true : false);
    }

    private void mockLoginAdmin(boolean valid) {
        when(adminService.login("admin2@gdn-commerce.com", "admin2"))
                .thenReturn(valid);
    }
}
