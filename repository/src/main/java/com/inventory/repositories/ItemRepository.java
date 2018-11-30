package com.inventory.repositories;

import com.inventory.models.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {

    Page<Item> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    Float countAllByNameContainingIgnoreCase(String name);

}
