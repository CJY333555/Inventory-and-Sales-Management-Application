package com.techshop.inventorypos.dialog;

import com.techshop.inventorypos.entity.Product;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

/**
 * Not a Spring bean by default here since dialogs are short-lived and created
 * per-use - MainApp/ProductsController instantiate it directly via FXMLLoader
 * with the standard controller factory (no @Autowired dependencies needed).
 */
public class ProductDialogController {

    @FXML private Label dialogTitle;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> categoryBox;
    @FXML private TextField priceField;
    @FXML private TextField stockField;
    @FXML private Label errorLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Stage dialogStage;
    private Product product;
    private boolean saveClicked = false;

    @FXML
    public void initialize() {
        categoryBox.setItems(FXCollections.observableArrayList(
                "Electronics", "Accessories", "Computer Parts", "Networking", "Peripherals", "General"));

        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> dialogStage.close());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /** Call this to pre-fill the form when editing an existing product. Skip it for "Add". */
    public void setProduct(Product product) {
        this.product = product;
        dialogTitle.setText("Edit Product");
        nameField.setText(product.getName());
        categoryBox.setValue(product.getCategory());
        priceField.setText(String.valueOf(product.getPrice()));
        stockField.setText(String.valueOf(product.getStockQuantity()));
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    /** Returns the Product built from form input - call after the dialog closes if isSaveClicked() is true. */
    public Product getResultProduct() {
        if (product == null) {
            product = new Product();
        }
        product.setName(nameField.getText().trim());
        product.setCategory(categoryBox.getValue());
        product.setPrice(Double.parseDouble(priceField.getText().trim()));
        product.setStockQuantity(Integer.parseInt(stockField.getText().trim()));
        return product;
    }

    private void handleSave() {
        String error = validate();
        if (error != null) {
            errorLabel.setText(error);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            return;
        }
        saveClicked = true;
        dialogStage.close();
    }

    private String validate() {
        if (nameField.getText() == null || nameField.getText().isBlank()) {
            return "Product name is required.";
        }
        if (categoryBox.getValue() == null || categoryBox.getValue().isBlank()) {
            return "Please select or enter a category.";
        }
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            if (price < 0) return "Price cannot be negative.";
        } catch (NumberFormatException ex) {
            return "Price must be a valid number, e.g. 29.90";
        }
        try {
            int stock = Integer.parseInt(stockField.getText().trim());
            if (stock < 0) return "Stock quantity cannot be negative.";
        } catch (NumberFormatException ex) {
            return "Stock quantity must be a whole number, e.g. 50";
        }
        return null;
    }
}
