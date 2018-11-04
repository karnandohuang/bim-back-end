package com.inventory.repositories;

import com.inventory.models.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, String> {

    @Query("SELECT r FROM Request r ORDER BY :orderBy ASC")
    List<Request> findAllByAsc(String orderBy);

    @Query("SELECT r FROM Request r ORDER BY :orderBy DESC")
    List<Request> findAllByDesc(String orderBy);

}
