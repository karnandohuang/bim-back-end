package com.inventory.repositories;

import com.inventory.models.Item;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {

    List<Item> findAllByNameContainingIgnoreCase(String name, Sort sort);

    Item findBySku(String sku);

}
