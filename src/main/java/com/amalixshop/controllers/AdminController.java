package com.amalixshop.controllers;

import com.amalixshop.models.Product;
import com.amalixshop.models.Category;
import com.amalixshop.services.ProductService;
import com.amalixshop.services.CategoryService;
import com.amalixshop.utils.EncryptionUtil;
import com.amalixshop.utils.NavigationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static com.amalixshop.utils.AlertUtil.*;

public class AdminController implements Initializable {

    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();

    // ========== CATEGORY TAB FIELDS ==========
    @FXML private TextField categoryNameField;
    @FXML private TextArea categoryDescField;
    @FXML private TableView<Category> categoriesTable;

    // ========== PRODUCT TAB FIELDS ==========
    @FXML private TextField productNameField;
    @FXML private TextArea productDescField;
    @FXML private TextField priceField;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private TableView<Product> productsTable;
    @FXML private TextField stockField;

    private ObservableList<Product> productsList = FXCollections.observableArrayList();
    private ObservableList<Category> categoriesList = FXCollections.observableArrayList();

    private Product selectedProduct = null;
    private Category selectedCategory = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupCategoryTable();
        setupProductTable();
        setupCategoryComboBox();
        loadProducts();
    }

    // ========== CATEGORY TAB METHODS ==========

    private void setupCategoryTable() {
        // Set up cell value factories
        categoriesTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        categoriesTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        categoriesTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("description"));

        categoriesTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedCategory = newSelection;
                        populateCategoryForm(newSelection);
                    }
                }
        );


        // Load data
        loadCategories();
    }

    private void populateCategoryForm(Category category) {
        categoryNameField.setText(category.getCategoryName());
        categoryDescField.setText(category.getDescription());
    }


    private void loadCategories() {
        categoriesList.setAll(categoryService.getAllCategories());
        categoriesTable.setItems(categoriesList);
    }

    private void loadProducts() {
        productsList.setAll(productService.getAllProducts());
        productsTable.setItems(productsList);
    }

    private void loadCategoriesIntoComboBox() {
        categoriesList.setAll(categoryService.getAllCategories());
        categoryComboBox.setItems(categoriesList);
    }

    private void setupCategoryComboBox() {
        loadCategoriesIntoComboBox();

        categoryComboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                setText(empty || category == null ? null : category.getCategoryName());
            }
        });

        categoryComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                setText(empty || category == null ? null : category.getCategoryName());
            }
        });
    }



    @FXML
    private void handleAddCategory() {
        try {
            // 1. Get values from UI
            String name = categoryNameField.getText();
            String description = categoryDescField.getText();

            // 2. Call service
            String success = categoryService.addCategory(name, description);

            // 3. Handle result
            if (success != null) {
                showSuccess("Success", "Category added successfully");
                handleClearCategoryForm();
                loadCategories();
                loadCategoriesIntoComboBox();
            } else {
                showError("Error", "Failed to add category");
            }
        } catch (Exception e) {
            showError("Error", "An error occurred: " + e.getMessage());
        }
    }


    @FXML
    private void handleUpdateCategory() {
        if (selectedCategory == null) {
            showWarning("No Selection", "Please select a category to update.");
            return;
        }

        try {
            String name = categoryNameField.getText();
            String description = categoryDescField.getText();

            boolean success = categoryService.updateCategory(
                    selectedCategory.getCategoryId(),
                    name,
                    description
            );

            if (success) {
                showSuccess("Success", "Category updated successfully.");
                handleClearCategoryForm();
                loadCategories();
            } else {
                showError("Error", "Failed to update category.");
            }

        } catch (Exception e) {
            showError("Error", e.getMessage());
        }
    }


    @FXML
    private void handleDeleteCategory() {
        if (selectedCategory == null) {
            showWarning("No Selection", "Please select a category to delete.");
            return;
        }

        boolean confirmed = showConfirmation(
                "Confirm Delete",
                "Are you sure you want to delete this category?"
        );

        if (!confirmed) return;

        try {
            boolean success = categoryService.deleteCategory(selectedCategory.getCategoryId());

            if (success) {
                showSuccess("Deleted", "Category deleted successfully.");
                handleClearCategoryForm();
                loadCategories();
            } else {
                showError("Error", "Failed to delete category.");
            }

        } catch (Exception e) {
            showError("Error", e.getMessage());
        }
    }


    @FXML
    private void handleClearCategoryForm() {
        categoryNameField.clear();
        categoryDescField.clear();
        categoriesTable.getSelectionModel().clearSelection();
        selectedCategory = null;
    }


    // ========== PRODUCT TAB METHODS ==========

    private void setupProductTable() {
        // Will be implemented later
        productsTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("productId"));
        productsTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("productName"));
        productsTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("description"));
        productsTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        productsTable.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("price"));

        productsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectedProduct = newSelection;
                        populateProductForm(newSelection);
                    }
                }
        );

        loadProducts();
    }

    private void populateProductForm(Product product) {
        productNameField.setText(product.getProductName());
        productDescField.setText(product.getDescription());
        priceField.setText(String.valueOf(product.getPrice()));

        // TODO: You need to get stock quantity from product
        // Currently Product model doesn't have stockQuantity field
        stockField.setText("0"); // Placeholder

        // Set category
        for (Category category : categoriesList) {
            if (category.getCategoryId().equals(product.getCategoryId())) {
                categoryComboBox.getSelectionModel().select(category);
                break;
            }
        }
    }

    @FXML
    private void handleSaveProduct() {
        try {

            Category selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
            if (selectedCategory == null) {
                showWarning("Validation Error", "Please select a category.");
                return;
            }

            // Get stock quantity
            int stockQuantity;
            try {
                stockQuantity = Integer.parseInt(stockField.getText());
                if (stockQuantity < 0) {
                    showWarning("Validation Error", "Stock quantity cannot be negative.");
                    return;
                }
            } catch (NumberFormatException e) {
                showWarning("Validation Error", "Please enter a valid stock number.");
                return;
            }


            Product product = new Product(
                    productNameField.getText(),
                    productDescField.getText(),
                    Double.parseDouble(priceField.getText()),
                    selectedCategory.getCategoryId()
            );

            boolean success = productService.saveProduct(product, stockQuantity);

            if (success) {
                showSuccess("Success", "Product saved successfully.");
                clearProductForm();
                loadProducts();
            } else {
                showError("Error", "Failed to save product.");
            }

        } catch (Exception e) {
            showError("Error", "An error occurred: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateProduct() {
        if (selectedProduct == null) {
            showWarning("No Selection", "Please select a product to update.");
            return;
        }

        try {
            Category selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
            if (selectedCategory == null) {
                showWarning("Validation Error", "Please select a category.");
                return;
            }

            // Get stock quantity (you need to load this from inventory)
            int stockQuantity;
            try {
                stockQuantity = Integer.parseInt(stockField.getText());
                if (stockQuantity < 0) {
                    showWarning("Validation Error", "Stock quantity cannot be negative.");
                    return;
                }
            } catch (NumberFormatException e) {
                showWarning("Validation Error", "Please enter a valid stock number.");
                return;
            }

            // Update product object with form data
            Product updatedProduct = new Product(
                    productNameField.getText(),
                    productDescField.getText(),
                    Double.parseDouble(priceField.getText()),
                    selectedCategory.getCategoryId()
            );

            // Set the product ID for update
            updatedProduct.setProductId(selectedProduct.getProductId());

            boolean success = productService.updateProduct(updatedProduct, stockQuantity);

            if (success) {
                showSuccess("Success", "Product updated successfully.");
                clearProductForm();
                loadProducts();
            } else {
                showError("Error", "Failed to update product.");
            }

        } catch (Exception e) {
            showError("Error", "An error occurred: " + e.getMessage());
        }
    }


    @FXML
    private void handleDeleteProduct() {
        if (selectedProduct == null) {
            showWarning("No Selection", "Please select a product to delete.");
            return;
        }

        boolean confirmed = showConfirmation(
                "Confirm Delete",
                "Are you sure you want to delete this product?"
        );

        if (!confirmed) return;

        try {
            boolean success = productService.deleteProduct(
                    EncryptionUtil.decrypt(selectedProduct.getProductId())
            );

            if (success) {
                showSuccess("Deleted", "Product deleted successfully.");
                clearProductForm();
                loadProducts();
            } else {
                showError("Error", "Failed to delete product.");
            }

        } catch (Exception e) {
            showError("Error", e.getMessage());
        }
    }

    @FXML
    private void handleClearProductForm() {
        clearProductForm();
        selectedProduct = null;
    }

    private void clearProductForm() {
        productNameField.clear();
        productDescField.clear();
        priceField.clear();
        stockField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
        productsTable.getSelectionModel().clearSelection();
        selectedProduct = null;
    }

    // ========== NAVIGATION ==========

    @FXML
    private void handleLogout() {
        Stage stage = (Stage) productNameField.getScene().getWindow();
        NavigationUtil.navigateToLogin(stage);
    }
}