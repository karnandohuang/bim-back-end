package com.inventory.configurations;

import com.inventory.models.Admin;
import com.inventory.models.Employee;
import com.inventory.models.Member;
import com.inventory.services.admin.AdminService;
import com.inventory.services.employee.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static java.util.Arrays.asList;

@Component
@ComponentScan("com.inventory.services")
public class MemberDetailsService implements UserDetailsService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AdminService adminService;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Member member;
        if (name.equals("admin")) {
            return new User("admin", "admin123", getAdminAuthorities());
        } else {
            try {
                member = employeeService.getEmployeeByEmail(name);
            } catch (RuntimeException e) {
                member = null;
            }

            if (member == null)
                try {
                    member = adminService.getAdminByEmail(name);
                } catch (RuntimeException rte) {
                    return new User("", "", getGrantedAuthorities(null));
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
