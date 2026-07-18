package com.techshop.inventorypos.dialog;

import com.techshop.inventorypos.entity.Member;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MemberDialogController {

    @FXML private Label dialogTitle;
    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> membershipBox;
    @FXML private Label errorLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Stage dialogStage;
    private Member member;
    private boolean saveClicked = false;

    @FXML
    public void initialize() {
        membershipBox.setItems(FXCollections.observableArrayList("Standard", "Silver", "Gold"));
        membershipBox.setValue("Standard");

        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> dialogStage.close());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMember(Member member) {
        this.member = member;
        dialogTitle.setText("Edit Member");
        nameField.setText(member.getName());
        phoneField.setText(member.getPhone());
        emailField.setText(member.getEmail());
        membershipBox.setValue(member.getMembershipType());
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    public Member getResultMember() {
        if (member == null) {
            member = new Member();
        }
        member.setName(nameField.getText().trim());
        member.setPhone(phoneField.getText().trim());
        member.setEmail(emailField.getText().trim());
        member.setMembershipType(membershipBox.getValue());
        return member;
    }

    private void handleSave() {
        if (nameField.getText() == null || nameField.getText().isBlank()) {
            errorLabel.setText("Member name is required.");
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            return;
        }
        saveClicked = true;
        dialogStage.close();
    }
}
