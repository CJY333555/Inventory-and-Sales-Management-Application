package com.techshop.inventorypos.util;

import com.techshop.inventorypos.dialog.MemberDialogController;
import com.techshop.inventorypos.dialog.ProductDialogController;
import com.techshop.inventorypos.entity.Member;
import com.techshop.inventorypos.entity.Product;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

public class DialogHelper {

    /**
     * Opens the Add/Edit Product modal and blocks until it's closed.
     * Pass existingProduct=null for "Add", or a loaded Product for "Edit".
     * Returns the saved Product if the user clicked Save, or empty if cancelled.
     */
    public static Optional<Product> openProductDialog(Product existingProduct) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogHelper.class.getResource("/fxml/product_dialog.fxml"));
            Parent root = loader.load();

            ProductDialogController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(existingProduct == null ? "Add Product" : "Edit Product");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(DialogHelper.class.getResource("/css/style.css").toExternalForm());
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            controller.setDialogStage(dialogStage);
            if (existingProduct != null) {
                controller.setProduct(existingProduct);
            }

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                return Optional.of(controller.getResultProduct());
            }
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /** Same pattern as openProductDialog, for Member add/edit. */
    public static Optional<Member> openMemberDialog(Member existingMember) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogHelper.class.getResource("/fxml/member_dialog.fxml"));
            Parent root = loader.load();

            MemberDialogController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(existingMember == null ? "Add Member" : "Edit Member");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(DialogHelper.class.getResource("/css/style.css").toExternalForm());
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            controller.setDialogStage(dialogStage);
            if (existingMember != null) {
                controller.setMember(existingMember);
            }

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                return Optional.of(controller.getResultMember());
            }
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
