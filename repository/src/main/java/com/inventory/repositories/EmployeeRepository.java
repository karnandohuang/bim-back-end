package com.inventory.repositories;

import com.inventory.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    Employee findByEmailEqualsAndPasswordEquals(String email, String password);
}
