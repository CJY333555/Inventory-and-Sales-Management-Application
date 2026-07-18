package com.techshop.inventorypos.repository;

import com.techshop.inventorypos.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Spring Data JPA generates the SQL for these automatically - no implementation needed.
    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByCategory(String category);
}
