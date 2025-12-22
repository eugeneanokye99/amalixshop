package com.amalixshop.dao;

import com.amalixshop.database.DatabaseConnection;
import com.amalixshop.models.Category;
import com.amalixshop.utils.EncryptionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT category_id, category_name, description FROM categories ORDER BY category_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Category category = new Category(
                        EncryptionUtil.encrypt(rs.getInt("category_id")),
                        rs.getString("category_name"),
                        rs.getString("description")
                );
                categories.add(category);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching categories: " + e.getMessage());
        }

        return categories;
    }

    public String save(Category category) {
        String sql = "INSERT INTO categories (category_name, description) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()){

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, category.getCategoryName());
            stmt.setString(2, category.getDescription());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return EncryptionUtil.encrypt(rs.getInt(1)); // Return generated ID
                    }
                }
            }
            return "";

        } catch (SQLException e) {
            System.err.println("Error saving category: " + e.getMessage());
            return "";
        }
    }

    public boolean update(Category category) {
        String sql = "UPDATE categories SET category_name = ?, description = ? WHERE category_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, category.getCategoryName());
            stmt.setString(2, category.getDescription());
            stmt.setInt(3, EncryptionUtil.decrypt(category.getCategoryId()));

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String categoryId) {
        String sql = "DELETE FROM categories WHERE category_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, EncryptionUtil.decrypt(categoryId));
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
            return false;
        }
    }

    public String getCategoryNameById(int categoryId) {
        String sql = "SELECT category_name FROM categories WHERE category_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("category_name");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting category name: " + e.getMessage());
        }

        return "Unknown";
    }

}