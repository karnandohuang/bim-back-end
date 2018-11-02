package com.inventory.services;

import com.inventory.models.Employee;

import java.util.Optional;

public interface UserAuthenticationService {

    Optional<String> login(String email, String password);

    Optional<Employee> findByToken(String token);

    void logout(Employee employee);

}
