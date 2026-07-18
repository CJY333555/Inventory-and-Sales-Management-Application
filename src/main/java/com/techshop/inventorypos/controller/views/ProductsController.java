package com.techshop.inventorypos.controller.views;

import com.techshop.inventorypos.entity.Product;
import com.techshop.inventorypos.service.ProductService;
import com.techshop.inventorypos.util.DialogHelper;
import com.techshop.inventorypos.util.PaginationBar;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductsController {

    private static final int PAGE_SIZE = 30;

    @FXML private Button addButton, editButton, deleteButton;
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Long> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private TableColumn<Product, String> colStatus;
    @FXML private HBox paginationContainer;
    @FXML private Label statusLabel;

    private final ProductService productService;
    private final ObservableList<Product> pageList = FXCollections.observableArrayList();
    private List<Product> allProducts = List.of();
    private String activeKeyword = null; // remembers current search filter across page changes
    private final PaginationBar pagination = new PaginationBar();

    @Autowired
    public ProductsController(ProductService productService) {
        this.productService = productService;
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new SimpleLongProperty(data.getValue().getId()).asObject());
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPrice.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrice()).asObject());
        colStock.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStockQuantity()).asObject());
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().isLowStock() ? "Low Stock" : "In Stock"));

        productTable.setItems(pageList);
        paginationContainer.getChildren().add(pagination);
        pagination.setOnPageChange(this::renderCurrentPage);

        addButton.setOnAction(e -> handleAdd());
        editButton.setOnAction(e -> handleEdit());
        deleteButton.setOnAction(e -> handleDelete());

        refresh();
    }

    public void refresh() {
        allProducts = activeKeyword == null || activeKeyword.isBlank()
                ? productService.getAllProducts()
                : productService.searchByName(activeKeyword);
        pagination.setItemCount(allProducts.size(), PAGE_SIZE);
        renderCurrentPage();
    }

    private void renderCurrentPage() {
        pageList.setAll(PaginationBar.pageOf(allProducts, pagination.getCurrentPage(), PAGE_SIZE));
        statusLabel.setText(allProducts.size() + " product(s) total.");
    }

    /** Called from the top search bar in MainController. */
    public void filterByName(String keyword) {
        activeKeyword = keyword;
        pagination.resetToFirstPage();
        refresh();
    }

    private void handleAdd() {
        Optional<Product> result = DialogHelper.openProductDialog(null);
        result.ifPresent(p -> {
            productService.saveProduct(p);
            refresh();
            statusLabel.setText("Added \"" + p.getName() + "\".");
        });
    }

    private void handleEdit() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a product to edit first.");
            return;
        }
        Optional<Product> result = DialogHelper.openProductDialog(selected);
        result.ifPresent(p -> {
            productService.saveProduct(p);
            refresh();
            statusLabel.setText("Updated \"" + p.getName() + "\".");
        });
    }

    private void handleDelete() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a product to delete first.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete \"" + selected.getName() + "\"? This cannot be undone.");
        confirm.setHeaderText(null);
        confirm.showAndWait().filter(btn -> btn == ButtonType.OK).ifPresent(btn -> {
            productService.deleteProduct(selected.getId());
            refresh();
            statusLabel.setText("Product deleted.");
        });
    }
}
