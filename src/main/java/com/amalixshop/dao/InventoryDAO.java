package com.amalixshop.dao;

import com.amalixshop.database.DatabaseConnection;

import java.sql.*;

public class InventoryDAO {

    public boolean createInventoryEntry(int productId, int stockQuantity) {
        String sql = "INSERT INTO inventory (product_id, stock_quantity) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            stmt.setInt(2, stockQuantity);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error creating inventory entry: " + e.getMessage());
            return false;
        }
    }

    public boolean updateInventoryStock(int productId, int newStockQuantity) {
        String sql = "UPDATE inventory SET stock_quantity = ? WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newStockQuantity);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating inventory: " + e.getMessage());
            return false;
        }
    }
}