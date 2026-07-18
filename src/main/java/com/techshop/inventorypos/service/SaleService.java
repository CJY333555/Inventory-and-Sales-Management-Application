package com.techshop.inventorypos.service;

import com.techshop.inventorypos.entity.*;
import com.techshop.inventorypos.repository.ProductRepository;
import com.techshop.inventorypos.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    @Autowired
    public SaleService(SaleRepository saleRepository, ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    /**
     * Records a single-item sale: deducts stock from the product and persists
     * the Sale + SaleItem together in one transaction. If anything fails,
     * @Transactional rolls back both the stock deduction and the sale record -
     * this is the AOP-backed guarantee that keeps stock counts consistent.
     */
    @Transactional
    public Sale recordSale(Product product, int quantity, Member member) {
        if (product.getStockQuantity() < quantity) {
            throw new IllegalStateException("Not enough stock for " + product.getName());
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);

        Sale sale = new Sale();
        sale.setMember(member);
        sale.setSaleDate(LocalDateTime.now());

        SaleItem item = new SaleItem(sale, product, quantity, product.getPrice());
        sale.getItems().add(item);
        sale.setTotalAmount(item.getSubtotal());

        return saleRepository.save(sale);
    }

    @Transactional(readOnly = true)
    public double getTotalRevenue() {
        return saleRepository.findAll().stream()
                .mapToDouble(Sale::getTotalAmount)
                .sum();
    }
}
