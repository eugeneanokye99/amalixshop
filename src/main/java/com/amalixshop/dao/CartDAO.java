package com.amalixshop.dao;

import com.amalixshop.models.Cart;
import com.amalixshop.models.CartItem;
import com.amalixshop.database.DatabaseConnection;
import com.amalixshop.utils.EncryptionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    // Create a new cart for a customer
    public String createCart(String customerId) {
        String sql = "INSERT INTO carts (customer_id) VALUES (?) RETURNING cart_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, EncryptionUtil.decrypt(customerId));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return EncryptionUtil.encrypt(rs.getInt("cart_id"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error creating cart: " + e.getMessage());
        }

        return null;
    }

    // Get cart by customer ID
    public Cart getCartByCustomerId(String customerId) {
        String sql = "SELECT * FROM carts WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, EncryptionUtil.decrypt(customerId));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cart cart = new Cart(
                            EncryptionUtil.encrypt(rs.getInt("cart_id")),
                            EncryptionUtil.encrypt(rs.getInt("customer_id"))
                    );

                    // Load cart items
                    List<CartItem> items = getCartItems(rs.getInt("cart_id"));
                    cart.setItems(items);
                    cart.calculateTotal();

                    return cart;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching cart: " + e.getMessage());
        }

        return null;
    }

    // Add item to cart
    public boolean addItemToCart(String cartId, String productId, int quantity) {
        String sql = "INSERT INTO cart_items (cart_id, product_id, quantity) " +
                "VALUES (?, ?, ?) " +
                "ON CONFLICT (cart_id, product_id) " +
                "DO UPDATE SET quantity = cart_items.quantity + EXCLUDED.quantity";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, EncryptionUtil.decrypt(cartId));
            stmt.setInt(2, EncryptionUtil.decrypt(productId));
            stmt.setInt(3, quantity);

            int rowsAffected = stmt.executeUpdate();
            updateCartTimestamp(cartId);
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding item to cart: " + e.getMessage());
        }

        return false;
    }

    // Get all items in a cart
    private List<CartItem> getCartItems(int cartId) {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT ci.*, p.product_name, p.price " +
                "FROM cart_items ci " +
                "JOIN products p ON ci.product_id = p.product_id " +
                "WHERE ci.cart_id = ? " +
                "ORDER BY ci.added_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cartId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem(
                            EncryptionUtil.encrypt(rs.getInt("cart_item_id")),
                            EncryptionUtil.encrypt(rs.getInt("cart_id")),
                            EncryptionUtil.encrypt(rs.getInt("product_id")),
                            rs.getInt("quantity")
                    );

                    item.setProductName(rs.getString("product_name"));
                    item.setPrice(rs.getDouble("price"));

                    items.add(item);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching cart items: " + e.getMessage());
        }

        return items;
    }

    // Update item quantity
    public boolean updateItemQuantity(String cartId, String productId, int quantity) {
        String sql = "UPDATE cart_items SET quantity = ? " +
                "WHERE cart_id = ? AND product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantity);
            stmt.setInt(2, EncryptionUtil.decrypt(cartId));
            stmt.setInt(3, EncryptionUtil.decrypt(productId));

            int rowsAffected = stmt.executeUpdate();
            updateCartTimestamp(cartId);
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating item quantity: " + e.getMessage());
        }

        return false;
    }

    // Remove item from cart
    public boolean removeItemFromCart(String cartId, String productId) {
        String sql = "DELETE FROM cart_items WHERE cart_id = ? AND product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, EncryptionUtil.decrypt(cartId));
            stmt.setInt(2, EncryptionUtil.decrypt(productId));

            int rowsAffected = stmt.executeUpdate();
            updateCartTimestamp(cartId);
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error removing item from cart: " + e.getMessage());
        }

        return false;
    }

    // Clear all items from cart
    public boolean clearCart(String cartId) {
        String sql = "DELETE FROM cart_items WHERE cart_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, EncryptionUtil.decrypt(cartId));

            int rowsAffected = stmt.executeUpdate();
            updateCartTimestamp(cartId);
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error clearing cart: " + e.getMessage());
        }

        return false;
    }

    // Update cart timestamp
    private void updateCartTimestamp(String cartId) {
        String sql = "UPDATE carts SET updated_at = CURRENT_TIMESTAMP WHERE cart_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, EncryptionUtil.decrypt(cartId));
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating cart timestamp: " + e.getMessage());
        }
    }
}