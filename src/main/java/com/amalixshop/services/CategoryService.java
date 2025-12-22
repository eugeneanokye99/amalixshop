package com.amalixshop.services;

import com.amalixshop.dao.CategoryDAO;
import com.amalixshop.models.Category;
import java.util.List;

public class CategoryService {
    private final CategoryDAO categoryDAO = new CategoryDAO();

    public List<Category> getAllCategories() {
        try {
            return categoryDAO.getAllCategories();
        } catch (Exception e) {
            System.err.println("Error fetching categories: " + e.getMessage());
            return List.of();
        }
    }

    public String addCategory(String name, String description) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return "Category name cannot be empty";
            }

            Category category = new Category(name.trim(), description != null ? description.trim() : "");


            return categoryDAO.save(category);

        } catch (Exception e) {
            return "Error adding category: " + e.getMessage();
        }
    }

    public boolean updateCategory(String categoryId, String name, String description) {
        try {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Category name cannot be empty");
            }

            Category category = new Category(categoryId, name.trim(), description != null ? description.trim() : "");

            return categoryDAO.update(category);

        } catch (Exception e) {
            System.err.println("Error updating category: " + e.getMessage());
            return false;
        }
    }


    public boolean deleteCategory(String categoryId) {
        try {
            return categoryDAO.delete(categoryId);
        } catch (Exception e) {
            System.err.println("Error deleting category: " + e.getMessage());
            return false;
        }
    }



}