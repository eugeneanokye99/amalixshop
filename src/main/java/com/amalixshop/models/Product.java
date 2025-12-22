package com.amalixshop.models;

public class Product {
    private String productId;
    private final String productName;
    private final String description;
    private final double price;
    private final String categoryId;
    private String categoryName;

    public Product(String productName, String description, double price, String categoryId) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
    }

    // Getters
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; } // Add this

    // Setters
    public void setProductId(String productId) { this.productId = productId; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}