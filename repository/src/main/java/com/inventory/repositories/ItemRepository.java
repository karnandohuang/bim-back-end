package com.inventory.repositories;

import com.inventory.models.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {

    Page<Item> findAllByNameContainingIgnoreCaseOrIdContainingIgnoreCase(String name, String id, Pageable pageable);

    Float countAllByNameContainingIgnoreCaseOrIdContainingIgnoreCase(String name, String id);

}
