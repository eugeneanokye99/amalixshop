// OrderItem.java
package com.amalixshop.models;

public class OrderItem {
    private String orderItemId;
    private String orderId;
    private String productId;
    private int quantity;
    private double unitPrice;
    private double subtotal;
    private String productName; // For display purposes

    // Constructors
    public OrderItem() {}

    public OrderItem(String orderItemId, String orderId, String productId,
                     int quantity, double unitPrice) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = quantity * unitPrice;
    }

    // Getters and Setters
    public String getOrderItemId() { return orderItemId; }
    public void setOrderItemId(String orderItemId) { this.orderItemId = orderItemId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.subtotal = quantity * unitPrice;
    }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        this.subtotal = quantity * unitPrice;
    }

    public double getSubtotal() { return subtotal; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
}