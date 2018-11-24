//package com.inventory.services.employee;
//
//import com.inventory.models.Employee;
//import com.inventory.repositories.EmployeeRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import java.util.Collection;
//
//import static java.util.Arrays.asList;
//
//@Service
//@Component
//public class EmployeeDetailsService implements UserDetailsService {
//
//    @Autowired
//    private EmployeeRepository employeeRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        User user = null;
//        try {
//            user = employeeRepository.findByEmail(email);
//        }catch(EmployeeNotFoundException){
//            user = adminRepository.findByEmail(email);
//        }
//        return new User(user.getEmail(), user.getPassword(), getGrantedAuthorities(user));
//    }
//
//    private Collection<? extends GrantedAuthority> getGrantedAuthorities(User user) {
//        Collection<? extends GrantedAuthority> authorities;
//        if (user instanceof Admin) {
//            authorities = asList(() -> "ROLE_ADMIN");
//        } else {
//            authorities = asList(() -> "ROLE_BASIC");
//        }
//        return authorities;
//    }
//}
