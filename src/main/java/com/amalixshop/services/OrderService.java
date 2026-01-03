package com.amalixshop.services;

import com.amalixshop.dao.OrderDAO;
import com.amalixshop.models.Order;
import java.util.List;

public class OrderService {
    private final OrderDAO orderDAO = new OrderDAO();

    // Create order from cart
    public String createOrderFromCart(String customerId, String shippingAddress,
                                      String billingAddress, String paymentMethod) {
        if (customerId == null || customerId.isEmpty()) {
            return null;
        }

        if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
            return null;
        }

        // If billing address is not provided, use shipping address
        if (billingAddress == null || billingAddress.trim().isEmpty()) {
            billingAddress = shippingAddress;
        }

        return orderDAO.createOrderFromCart(customerId, shippingAddress,
                billingAddress, paymentMethod);
    }

    // Get order by ID
    public Order getOrderById(String orderId) {
        return orderDAO.getOrderById(orderId);
    }

    // Get orders by customer ID
    public List<Order> getOrdersByCustomerId(String customerId) {
        return orderDAO.getOrdersByCustomerId(customerId);
    }

    // Update order status
    public boolean updateOrderStatus(String orderId, String status) {
        return orderDAO.updateOrderStatus(orderId, status);
    }

    // Update payment status
    public boolean updatePaymentStatus(String orderId, String paymentStatus) {
        return orderDAO.updatePaymentStatus(orderId, paymentStatus);
    }

    // Get recent orders
    public List<Order> getRecentOrders(String customerId, int limit) {
        return orderDAO.getRecentOrders(customerId, limit);
    }

    // Get order statistics
    public OrderDAO.OrderStatistics getOrderStatistics(String customerId) {
        return orderDAO.getOrderStatistics(customerId);
    }
}