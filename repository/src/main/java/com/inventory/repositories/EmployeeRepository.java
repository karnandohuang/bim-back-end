package com.inventory.repositories;

import com.inventory.models.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    Employee findByEmail(String email);

    Page<Employee> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Employee> findAllBySuperiorIdAndNameContainingIgnoreCase(String superiorId,
                                                                  String name,
                                                                  Pageable pageable);

    List<Employee> findAllBySuperiorId(String superiorId);

    Float countAllByNameContainingIgnoreCase(String name);

    Float countAllBySuperiorIdAndNameContainingIgnoreCase(String superiorId, String name);
}
