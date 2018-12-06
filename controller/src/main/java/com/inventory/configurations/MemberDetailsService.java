package com.inventory.configurations;

import com.inventory.mappers.ModelHelper;
import com.inventory.models.Admin;
import com.inventory.models.Employee;
import com.inventory.models.Member;
import com.inventory.services.admin.AdminService;
import com.inventory.services.employee.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static java.util.Arrays.asList;

@Service
@Component
public class MemberDetailsService implements UserDetailsService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private ModelHelper helper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member;
        try {
            member = employeeService.getEmployeeByEmail(email);
        } catch (RuntimeException e) {
            try {
                member = adminService.getAdminByEmail(email);
            } catch (RuntimeException rte) {
                return new User("", "", getGrantedAuthorities(null));
            }
        }
        return new User(member.getEmail(), member.getPassword(), getGrantedAuthorities(member));
    }

    private Collection<? extends GrantedAuthority> getGrantedAuthorities(Member member) {
        Collection<? extends GrantedAuthority> authorities;
        if (member == null)
            authorities = asList(() -> "ROLE_GUEST");
        else if (member instanceof Admin) {
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
}
