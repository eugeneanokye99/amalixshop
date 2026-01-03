package com.amalixshop.models;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private String cartId;
    private String customerId;
    private List<CartItem> items;
    private double totalAmount;

    public Cart() {
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
    }

    public Cart(String cartId, String customerId) {
        this();
        this.cartId = cartId;
        this.customerId = customerId;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    // GETTERS ONLY
    public String getCartId() {
        return cartId;
    }

    public String getCustomerId() {
        return customerId;
    }


    public List<CartItem> getItems() {
        return items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void calculateTotal() {
        this.totalAmount = items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }
}