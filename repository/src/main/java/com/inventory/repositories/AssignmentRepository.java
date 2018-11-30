package com.inventory.repositories;

import com.inventory.models.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, String> {

    Page<Assignment> findAllByEmployeeId(String employeeId, Pageable pageable);

    Float countAllByEmployeeId(String employeeId);

    Float countAllByEmployeeIdAndStatus(String employeeId, String status);

    Float countAllByItemIdAndStatus(String itemId, String status);
}
