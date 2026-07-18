package com.techshop.inventorypos.controller.views;

import com.techshop.inventorypos.entity.Product;
import com.techshop.inventorypos.service.ProductService;
import com.techshop.inventorypos.service.SaleService;
import com.techshop.inventorypos.util.PaginationBar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DashboardController {

    private static final int PAGE_SIZE = 15;

    @FXML private Label totalProductsLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label totalSalesLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private ListView<String> lowStockList;
    @FXML private HBox paginationContainer;

    private final ProductService productService;
    private final SaleService saleService;

    private final ObservableList<String> pageList = FXCollections.observableArrayList();
    private List<String> allLowStockLines = List.of();
    private final PaginationBar pagination = new PaginationBar();

    @Autowired
    public DashboardController(ProductService productService, SaleService saleService) {
        this.productService = productService;
        this.saleService = saleService;
    }

    @FXML
    public void initialize() {
        lowStockList.setItems(pageList);
        paginationContainer.getChildren().add(pagination);
        pagination.setOnPageChange(this::renderCurrentPage);

        refresh();
    }

    public void refresh() {
        List<Product> products = productService.getAllProducts();
        long lowStockCount = products.stream().filter(Product::isLowStock).count();

        totalProductsLabel.setText(String.valueOf(products.size()));
        lowStockLabel.setText(String.valueOf(lowStockCount));
        totalSalesLabel.setText(String.valueOf(saleService.getAllSales().size()));
        totalRevenueLabel.setText(String.format("%.2f", saleService.getTotalRevenue()));

        allLowStockLines = products.stream()
                .filter(Product::isLowStock)
                .map(p -> p.getName() + " - only " + p.getStockQuantity() + " left")
                .toList();

        pagination.setItemCount(allLowStockLines.size(), PAGE_SIZE);
        renderCurrentPage();
    }

    private void renderCurrentPage() {
        pageList.setAll(PaginationBar.pageOf(allLowStockLines, pagination.getCurrentPage(), PAGE_SIZE));
    }
}
