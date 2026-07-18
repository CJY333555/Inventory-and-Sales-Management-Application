package com.techshop.inventorypos.repository;

import com.techshop.inventorypos.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);
}
