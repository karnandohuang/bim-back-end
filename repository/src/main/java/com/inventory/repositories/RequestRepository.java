package com.inventory.repositories;

import com.inventory.models.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, String> {

    Page<Request> findAllByEmployeeId(String employeeId, Pageable pageable);

    Float countAllByEmployeeId(String employeeId);

    Float countAllByEmployeeIdAndStatus(String employeeId, String status);
}
