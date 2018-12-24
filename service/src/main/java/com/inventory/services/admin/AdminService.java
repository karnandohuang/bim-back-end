package com.inventory.services.admin;

import com.inventory.models.Paging;
import com.inventory.models.entity.Admin;
import com.inventory.services.exceptions.admin.AdminNotFoundException;

import javax.transaction.Transactional;
import java.util.List;

public interface AdminService {
    @Transactional
    Admin getAdminByEmail(String email);

    @Transactional
    Admin getAdmin(String id) throws AdminNotFoundException;

    Boolean login(String email, String password);

    @Transactional
    List<Admin> getAdminList(Paging paging);

    @Transactional
    Admin saveAdmin(Admin request) throws RuntimeException;

    @Transactional
    String deleteAdmin(List<String> ids) throws RuntimeException;
}
