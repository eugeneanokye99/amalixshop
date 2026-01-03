package com.amalixshop.dao;

import com.amalixshop.models.Order;
import com.amalixshop.models.OrderItem;
import com.amalixshop.models.CartItem;
import com.amalixshop.database.DatabaseConnection;
import com.amalixshop.utils.EncryptionUtil;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    // Create a new order from cart
    public String createOrderFromCart(String customerId, String shippingAddress,
                                      String billingAddress, String paymentMethod) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Get cart and items
            CartDAO cartDAO = new CartDAO();
            List<CartItem> cartItems = getCartItemsForCustomer(customerId, conn);

            if (cartItems.isEmpty()) {
                System.out.println("No items in cart for customer: " + customerId);
                return null;
            }

            System.out.println("Found " + cartItems.size() + " items in cart for order creation");

            // 2. Calculate total
            double totalAmount = cartItems.stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();

            System.out.println("Order total calculated: " + totalAmount);

            // 3. Insert order
            String orderSql = "INSERT INTO orders (customer_id, total_amount, shipping_address, " +
                    "billing_address, payment_method, status, payment_status) " +
                    "VALUES (?, ?, ?, ?, ?, 'pending', 'pending') " +
                    "RETURNING order_id";

            try (PreparedStatement orderStmt = conn.prepareStatement(orderSql)) {
                orderStmt.setInt(1, EncryptionUtil.decrypt(customerId));
                orderStmt.setDouble(2, totalAmount);
                orderStmt.setString(3, shippingAddress);
                orderStmt.setString(4, billingAddress != null ? billingAddress : shippingAddress);
                orderStmt.setString(5, paymentMethod);

                System.out.println("Inserting order for customer: " + EncryptionUtil.decrypt(customerId));

                ResultSet rs = orderStmt.executeQuery();
                if (!rs.next()) {
                    System.err.println("Failed to get order ID after insertion");
                    conn.rollback();
                    return null;
                }

                int orderId = rs.getInt("order_id");
                String encryptedOrderId = EncryptionUtil.encrypt(orderId);
                System.out.println("Order created with ID: " + orderId + " (encrypted: " + encryptedOrderId + ")");

                // 4. Insert order items
                String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) " +
                        "VALUES (?, ?, ?, ?)";

                try (PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {
                    for (CartItem cartItem : cartItems) {
                        itemStmt.setInt(1, orderId);
                        itemStmt.setInt(2, EncryptionUtil.decrypt(cartItem.getProductId()));
                        itemStmt.setInt(3, cartItem.getQuantity());
                        itemStmt.setDouble(4, cartItem.getPrice());
                        itemStmt.addBatch();

                        System.out.println("Adding item to batch: " + cartItem.getProductName() +
                                " (Qty: " + cartItem.getQuantity() +
                                ", Price: " + cartItem.getPrice() + ")");
                    }
                    int[] batchResults = itemStmt.executeBatch();
                    System.out.println("Inserted " + batchResults.length + " order items");
                }

                // 5. Clear the cart
                String cartId = getCartId(customerId, conn);
                if (cartId != null) {
                    System.out.println("Clearing cart ID: " + cartId);
                    clearCart(cartId, conn);
                } else {
                    System.out.println("No cart found for customer");
                }

                conn.commit();
                System.out.println("Order creation transaction committed successfully");
                return encryptedOrderId;

            } catch (SQLException e) {
                System.err.println("SQL error during order creation: " + e.getMessage());
                e.printStackTrace();
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("Error creating order: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back: " + ex.getMessage());
            }
            return null;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    // Helper method to get cart items for a customer
    private List<CartItem> getCartItemsForCustomer(String customerId, Connection conn) throws SQLException {
        List<CartItem> items = new ArrayList<>();

        // First, get the cart ID for this customer
        String cartIdSql = "SELECT cart_id FROM carts WHERE customer_id = ?";
        int cartId = -1;

        try (PreparedStatement cartStmt = conn.prepareStatement(cartIdSql)) {
            cartStmt.setInt(1, EncryptionUtil.decrypt(customerId));

            try (ResultSet cartRs = cartStmt.executeQuery()) {
                if (cartRs.next()) {
                    cartId = cartRs.getInt("cart_id");
                } else {
                    System.out.println("No cart found for customer: " + customerId);
                    return items; // Return empty list
                }
            }
        }

        System.out.println("Found cart ID: " + cartId + " for customer");

        // Now get the cart items
        String sql = "SELECT ci.*, p.product_name, p.price " +
                "FROM cart_items ci " +
                "JOIN products p ON ci.product_id = p.product_id " +
                "WHERE ci.cart_id = ? " +
                "ORDER BY ci.added_at DESC";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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

                    System.out.println("Cart item: " + item.getProductName() +
                            " (Qty: " + item.getQuantity() +
                            ", Price: " + item.getPrice() + ")");
                }
            }
        }

        System.out.println("Total cart items found: " + items.size());
        return items;
    }

    // Helper method to get cart ID
    private String getCartId(String customerId, Connection conn) throws SQLException {
        String sql = "SELECT cart_id FROM carts WHERE customer_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, EncryptionUtil.decrypt(customerId));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return EncryptionUtil.encrypt(rs.getInt("cart_id"));
                }
            }
        }
        return null;
    }

    // Helper method to clear cart
    private void clearCart(String cartId, Connection conn) throws SQLException {
        String sql = "DELETE FROM cart_items WHERE cart_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, EncryptionUtil.decrypt(cartId));
            int rowsDeleted = stmt.executeUpdate();
            System.out.println("Cleared " + rowsDeleted + " items from cart");
        }

        // Also update cart timestamp
        updateCartTimestamp(cartId, conn);
    }

    // Helper method to update cart timestamp
    private void updateCartTimestamp(String cartId, Connection conn) throws SQLException {
        String sql = "UPDATE carts SET updated_at = CURRENT_TIMESTAMP WHERE cart_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, EncryptionUtil.decrypt(cartId));
            stmt.executeUpdate();
        }
    }

    // Get order by ID
    public Order getOrderById(String orderId) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, EncryptionUtil.decrypt(orderId));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order order = extractOrderFromResultSet(rs);

                    // Load order items
                    List<OrderItem> items = getOrderItems(rs.getInt("order_id"));
                    order.setItems(items);

                    return order;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching order: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Get orders by customer ID
    public List<Order> getOrdersByCustomerId(String customerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY order_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, EncryptionUtil.decrypt(customerId));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = extractOrderFromResultSet(rs);

                    // Load order items
                    List<OrderItem> items = getOrderItems(rs.getInt("order_id"));
                    order.setItems(items);

                    orders.add(order);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching orders: " + e.getMessage());
            e.printStackTrace();
        }

        return orders;
    }

    // Helper method to extract Order from ResultSet
    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        return new Order(
                EncryptionUtil.encrypt(rs.getInt("order_id")),
                EncryptionUtil.encrypt(rs.getInt("customer_id")),
                rs.getTimestamp("order_date") != null ?
                        rs.getTimestamp("order_date").toLocalDateTime() : null,
                rs.getDouble("total_amount"),
                rs.getString("shipping_address"),
                rs.getString("billing_address"),
                rs.getString("status"),
                rs.getString("payment_method"),
                rs.getString("payment_status"),
                rs.getString("notes")
        );
    }

    // Helper method to get order items
    private List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, p.product_name " +
                "FROM order_items oi " +
                "JOIN products p ON oi.product_id = p.product_id " +
                "WHERE oi.order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem(
                            EncryptionUtil.encrypt(rs.getInt("order_item_id")),
                            EncryptionUtil.encrypt(rs.getInt("order_id")),
                            EncryptionUtil.encrypt(rs.getInt("product_id")),
                            rs.getInt("quantity"),
                            rs.getDouble("unit_price")
                    );

                    item.setProductName(rs.getString("product_name"));
                    items.add(item);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching order items: " + e.getMessage());
            e.printStackTrace();
        }

        return items;
    }

    // Update order status
    public boolean updateOrderStatus(String orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, EncryptionUtil.decrypt(orderId));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating order status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Update payment status
    public boolean updatePaymentStatus(String orderId, String paymentStatus) {
        String sql = "UPDATE orders SET payment_status = ? WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, paymentStatus);
            stmt.setInt(2, EncryptionUtil.decrypt(orderId));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating payment status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Get recent orders (for dashboard)
    public List<Order> getRecentOrders(String customerId, int limit) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE customer_id = ? ORDER BY order_date DESC LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, EncryptionUtil.decrypt(customerId));
            stmt.setInt(2, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = extractOrderFromResultSet(rs);
                    orders.add(order);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching recent orders: " + e.getMessage());
            e.printStackTrace();
        }

        return orders;
    }

    // Get order statistics
    public OrderStatistics getOrderStatistics(String customerId) {
        OrderStatistics stats = new OrderStatistics();
        String sql = "SELECT " +
                "COUNT(*) as total_orders, " +
                "COALESCE(SUM(total_amount), 0) as total_spent, " +
                "COALESCE(AVG(total_amount), 0) as avg_order_value, " +
                "MAX(order_date) as last_order_date " +
                "FROM orders WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, EncryptionUtil.decrypt(customerId));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalOrders(rs.getInt("total_orders"));
                    stats.setTotalSpent(rs.getDouble("total_spent"));
                    stats.setAverageOrderValue(rs.getDouble("avg_order_value"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching order statistics: " + e.getMessage());
            e.printStackTrace();
        }

        return stats;
    }

    // Inner class for order statistics
    public static class OrderStatistics {
        private int totalOrders;
        private double totalSpent;
        private double averageOrderValue;
        private LocalDateTime lastOrderDate;

        // Getters and Setters
        public int getTotalOrders() { return totalOrders; }
        public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }

        public double getTotalSpent() { return totalSpent; }
        public void setTotalSpent(double totalSpent) { this.totalSpent = totalSpent; }

        public double getAverageOrderValue() { return averageOrderValue; }
        public void setAverageOrderValue(double averageOrderValue) { this.averageOrderValue = averageOrderValue; }

    }
}