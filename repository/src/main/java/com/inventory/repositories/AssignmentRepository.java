package com.inventory.repositories;

import com.inventory.models.entity.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, String> {

    Page<Assignment> findAllByEmployeeIdAndStatusContaining(String employeeId, String status, Pageable pageable);

    Page<Assignment> findAllByStatusContaining(String status, Pageable pageable);

    Page<Assignment> findAllByEmployeeSuperiorIdAndStatusContaining(String superiorId, String status, Pageable pageable);

    Float countAllByEmployeeIdAndStatusContaining(String employeeId, String filterStatus);

    Float countAllByEmployeeIdAndStatus(String employeeId, String status);

    Float countAllByStatus(String status);

    Float countAllByItemIdAndStatus(String itemId, String status);

    Float countAllByEmployeeSuperiorIdAndStatusContaining(String superiorId, String status);
}
