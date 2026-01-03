package com.amalixshop.controllers;

import com.amalixshop.models.Customer;
import com.amalixshop.services.CartService;
import com.amalixshop.services.ProductSearchService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import com.amalixshop.utils.NavigationUtil;
import com.amalixshop.services.CategoryService;
import com.amalixshop.models.Category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.amalixshop.services.ProductService;
import com.amalixshop.models.Product;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;

public class DashboardController {
    // Service instance
    private final CategoryService categoryService = new CategoryService();
    private final ProductService productService = new ProductService();
    private final ProductSearchService searchService = new ProductSearchService();
    private final CartService cartService = new CartService();
    private String currentCustomerId;

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

    // Current filter state
    private String currentCategoryFilter = null;
    private Double currentMinPrice = null;
    private Double currentMaxPrice = null;
    private String currentSort = null;

    // Pagination variables
    private static final int PRODUCTS_PER_PAGE = 12;
    private int currentPage = 1;
    private int totalPages = 1;
    private List<Product> currentProducts = new ArrayList<>();

    // Navigation methods
    @FXML
    private void handleLogout() {
        Stage stage = (Stage) searchField.getScene().getWindow();
        NavigationUtil.navigateToLogin(stage);
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        performSearch(query);
    }

    @FXML
    private void handleApplyFilters() {
        try {
            // Get price values
            String minPriceText = minPriceField.getText().trim();
            String maxPriceText = maxPriceField.getText().trim();

            currentMinPrice = minPriceText.isEmpty() ? null : Double.parseDouble(minPriceText);
            currentMaxPrice = maxPriceText.isEmpty() ? null : Double.parseDouble(maxPriceText);

            applyCurrentFilters();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setContentText("Please enter valid numbers for price range.");
            alert.showAndWait();
        }
    }

    private void applyCurrentFilters() {
        List<Product> results = applyCombinedFilters(searchField.getText().trim());

        // Apply sorting if selected
        if (currentSort != null) {
            results = sortProducts(results, currentSort);
        }

        // Reset to page 1 when filters change
        currentPage = 1;
        displayProducts(results);
    }

    @FXML
    private void handleClearFilters() {
        // Clear all filters
        categoryListView.getSelectionModel().clearSelection();
        minPriceField.clear();
        maxPriceField.clear();
        searchField.clear();
        sortComboBox.getSelectionModel().clearSelection();

        // Reset filter state
        currentCategoryFilter = null;
        currentMinPrice = null;
        currentMaxPrice = null;
        currentSort = null;

        // Show all products (reset to page 1)
        currentPage = 1;
        displayProducts(searchService.search(""));
    }

    @FXML
    private void handlePreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            showPageProducts();
            updatePagination();
        }
    }

    @FXML
    private void handleNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            showPageProducts();
            updatePagination();
        }
    }

    // Sidebar navigation methods
    @FXML
    private void handleViewProducts() {
        loadProducts();
        loadCategories();
    }

    @FXML
    private void handleViewOrders() {
        if (currentCustomerId == null) {
            showLoginRequiredAlert();
            return;
        }
        Stage stage = (Stage) searchField.getScene().getWindow();
        NavigationUtil.navigateToOrders(stage, currentCustomerId);
    }

    @FXML
    private void handleViewProfile() {
        if (currentCustomerId == null) {
            showLoginRequiredAlert();
            return;
        }
        Stage stage = (Stage) searchField.getScene().getWindow();
        NavigationUtil.navigateToProfile(stage, currentCustomerId);
    }

    private void showLoginRequiredAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Login Required");
        alert.setContentText("Please login to access this feature.");
        alert.showAndWait();
    }

    @FXML
    private void handleViewReviews() {
        // TODO: Navigate to reviews page
        System.out.println("View reviews clicked");
    }

    @FXML
    private void handleViewCart() {
        System.out.println("Navigate to cart clicked");
        System.out.println("Current customer ID: " + currentCustomerId);

        Stage stage = (Stage) searchField.getScene().getWindow();
        NavigationUtil.navigateToCart(stage, currentCustomerId);
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
                    currentSort = newValue;
                    applyCurrentFilters();
                }
        );

        loadCategories();
        loadAndCacheProducts();
        loadProducts();
        setupSearchSuggestions();
        setupCategorySelectionListener();
    }

    public void setCurrentCustomerId(String customerId) {
        this.currentCustomerId = customerId;
    }


    private void loadProducts() {
        try {
            productsGrid.getChildren().clear();

            List<Product> products = productService.getAllProducts();
            currentPage = 1;
            displayProducts(products);

        } catch (Exception e) {
            Label errorLabel = new Label("Error loading products. Please try again.");
            errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #e74c3c;");
            productsGrid.getChildren().add(errorLabel);
        }
    }

    private void loadCategories() {

        categoryListView.getItems().clear();

        // Get categories from service
        List<Category> categories = categoryService.getAllCategories();

        if (categories.isEmpty()) {
            categoryListView.getItems().add("No categories available");
        } else {
            // Add "All Categories" option
            categoryListView.getItems().add("All Categories");

            for (Category category : categories) {
                categoryListView.getItems().add(category.getCategoryName());
            }

        }
    }


    private void loadAndCacheProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            searchService.initializeCache(products);
            currentPage = 1;
            displayProducts(products);
        } catch (Exception e) {
            System.err.println("Error loading products: " + e.getMessage());
        }
    }

    private void setupSearchSuggestions() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() >= 2) { // Start suggesting after 2 characters
                List<String> suggestions = searchService.getSuggestions(newValue);
                // You could show these in a dropdown or ListView
                if (!suggestions.isEmpty()) {
                    System.out.println("Suggestions for '" + newValue + "': " + suggestions);
                }
            }
        });
    }


    private void setupCategorySelectionListener() {
        categoryListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null && !newValue.equals(oldValue)) {
                        applyCategoryFilter(newValue);
                    }
                }
        );
    }

    private void setupProductGrid() {
        // TODO: Set up product cards in grid
        System.out.println("Setting up product grid...");
    }

    private void updatePagination() {
        // Calculate total pages
        totalPages = (int) Math.ceil((double) currentProducts.size() / PRODUCTS_PER_PAGE);

        // Update page label
        pageLabel.setText("Page " + currentPage + " of " + totalPages);

        // Enable/disable buttons
        prevPageBtn.setDisable(currentPage <= 1);
        nextPageBtn.setDisable(currentPage >= totalPages);

    }

    private void performSearch(String query) {
        try {
            List<Product> results;

            if (currentCategoryFilter != null || currentMinPrice != null || currentMaxPrice != null) {
                // Apply combined filters
                results = applyCombinedFilters(query);
            } else {
                // Just search
                results = searchService.search(query);
            }

            currentPage = 1;
            displayProducts(results);
            resultsLabel.setText(results.size() + " results" +
                    (query.isEmpty() ? "" : " for \"" + query + "\""));

        } catch (Exception e) {
            System.err.println("Error searching: " + e.getMessage());
        }
    }

    // Update applyCategoryFilter method
    private void applyCategoryFilter(String category) {
        currentCategoryFilter = "All Categories".equals(category) ? null : category;
        applyCurrentFilters();
    }

    private void applyPriceFilter(double minPrice, double maxPrice) {
        // TODO: Filter products by price range
        System.out.println("Filtering by price: " + minPrice + " - " + maxPrice);
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.setPrefWidth(180);
        card.setPrefHeight(250);
        card.setStyle("-fx-background-color: white; -fx-border-color: #ddd; " +
                "-fx-border-radius: 8; -fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");


        // Product name
        Label nameLabel = new Label(product.getProductName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(150);

        // Product price
        Label priceLabel = new Label("₵" + String.format("%.2f", product.getPrice()));
        priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Category
        Label categoryLabel = new Label(product.getCategoryName());
        categoryLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        // Stock status (from inventory)
        Label stockLabel = getLabel(product);

        // Add to cart button
        Button addToCartBtn = new Button("Add to Cart");
        addToCartBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12px;");
        addToCartBtn.setPrefWidth(150);
        addToCartBtn.setOnAction(e -> addToCart(product));

        // View details button
        Button detailsBtn = new Button("View Details");
        detailsBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 12px;");
        detailsBtn.setPrefWidth(150);
        detailsBtn.setOnAction(e -> showProductDetails(product));

        card.getChildren().addAll(
                nameLabel,
                priceLabel,
                categoryLabel,
                stockLabel,
                addToCartBtn,
                detailsBtn
        );

        // Add hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #3498db; " +
                    "-fx-border-radius: 8; -fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(52,152,219,0.2), 15, 0, 0, 3);");
        });

        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; -fx-border-color: #ddd; " +
                    "-fx-border-radius: 8; -fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        });

        return card;
    }

    private static Label getLabel(Product product) {
        Label stockLabel = new Label();
        if (product.getStockQuantity() > 10) {
            stockLabel.setText("In Stock");
            stockLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 12px;");
        } else if (product.getStockQuantity() > 0) {
            stockLabel.setText("Low Stock: " + product.getStockQuantity());
            stockLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 12px;");
        } else {
            stockLabel.setText("Out of Stock");
            stockLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");
        }
        return stockLabel;
    }

    private void addToCart(Product product) {
        // Check if user is logged in
        if (currentCustomerId == null || currentCustomerId.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Login Required");
            alert.setHeaderText("Please login to add items to cart");
            alert.setContentText("You need to be logged in to add products to your cart.");
            alert.showAndWait();
            return;
        }

        // Check stock availability
        if (product.getStockQuantity() <= 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Out of Stock");
            alert.setContentText(product.getProductName() + " is currently out of stock.");
            alert.showAndWait();
            return;
        }

        try {
            // Decrypt the product ID
            String productId = product.getProductId();

            System.out.println(currentCustomerId);

            // Add to cart
            boolean success = cartService.addToCart(currentCustomerId, productId, 1);

            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Cart Updated");
                alert.setHeaderText("Product Added to Cart");
                alert.setContentText(product.getProductName() + " has been added to your cart.");
                alert.showAndWait();

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Failed to add product to cart. Please try again.");
                alert.showAndWait();
            }

        } catch (Exception e) {
            System.err.println("Error adding to cart: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("An error occurred: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void showProductDetails(Product product) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Product Details");
        alert.setHeaderText(product.getProductName());
        alert.setContentText("Price: $" + product.getPrice() +
                "\nCategory: " + product.getCategoryName() +
                "\nDescription: " + product.getDescription() +
                "\nStock: " + product.getStockQuantity() + " units");
        alert.showAndWait();
    }

    private List<Product> applyCombinedFilters(String query) {
        List<Product> results;

        if (currentCategoryFilter != null && (currentMinPrice != null || currentMaxPrice != null)) {
            // All filters: category + price + search
            double min = currentMinPrice != null ? currentMinPrice : 0;
            double max = currentMaxPrice != null ? currentMaxPrice : Double.MAX_VALUE;

            List<Product> categoryProducts = searchService.filterByCategory(currentCategoryFilter);
            results = categoryProducts.stream()
                    .filter(p -> p.getPrice() >= min && p.getPrice() <= max)
                    .filter(p -> query.isEmpty() ||
                            p.getProductName().toLowerCase().contains(query.toLowerCase()) ||
                            p.getDescription().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());

        } else if (currentCategoryFilter != null) {
            // Category filter only
            results = searchService.filterByCategory(currentCategoryFilter);
            if (!query.isEmpty()) {
                results = results.stream()
                        .filter(p -> p.getProductName().toLowerCase().contains(query.toLowerCase()) ||
                                p.getDescription().toLowerCase().contains(query.toLowerCase()))
                        .collect(Collectors.toList());
            }

        } else if (currentMinPrice != null || currentMaxPrice != null) {
            // Price filter only
            double min = currentMinPrice != null ? currentMinPrice : 0;
            double max = currentMaxPrice != null ? currentMaxPrice : Double.MAX_VALUE;

            results = searchService.filterByPriceRange(min, max);
            if (!query.isEmpty()) {
                results = results.stream()
                        .filter(p -> p.getProductName().toLowerCase().contains(query.toLowerCase()) ||
                                p.getDescription().toLowerCase().contains(query.toLowerCase()))
                        .collect(Collectors.toList());
            }

        } else {
            // Just search
            results = searchService.search(query);
        }

        return results;
    }

    private List<Product> sortProducts(List<Product> products, String sortOption) {
        List<Product> sorted = new ArrayList<>(products);

        switch (sortOption) {
            case "Price: Low to High":
                sorted.sort(Comparator.comparingDouble(Product::getPrice));
                break;
            case "Price: High to Low":
                sorted.sort((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
                break;
            case "Name A-Z":
                sorted.sort(Comparator.comparing(Product::getProductName, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Newest":
                // Assuming you have a date field in Product
                // sorted.sort(Comparator.comparing(Product::getCreatedDate).reversed());
                break;
        }

        return sorted;
    }

    private void displayProducts(List<Product> products) {
        // Store all filtered products for pagination
        currentProducts = new ArrayList<>(products);

        // Reset to page 1 when new products are loaded
        currentPage = 1;

        // Show only products for current page
        showPageProducts();

        // Update pagination
        updatePagination();
    }

    private void showPageProducts() {
        productsGrid.getChildren().clear();

        if (currentProducts.isEmpty()) {
            Label noProductsLabel = new Label("No products found");
            noProductsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
            productsGrid.getChildren().add(noProductsLabel);
            return;
        }

        // Calculate start and end indices for current page
        int startIndex = (currentPage - 1) * PRODUCTS_PER_PAGE;
        int endIndex = Math.min(startIndex + PRODUCTS_PER_PAGE, currentProducts.size());

        // Add products for current page
        for (int i = startIndex; i < endIndex; i++) {
            productsGrid.getChildren().add(createProductCard(currentProducts.get(i)));
        }

        // Update results label
        updateResultsLabel();
    }
    private void updateResultsLabel() {
        int totalCount = currentProducts.size();
        int startIndex = (currentPage - 1) * PRODUCTS_PER_PAGE + 1;
        int endIndex = Math.min(currentPage * PRODUCTS_PER_PAGE, totalCount);

        StringBuilder label = new StringBuilder();

        if (totalCount > 0) {
            label.append("Showing ").append(startIndex).append("-").append(endIndex)
                    .append(" of ").append(totalCount).append(" products");
        } else {
            label.append("No products found");
        }

        if (currentCategoryFilter != null) {
            label.append(" in ").append(currentCategoryFilter);
        }

        if (currentMinPrice != null || currentMaxPrice != null) {
            label.append(" (");
            if (currentMinPrice != null) label.append("$").append(currentMinPrice);
            label.append(" - ");
            if (currentMaxPrice != null) label.append("$").append(currentMaxPrice);
            label.append(")");
        }

        resultsLabel.setText(label.toString());
    }

    // Refresh cache when needed (e.g., after adding to cart)
    private void refreshProductCache() {
        List<Product> updatedProducts = productService.getAllProducts();
        searchService.refreshCache(updatedProducts);
        System.out.println("Refreshed cache with " + updatedProducts.size() + " products");
    }
}