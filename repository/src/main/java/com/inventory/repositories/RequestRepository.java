package com.inventory.repositories;

import com.inventory.models.Request;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, String> {

    List<Request> findAllByEmployeeId(String employeeId, Sort sort);
}
