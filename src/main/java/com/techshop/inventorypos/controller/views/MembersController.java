package com.techshop.inventorypos.controller.views;

import com.techshop.inventorypos.entity.Member;
import com.techshop.inventorypos.service.MemberService;
import com.techshop.inventorypos.util.DialogHelper;
import com.techshop.inventorypos.util.PaginationBar;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MembersController {

    private static final int PAGE_SIZE = 30;

    @FXML private Button addButton, editButton, deleteButton;
    @FXML private TableView<Member> memberTable;
    @FXML private TableColumn<Member, Long> colId;
    @FXML private TableColumn<Member, String> colName;
    @FXML private TableColumn<Member, String> colPhone;
    @FXML private TableColumn<Member, String> colEmail;
    @FXML private TableColumn<Member, String> colType;
    @FXML private HBox paginationContainer;
    @FXML private Label statusLabel;

    private final MemberService memberService;
    private final ObservableList<Member> pageList = FXCollections.observableArrayList();
    private List<Member> allMembers = List.of();
    private final PaginationBar pagination = new PaginationBar();

    @Autowired
    public MembersController(MemberService memberService) {
        this.memberService = memberService;
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d -> new SimpleLongProperty(d.getValue().getId()).asObject());
        colName.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("email"));
        colType.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("membershipType"));

        memberTable.setItems(pageList);
        paginationContainer.getChildren().add(pagination);
        pagination.setOnPageChange(this::renderCurrentPage);

        addButton.setOnAction(e -> handleAdd());
        editButton.setOnAction(e -> handleEdit());
        deleteButton.setOnAction(e -> handleDelete());

        refresh();
    }

    private void refresh() {
        allMembers = memberService.getAllMembers();
        pagination.setItemCount(allMembers.size(), PAGE_SIZE);
        renderCurrentPage();
    }

    private void renderCurrentPage() {
        pageList.setAll(PaginationBar.pageOf(allMembers, pagination.getCurrentPage(), PAGE_SIZE));
        statusLabel.setText(allMembers.size() + " member(s) total.");
    }

    private void handleAdd() {
        Optional<Member> result = DialogHelper.openMemberDialog(null);
        result.ifPresent(m -> {
            memberService.saveMember(m);
            refresh();
            statusLabel.setText("Added \"" + m.getName() + "\".");
        });
    }

    private void handleEdit() {
        Member selected = memberTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a member to edit first.");
            return;
        }
        Optional<Member> result = DialogHelper.openMemberDialog(selected);
        result.ifPresent(m -> {
            memberService.saveMember(m);
            refresh();
            statusLabel.setText("Updated \"" + m.getName() + "\".");
        });
    }

    private void handleDelete() {
        Member selected = memberTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a member to delete first.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete \"" + selected.getName() + "\"? This cannot be undone.");
        confirm.setHeaderText(null);
        confirm.showAndWait().filter(btn -> btn == ButtonType.OK).ifPresent(btn -> {
            memberService.deleteMember(selected.getId());
            refresh();
            statusLabel.setText("Member deleted.");
        });
    }
}
