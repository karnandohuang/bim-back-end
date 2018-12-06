package com.inventory.services.admin;

import com.inventory.models.Admin;
import com.inventory.repositories.AdminRepository;
import com.inventory.services.exceptions.employee.EmployeeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    @Transactional
    public Admin getAdminByEmail(String email) {
        try {
            return adminRepository.findByEmail(email);
        } catch (RuntimeException e) {
            throw new EmployeeNotFoundException(email, "Email");
        }
    }
}
