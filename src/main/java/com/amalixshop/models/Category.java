package com.amalixshop.models;

import java.sql.Timestamp;

public class Category {
    private  String categoryId;
    private final String categoryName;
    private final String description;

    public Category(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
    }
    public Category(String categoryId, String categoryName, String description) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
    }



    // Getters only
    public String getCategoryId() {
        return categoryId;
    }
    public String getCategoryName() { return categoryName; }
    public String getDescription() { return description; }


}