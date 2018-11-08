package com.inventory.repositories;

import com.inventory.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<String, Admin> {
}
