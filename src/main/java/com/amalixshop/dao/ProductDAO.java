package com.amalixshop.dao;

import com.amalixshop.database.DatabaseConnection;
import com.amalixshop.models.Product;
import com.amalixshop.utils.EncryptionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ProductDAO {

    public int insertProduct(Product product) {
        String sql = "INSERT INTO products (product_name, description, price, category_id) VALUES (?, ?, ?, ?) RETURNING product_id";

        try (Connection conn = DatabaseConnection.getConnection()) {

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, product.getProductName());
            stmt.setString(2, product.getDescription());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, EncryptionUtil.decrypt(product.getCategoryId()));


            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("product_id");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error inserting product: " + e.getMessage());
        }

        return -1;
    }



    public int updateProduct(Product product) {
        String sql = "UPDATE products SET product_name = ?, description = ?, price = ?, category_id = ? WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getProductName());
            stmt.setString(2, product.getDescription());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, EncryptionUtil.decrypt(product.getCategoryId()));
            stmt.setInt(5, EncryptionUtil.decrypt(product.getProductId())); // Add this line

            return stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
            return 0;
        }
    }


    public int deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            return stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
            return 0;
        }
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name, i.stock_quantity " +
                "FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN inventory i ON p.product_id = i.product_id " +
                "ORDER BY p.product_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getString("product_name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        EncryptionUtil.encrypt(rs.getInt("category_id"))
                );

                // Set product ID (encrypted)
                product.setProductId(EncryptionUtil.encrypt(rs.getInt("product_id")));

                // Set category name
                product.setCategoryName(rs.getString("category_name"));
                product.setStockQuantity(rs.getInt("stock_quantity"));

                products.add(product);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching products: " + e.getMessage());
        }

        return products;
    }


    public Product getProductById(int productId) {
        String sql = "SELECT p.*, c.category_name, i.stock_quantity " +
                "FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN inventory i ON p.product_id = i.product_id " +
                "WHERE p.product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getString("product_name"),
                            rs.getString("description"),
                            rs.getDouble("price"),
                            EncryptionUtil.encrypt(rs.getInt("category_id"))
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching product: " + e.getMessage());
        }

        return null;
    }

    public List<Product> searchProducts(String query) {
        List<Product> products = new ArrayList<>();

        if (query == null || query.trim().isEmpty()) {
            return getAllProducts();
        }

        String searchTerm = "%" + query.toLowerCase() + "%";
        String sql = "SELECT p.*, c.category_name, i.stock_quantity " +
                "FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN inventory i ON p.product_id = i.product_id " +
                "WHERE LOWER(p.product_name) LIKE ? " +
                "OR LOWER(p.description) LIKE ? " +
                "OR LOWER(c.category_name) LIKE ? " +
                "ORDER BY p.product_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            stmt.setString(3, searchTerm);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = mapResultSetToProduct(rs);
                    products.add(product);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error searching products: " + e.getMessage());
        }

        return products;
    }

    public List<Product> getProductsByCategory(String categoryName) {
        List<Product> products = new ArrayList<>();

        String sql = "SELECT p.*, c.category_name, i.stock_quantity " +
                "FROM products p " +
                "INNER JOIN categories c ON p.category_id = c.category_id " +
                "LEFT JOIN inventory i ON p.product_id = i.product_id " +
                "WHERE c.category_name = ? " +
                "ORDER BY p.product_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoryName);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = mapResultSetToProduct(rs);
                    products.add(product);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching products by category: " + e.getMessage());
        }

        return products;
    }

    // Helper method to map ResultSet to Product
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product(
                rs.getString("product_name"),
                rs.getString("description"),
                rs.getDouble("price"),
                EncryptionUtil.encrypt(rs.getInt("category_id"))
        );

        product.setProductId(EncryptionUtil.encrypt(rs.getInt("product_id")));
        product.setCategoryName(rs.getString("category_name"));
        product.setStockQuantity(rs.getInt("stock_quantity"));

        return product;
    }
}