package com.inventory.services.employee;

import com.inventory.models.Employee;
import com.inventory.repositories.EmployeeRepository;
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
public class EmployeeDetailsService implements UserDetailsService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String employeeId) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findById(employeeId).get();
        return new User(employee.getId(), employee.getPassword(), getGrantedAuthorities(employee));
    }

    private Collection<? extends GrantedAuthority> getGrantedAuthorities(Employee employee) {
        Collection<? extends GrantedAuthority> authorities;
        if (employee.getSuperiorId().equals("null")) {
            authorities = asList(() -> "ROLE_SUPERIOR", () -> "ROLE_BASIC");
        } else {
            authorities = asList(() -> "ROLE_BASIC");
        }
        return authorities;
    }
}
