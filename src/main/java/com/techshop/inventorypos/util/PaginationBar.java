package com.techshop.inventorypos.util;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.function.Consumer;

/**
 * A small "< 1 / 3 >" pagination bar. Used the same way on Products, Sales,
 * Reports (30 rows/page) and Dashboard (15 rows/page) so all four pages
 * behave consistently.
 */
public class PaginationBar extends HBox {

    private final Button prevButton = new Button("<");
    private final Label pageLabel = new Label("1 / 1");
    private final Button nextButton = new Button(">");

    private int currentPage = 1;   // 1-indexed
    private int totalPages = 1;
    private Runnable onPageChange;

    public PaginationBar() {
        setAlignment(Pos.CENTER);
        setSpacing(12);
        getStyleClass().add("pagination-bar");
        prevButton.getStyleClass().add("pagination-btn");
        nextButton.getStyleClass().add("pagination-btn");
        pageLabel.getStyleClass().add("pagination-label");

        prevButton.setOnAction(e -> goTo(currentPage - 1));
        nextButton.setOnAction(e -> goTo(currentPage + 1));

        getChildren().addAll(prevButton, pageLabel, nextButton);
        updateState();
    }

    public void setOnPageChange(Runnable onPageChange) {
        this.onPageChange = onPageChange;
    }

    /** Recalculates total pages from a full list size + page size. Call after data refreshes. */
    public void setItemCount(int totalItems, int pageSize) {
        totalPages = Math.max(1, (int) Math.ceil(totalItems / (double) pageSize));
        if (currentPage > totalPages) currentPage = totalPages;
        if (currentPage < 1) currentPage = 1;
        updateState();
    }

    public void resetToFirstPage() {
        currentPage = 1;
        updateState();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    private void goTo(int page) {
        if (page < 1 || page > totalPages || page == currentPage) return;
        currentPage = page;
        updateState();
        if (onPageChange != null) onPageChange.run();
    }

    private void updateState() {
        pageLabel.setText(currentPage + " / " + totalPages);
        prevButton.setDisable(currentPage <= 1);
        nextButton.setDisable(currentPage >= totalPages);
    }

    /** Utility: slices a full list down to just the current page's rows. */
    public static <T> List<T> pageOf(List<T> fullList, int page, int pageSize) {
        int from = Math.min((page - 1) * pageSize, fullList.size());
        int to = Math.min(from + pageSize, fullList.size());
        return fullList.subList(from, to);
    }
}
