package com.inventory.repositories;

import com.inventory.models.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, String> {
}
