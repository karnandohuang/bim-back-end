package com.inventory.repositories;

import com.inventory.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {

    @Query("SELECT i FROM Item i WHERE i.name LIKE %:name% ORDER BY :orderBy DESC")
    List<Item> findAllByNameIsContainingDesc(String name, String orderBy);

    @Query("SELECT i FROM Item i WHERE i.name LIKE %:name% ORDER BY :orderBy ASC")
    List<Item> findAllByNameIsContainingAsc(String name, String orderBy);

}
