package com.inventory.services.admin;

import com.inventory.models.Paging;
import com.inventory.models.entity.Admin;

import javax.transaction.Transactional;
import java.util.List;

public interface AdminService {
    @Transactional
    Admin getAdminByEmail(String email);

    @Transactional
    Admin getAdmin(String id);

    Boolean login(String email, String password);

    @Transactional
    List<Admin> getAdminList(Paging paging);

    @Transactional
    Admin saveAdmin(Admin request);

    @Transactional
    String deleteAdmin(List<String> ids);
}
