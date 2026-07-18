package com.techshop.inventorypos.controller;

import com.techshop.inventorypos.controller.views.ProductsController;
import com.techshop.inventorypos.util.AppVersion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * The app "shell": sidebar + top bar stay fixed, and this controller swaps
 * whichever page view sits inside contentArea. Each page (Dashboard, Products,
 * Sales, Reports) is its own FXML + @Component controller, loaded on demand
 * through Spring's ApplicationContext so @Autowired still works inside them.
 */
@Component
public class MainController {

    @FXML private TextField searchField;
    @FXML private Button navDashboard, navProducts, navMembers, navSales, navReports;
    @FXML private StackPane contentArea;
    @FXML private Label versionLabel;

    private final ApplicationContext springContext;
    private ProductsController activeProductsController; // kept for live search filtering

    @Autowired
    public MainController(ApplicationContext springContext) {
        this.springContext = springContext;
    }

    @FXML
    public void initialize() {
        versionLabel.setText("v" + AppVersion.CURRENT);

        navDashboard.setOnAction(e -> showView("/fxml/views/dashboard_view.fxml", navDashboard));
        navProducts.setOnAction(e -> showView("/fxml/views/products_view.fxml", navProducts));
        navMembers.setOnAction(e -> showView("/fxml/views/members_view.fxml", navMembers));
        navSales.setOnAction(e -> showView("/fxml/views/sales_view.fxml", navSales));
        navReports.setOnAction(e -> showView("/fxml/views/reports_view.fxml", navReports));

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (activeProductsController != null) {
                activeProductsController.filterByName(newVal);
            }
        });

        // Default landing page
        showView("/fxml/views/dashboard_view.fxml", navDashboard);
    }

    private void showView(String fxmlPath, Button activeButton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(springContext::getBean);
            Parent view = loader.load();

            Object controller = loader.getController();
            activeProductsController = (controller instanceof ProductsController pc) ? pc : null;

            contentArea.getChildren().setAll(view);
            highlightActiveNav(activeButton);
        } catch (Exception e) {
            // Catching broadly (not just IOException) so a bug in a page's controller
            // shows up as a visible error instead of the nav button silently doing nothing.
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR,
                    "Couldn't open this page:\n" + e.getMessage());
            alert.setHeaderText("Something went wrong");
            alert.showAndWait();
        }
    }

    private void highlightActiveNav(Button active) {
        for (Button b : new Button[]{navDashboard, navProducts, navMembers, navSales, navReports}) {
            b.getStyleClass().remove("nav-button-active");
        }
        active.getStyleClass().add("nav-button-active");
    }
}
