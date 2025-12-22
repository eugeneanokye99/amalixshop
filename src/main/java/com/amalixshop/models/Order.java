package com.amalixshop.models;

import java.sql.Timestamp;

public class Order {
    private final int orderId;
    private final int customerId;
    private final Timestamp orderDate;
    private final double totalAmount;
    private final String shippingAddress;
    private final String billingAddress;
    private final String status;
    private final String paymentMethod;
    private final String paymentStatus;
    private final String notes;

    public Order(int orderId, int customerId, Timestamp orderDate, double totalAmount,
                 String shippingAddress, String billingAddress, String status,
                 String paymentMethod, String paymentStatus, String notes) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.billingAddress = billingAddress;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.notes = notes;
    }

    // Getters only
    public int getOrderId() { return orderId; }
    public int getCustomerId() { return customerId; }
    public Timestamp getOrderDate() { return orderDate; }
    public double getTotalAmount() { return totalAmount; }
    public String getShippingAddress() { return shippingAddress; }
    public String getBillingAddress() { return billingAddress; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getNotes() { return notes; }
}