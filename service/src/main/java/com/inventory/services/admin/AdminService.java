package com.inventory.services.admin;

import com.inventory.models.Admin;

import javax.transaction.Transactional;

public interface AdminService {
    @Transactional
    Admin getAdminByEmail(String email);
}
