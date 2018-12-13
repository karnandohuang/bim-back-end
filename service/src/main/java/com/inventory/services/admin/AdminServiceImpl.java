package com.inventory.services.admin;

import com.inventory.models.Admin;
import com.inventory.repositories.AdminRepository;
import com.inventory.services.exceptions.employee.EmployeeNotFoundException;
import com.inventory.services.validators.EmployeeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private EmployeeValidator validator;

    @Override
    @Transactional
    public Admin getAdminByEmail(String email) {
        try {
            return adminRepository.findByEmail(email);
        } catch (RuntimeException e) {
            throw new EmployeeNotFoundException(email, "Email");
        }
    }

    @Override
    public Boolean login(String email, String password) {
        boolean isEmailValid = validator.validateEmailFormatMember(email);
        if (!isEmailValid)
            return false;
        Admin admin;
        try {
            admin = adminRepository.findByEmail(
                    email);
        } catch (Exception e) {
            return false;
        }
        if (encoder.matches(password, admin.getPassword()))
            return true;
        else
            return false;
    }
}
