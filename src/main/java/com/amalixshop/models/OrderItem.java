package com.amalixshop.models;

public class OrderItem {
    private final int orderItemId;
    private final int orderId;
    private final int productId;
    private final int quantity;
    private final double unitPrice;

    public OrderItem(int orderItemId, int orderId, int productId,
                     int quantity, double unitPrice) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Getters only
    public int getOrderItemId() { return orderItemId; }
    public int getOrderId() { return orderId; }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getSubtotal() { return quantity * unitPrice; }
}