package com.inventory.repositories;

import com.inventory.models.Employee;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    Employee findByEmail(String email);

    List<Employee> findAllByNameContaining(String name, Sort sort);
}
