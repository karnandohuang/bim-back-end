package com.inventory.services;

import com.inventory.models.Paging;
import com.inventory.models.entity.Admin;
import com.inventory.repositories.AdminRepository;
import com.inventory.services.admin.AdminServiceImpl;
import com.inventory.services.utils.GeneralMapper;
import com.inventory.services.utils.validators.AdminValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
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

    private Paging paging = new Paging();
    private List<Admin> adminList = mock(ArrayList.class);
    private Page<Admin> adminPageList = mock(Page.class);

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
    public void findAdminAndPagingAscSuccess(){
        setPaging("asc");

        mockAdminList();
        mockCountTotalRecord();
        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);

        List<Admin> listOfAdmin = adminService.getAdminList(paging);;

        verify(adminRepository).findAll(pageArgument.capture());
        verify(adminRepository).count();
    }

    @Test
    public void findAdminAndPagingDescSuccess(){
        setPaging("desc");

        mockAdminList();
        mockCountTotalRecord();
        ArgumentCaptor<Pageable> pageArgument = ArgumentCaptor.forClass(Pageable.class);

        List<Admin> listOfAdmin = adminService.getAdminList(paging);;

        verify(adminRepository).findAll(pageArgument.capture());
        verify(adminRepository).count();
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

    @Test
    public void insertAdminEmailValidPasswordNotNullSuccess() {
        Admin admin = setAdminWithEmailAndPassword("admin@gdn-commerce.com", "admin");

        mockFindAdminByEmail(false, "admin@gdn-commerce.com");
        mockValidateEmail(true, "admin@gdn-commerce.com");
        adminService.saveAdmin(admin);

        verify(validator).validateNullFieldAdmin(admin);
        verify(validator).validateEmailFormatMember("admin@gdn-commerce.com");
        verify(adminRepository).findByEmail("admin@gdn-commerce.com");
        verify(adminRepository).save(admin);
        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(adminRepository);
    }

    @Test
    public void insertAdminEmailValidPasswordNullFailed() {
        Admin admin = setAdminWithEmailAndPassword("admin@gdn-commerce.com", null);

        mockFindAdminByEmail(false, "admin@gdn-commerce.com");
        mockValidateEmail(true, "admin@gdn-commerce.com");
        mockNullFieldAdminFound(true);

        try {
            adminService.saveAdmin(admin);
        } catch (RuntimeException e){
            verify(validator).validateNullFieldAdmin(admin);
            verify(validator).validateEmailFormatMember("admin@gdn-commerce.com");
            verify(adminRepository).findByEmail("admin@gdn-commerce.com");
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(adminRepository);
        }
    }

    @Test
    public void insertAdminEmailNotValidPasswordNotNullFailed(){
        Admin admin = setAdminWithEmailAndPassword("admin@gn-commerce.com", null);

        mockFindAdminByEmail(false, "admin@gn-commerce.com");
        mockValidateEmail(false, "admin@gn-commerce.com");

        try {
            adminService.saveAdmin(admin);
        } catch (RuntimeException e){
            verify(validator).validateNullFieldAdmin(admin);
            verify(validator).validateEmailFormatMember("admin@gn-commerce.com");
            verify(adminRepository).findByEmail("admin@gn-commerce.com");
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(adminRepository);
        }
    }

    @Test
    public void insertAdminEmailValidFoundPasswordNotNullFailed(){
        Admin admin = setAdminWithEmailAndPassword("admin@gdn-commerce.com", "admin");

        mockFindAdminByEmail(true, "admin@gdn-commerce.com");
        mockValidateEmail(true, "admin@gdn-commerce.com");

        try {
            adminService.saveAdmin(admin);
        } catch (RuntimeException e){
            verify(validator).validateNullFieldAdmin(admin);
            verify(validator).validateEmailFormatMember("admin@gdn-commerce.com");
            verify(adminRepository).findByEmail("admin@gdn-commerce.com");
            verifyNoMoreInteractions(validator);
            verifyNoMoreInteractions(adminRepository);
        }
    }

    @Test
    public void editAdminEmailValidPasswordNotNullSuccess(){
        Admin admin = setAdminWithIdEmailAndPassword("AD002","admin@gdn-commerce.com", "admin");
        mockFindAdminById(true, "AD002");
        mockFindAdminByEmail(true,"admin@gdn-commerce.com");
        mockValidateEmail(true, "admin@gdn-commerce.com");
        mockValidateId(true, "AD002");
        mockMapAdmin(false, admin);
        adminService.saveAdmin(admin);

        verify(validator).validateEmailFormatMember("admin@gdn-commerce.com");
        verify(validator).validateIdFormatEntity("AD002", "AD");
        verify(validator).validateNullFieldAdmin(admin);
        verify(adminRepository).findByEmail("admin@gdn-commerce.com");
        verify(adminRepository).findById("AD002");
        verify(adminRepository).save(admin);
        verifyNoMoreInteractions(validator);
        verifyNoMoreInteractions(adminRepository);
    }

    @Test
    public void deleteAdminByListOfIdSuccess(){
        List<String> ids = new ArrayList<>();
        ids.add("AD001");

        mockValidateId(true, ids.get(0));
        mockFindAdminById(true, ids.get(0));
        assertEquals("Delete success!", adminService.deleteAdmin(ids));

        verify(validator).validateIdFormatEntity(ids.get(0), "AD");
        verify(adminRepository).findById(ids.get(0));
        verify(adminRepository).deleteById(ids.get(0));
        verifyNoMoreInteractions(adminRepository);
        verifyNoMoreInteractions(validator);
    }

    @Test public void deleteAdminByListOfIdNotFoundFailed(){
        List<String> ids = new ArrayList<>();
        ids.add("AD001");

        mockValidateId(true, ids.get(0));
        mockFindAdminById(false, ids.get(0));

        try {
            adminService.deleteAdmin(ids);
        } catch (RuntimeException e){
            verify(validator).validateIdFormatEntity(ids.get(0), "AD");
            verify(adminRepository).findById(ids.get(0));
            verifyNoMoreInteractions(adminRepository);
            verifyNoMoreInteractions(validator);
        }
    }

    @Test
    public void deleteAdminByListOfIdNotValidNotFoundFailed(){
        List<String> ids = new ArrayList<>();
        ids.add("AE001");

        mockValidateId(false, ids.get(0));
        mockFindAdminById(false, ids.get(0));

        try {
            adminService.deleteAdmin(ids);
        } catch (RuntimeException e){
            verify(validator).validateIdFormatEntity(ids.get(0), "AD");
            verifyZeroInteractions(adminRepository);
            verifyNoMoreInteractions(validator);
        }
    }

    private Admin setAdminWithIdEmailAndPassword(String id, String email, String password) {
        Admin admin = new Admin();
        admin.setId(id);
        admin.setEmail(email);
        admin.setPassword(password);
        return admin;
    }

    private Admin setAdminWithEmailAndPassword(String email, String password) {
        Admin admin = new Admin();
        admin.setEmail(email);
        admin.setPassword(password);
        return admin;
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

    private void mockNullFieldAdminFound(boolean found) {
        when(validator.validateNullFieldAdmin(any(Admin.class)))
                .thenReturn(found ? "something" : null);
    }

    private void mockMapAdmin(boolean isNull, Admin a){
        when(mapper.map(a, Admin.class))
                .thenReturn(isNull ? null : a);
    }

    private void mockAdminList(){
        when(adminRepository.findAll(any(Pageable.class)))
                .thenReturn(adminPageList);

    }

    private void mockCountTotalRecord(){
        when(adminRepository.count())
                .thenReturn((long) 2);
    }

    private void setPaging(String sortedType){
        this.paging.setPageNumber(1);
        this.paging.setPageSize(3);
        this.paging.setSortedBy("updatedDate");
        this.paging.setSortedType(sortedType);
    }
}
