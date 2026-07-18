package com.techshop.inventorypos.controller.views;

import com.techshop.inventorypos.entity.Sale;
import com.techshop.inventorypos.entity.SaleItem;
import com.techshop.inventorypos.service.SaleService;
import com.techshop.inventorypos.util.PaginationBar;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ReportsController {

    private static final int PAGE_SIZE = 30;

    @FXML private TableView<CategoryRow> categoryTable;
    @FXML private TableColumn<CategoryRow, String> colCat;
    @FXML private TableColumn<CategoryRow, Integer> colUnitsSold;
    @FXML private TableColumn<CategoryRow, Double> colRevenue;
    @FXML private HBox paginationContainer;
    @FXML private Label summaryLabel;

    private final SaleService saleService;
    private final ObservableList<CategoryRow> pageList = FXCollections.observableArrayList();
    private List<CategoryRow> allRows = List.of();
    private final PaginationBar pagination = new PaginationBar();

    @Autowired
    public ReportsController(SaleService saleService) {
        this.saleService = saleService;
    }

    @FXML
    public void initialize() {
        colCat.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().category()));
        colUnitsSold.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().units()).asObject());
        colRevenue.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().revenue()).asObject());

        categoryTable.setItems(pageList);
        paginationContainer.getChildren().add(pagination);
        pagination.setOnPageChange(this::renderCurrentPage);

        refresh();
    }

    private void refresh() {
        Map<String, int[]> unitsByCategory = new LinkedHashMap<>();
        Map<String, double[]> revenueByCategory = new LinkedHashMap<>();

        for (Sale sale : saleService.getAllSales()) {
            for (SaleItem item : sale.getItems()) {
                String cat = item.getProduct().getCategory();
                unitsByCategory.computeIfAbsent(cat, k -> new int[1])[0] += item.getQuantity();
                revenueByCategory.computeIfAbsent(cat, k -> new double[1])[0] += item.getSubtotal();
            }
        }

        allRows = unitsByCategory.keySet().stream()
                .map(cat -> new CategoryRow(cat, unitsByCategory.get(cat)[0], revenueByCategory.get(cat)[0]))
                .collect(Collectors.toList());

        pagination.setItemCount(allRows.size(), PAGE_SIZE);
        renderCurrentPage();

        summaryLabel.setText(allRows.isEmpty()
                ? "No sales recorded yet - record a sale to see category breakdowns here."
                : allRows.size() + " categories with recorded sales.");
    }

    private void renderCurrentPage() {
        pageList.setAll(PaginationBar.pageOf(allRows, pagination.getCurrentPage(), PAGE_SIZE));
    }

    public record CategoryRow(String category, int units, double revenue) {}
}
