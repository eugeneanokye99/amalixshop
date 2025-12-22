package com.amalixshop.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import com.amalixshop.utils.NavigationUtil;

public class DashboardController {

    // UI Components from FXML
    @FXML private TextField searchField;
    @FXML private Label welcomeLabel;
    @FXML private ListView<String> categoryListView;
    @FXML private TextField minPriceField;
    @FXML private TextField maxPriceField;
    @FXML private Label resultsLabel;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private FlowPane productsGrid;
    @FXML private Button prevPageBtn;
    @FXML private Button nextPageBtn;
    @FXML private Label pageLabel;


    // Navigation methods
    @FXML
    private void handleLogout() {
        Stage stage = (Stage) searchField.getScene().getWindow();
        NavigationUtil.navigateToLogin(stage);
    }

    @FXML
    private void handleSearch() {
        // TODO: Implement product search functionality
        System.out.println("Search clicked");
    }

    @FXML
    private void handleApplyFilters() {
        // TODO: Apply category and price filters
        System.out.println("Apply filters clicked");
    }

    @FXML
    private void handleClearFilters() {
        // TODO: Clear all filters
        System.out.println("Clear filters clicked");
    }

    @FXML
    private void handlePreviousPage() {
        // TODO: Navigate to previous page
        System.out.println("Previous page clicked");
    }

    @FXML
    private void handleNextPage() {
        // TODO: Navigate to next page
        System.out.println("Next page clicked");
    }

    // Sidebar navigation methods
    @FXML
    private void handleViewProducts() {
        // TODO: Refresh product view (already on products page)
        System.out.println("View products clicked");
    }

    @FXML
    private void handleViewOrders() {
        // TODO: Navigate to orders page
        System.out.println("View orders clicked");
    }

    @FXML
    private void handleViewReviews() {
        // TODO: Navigate to reviews page
        System.out.println("View reviews clicked");
    }

    @FXML
    private void handleViewCart() {
        // TODO: Navigate to cart page
        System.out.println("View cart clicked");
    }

    @FXML
    private void handleViewProfile() {
        // TODO: Navigate to profile page
        System.out.println("View profile clicked");
    }

    // Initialize method
    @FXML
    private void initialize() {
        System.out.println("Customer dashboard initialized");

        // Initialize sort options
        sortComboBox.getItems().addAll(
                "Newest",
                "Price: Low to High",
                "Price: High to Low",
                "Name A-Z"
        );

        // Set up sort combo box listener
        sortComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    // TODO: Handle sort change
                    System.out.println("Sort changed to: " + newValue);
                }
        );
    }

    // Helper methods (to be implemented)
    private void loadProducts() {
        // TODO: Load products from service and display in grid
        System.out.println("Loading products...");
    }

    private void loadCategories() {
        // TODO: Load categories into list view
        System.out.println("Loading categories...");
    }

    private void setupProductGrid() {
        // TODO: Set up product cards in grid
        System.out.println("Setting up product grid...");
    }

    private void updatePagination() {
        // TODO: Update pagination buttons and label
        System.out.println("Updating pagination...");
    }

    private void performSearch(String query) {
        // TODO: Search products by name or category
        System.out.println("Searching for: " + query);
    }

    private void applyCategoryFilter(String category) {
        // TODO: Filter products by category
        System.out.println("Filtering by category: " + category);
    }

    private void applyPriceFilter(double minPrice, double maxPrice) {
        // TODO: Filter products by price range
        System.out.println("Filtering by price: " + minPrice + " - " + maxPrice);
    }
}