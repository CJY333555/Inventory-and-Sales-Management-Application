package com.techshop.inventorypos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import com.techshop.inventorypos.util.UpdateChecker;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * This is the real entry point of the desktop app.
 * It starts Spring first (so IoC / dependency injection is ready),
 * then hands control over to JavaFX and loads the main screen.
 */
public class MainApp extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        // Start Spring Boot's IoC container before the JavaFX UI loads.
        // This is what lets @Autowired work inside JavaFX controllers.
        springContext = InventoryPosApplication.run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));

        // This is the key line: tell JavaFX to fetch controllers from Spring
        // instead of creating them with "new", so @Autowired fields get injected.
        loader.setControllerFactory(springContext::getBean);

        javafx.scene.Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        primaryStage.setTitle("Inventory & Sales Management System");
        primaryStage.setScene(scene);

        // Makes the window fill the screen edge-to-edge, not a small centered box.
        primaryStage.setMaximized(true);
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(700);

        // Optional app icon - drop a file named app-icon.png into resources/images/
        var iconStream = getClass().getResourceAsStream("/images/app-icon.png");
        if (iconStream != null) {
            primaryStage.getIcons().add(new Image(iconStream));
        }

        primaryStage.show();

        // Runs on a background thread and never interrupts startup even if it fails.
        UpdateChecker.checkForUpdatesAsync();
    }

    @Override
    public void stop() {
        // Cleanly shuts down the Spring context (closes DB connections etc.)
        springContext.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
