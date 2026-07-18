package com.techshop.inventorypos.controller.views;

import com.techshop.inventorypos.entity.Member;
import com.techshop.inventorypos.entity.Product;
import com.techshop.inventorypos.entity.Sale;
import com.techshop.inventorypos.service.MemberService;
import com.techshop.inventorypos.service.ProductService;
import com.techshop.inventorypos.service.SaleService;
import com.techshop.inventorypos.util.PaginationBar;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class SalesController {

    private static final int PAGE_SIZE = 30;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    @FXML private ComboBox<Product> productBox;
    @FXML private TextField quantityField;
    @FXML private ComboBox<Member> memberBox;
    @FXML private Button recordSaleButton;
    @FXML private Label saleStatusLabel;

    @FXML private TableView<Sale> salesTable;
    @FXML private TableColumn<Sale, Long> colSaleId;
    @FXML private TableColumn<Sale, String> colDate;
    @FXML private TableColumn<Sale, String> colProduct;
    @FXML private TableColumn<Sale, Integer> colQuantity;
    @FXML private TableColumn<Sale, String> colMember;
    @FXML private TableColumn<Sale, Double> colTotal;
    @FXML private HBox paginationContainer;

    private final ProductService productService;
    private final MemberService memberService;
    private final SaleService saleService;

    private final ObservableList<Sale> pageList = FXCollections.observableArrayList();
    private List<Sale> allSales = List.of();
    private final PaginationBar pagination = new PaginationBar();

    @Autowired
    public SalesController(ProductService productService, MemberService memberService, SaleService saleService) {
        this.productService = productService;
        this.memberService = memberService;
        this.saleService = saleService;
    }

    @FXML
    public void initialize() {
        productBox.setItems(FXCollections.observableArrayList(productService.getAllProducts()));
        memberBox.setItems(FXCollections.observableArrayList(memberService.getAllMembers()));

        colSaleId.setCellValueFactory(d -> new SimpleLongProperty(d.getValue().getId()).asObject());
        colDate.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSaleDate().format(FMT)));

        // Each Sale currently holds exactly one item (single-product sale flow) -
        // showing that item's product/quantity as their own columns instead of a merged string.
        colProduct.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getItems().isEmpty() ? "-" : d.getValue().getItems().get(0).getProduct().getName()));
        colQuantity.setCellValueFactory(d -> new SimpleIntegerProperty(
                d.getValue().getItems().isEmpty() ? 0 : d.getValue().getItems().get(0).getQuantity()).asObject());
        colMember.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getMember() == null ? "Walk-in" : d.getValue().getMember().getName()));
        colTotal.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getTotalAmount()).asObject());

        salesTable.setItems(pageList);
        paginationContainer.getChildren().add(pagination);
        pagination.setOnPageChange(this::renderCurrentPage);

        refreshSales();

        recordSaleButton.setOnAction(e -> handleRecordSale());
    }

    private void refreshSales() {
        allSales = saleService.getAllSales();
        pagination.setItemCount(allSales.size(), PAGE_SIZE);
        renderCurrentPage();
    }

    private void renderCurrentPage() {
        pageList.setAll(PaginationBar.pageOf(allSales, pagination.getCurrentPage(), PAGE_SIZE));
    }

    private void handleRecordSale() {
        Product selected = productBox.getValue();
        if (selected == null) {
            saleStatusLabel.setText("Select a product first.");
            return;
        }
        int qty;
        try {
            qty = Integer.parseInt(quantityField.getText().trim());
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            saleStatusLabel.setText("Enter a valid quantity.");
            return;
        }

        Member selectedMember = memberBox.getValue(); // may be null - walk-in sale

        try {
            saleService.recordSale(selected, qty, selectedMember);
            saleStatusLabel.setText("Sale recorded.");
            quantityField.clear();
            productBox.setItems(FXCollections.observableArrayList(productService.getAllProducts()));
            refreshSales();
        } catch (IllegalStateException ex) {
            saleStatusLabel.setText(ex.getMessage());
        }
    }
}
