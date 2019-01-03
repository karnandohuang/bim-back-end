package com.inventory.services.security;

import com.inventory.models.abstract_entity.Member;
import com.inventory.models.entity.Admin;
import com.inventory.models.entity.Employee;
import com.inventory.services.admin.AdminService;
import com.inventory.services.employee.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static java.util.Arrays.asList;

@Component
public class MemberDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(MemberDetailsService.class);
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private AdminService adminService;

    @Override
    public UserDetails loadUserByUsername(String name) {
        Member member;
        if (name.equals("admin")) {
            return new User("admin", "admin123", getAdminAuthorities());
        } else {
            try {
                logger.info("checking if email is of an employee...");
                member = employeeService.getEmployeeByEmail(name);
            } catch (RuntimeException e) {
                member = null;
            }

            if (member == null) {
                logger.info("email is not of employee. checking admin...");
                try {
                    member = adminService.getAdminByEmail(name);
                } catch (RuntimeException rte) {
                    logger.info("email is not of admin. returning empty user!");
                    return null;
                }
            }
            return new User(member.getEmail(), member.getPassword(), getGrantedAuthorities(member));
        }
    }

    private Collection<? extends GrantedAuthority> getGrantedAuthorities(Member member) {
        Collection<? extends GrantedAuthority> authorities;
        if (member instanceof Admin) {
            authorities = asList(() -> "ROLE_ADMIN");
        } else {
            Employee employee = (Employee) member;
            if (employee.getRole().equals("SUPERIOR"))
                authorities = asList(() -> "ROLE_SUPERIOR");
            else
                authorities = asList(() -> "ROLE_EMPLOYEE");
        }
        return authorities;
    }

    private Collection<? extends GrantedAuthority> getAdminAuthorities() {
        Collection<? extends GrantedAuthority> authorities;
        authorities = asList(() -> "ROLE_ADMIN");
        return authorities;
    }
}
