package com.amalixshop.models;

import java.time.LocalDateTime;

public class CartItem {
    private String cartItemId;
    private String cartId;
    private String productId;
    private int quantity;
    private String productName;
    private double price;

    public CartItem(String cartItemId, String cartId, String productId, int quantity) {
        this.cartItemId = cartItemId;
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }



    // GETTERS ONLY
    public String getCartItemId() {
        return cartItemId;
    }

    public String getCartId() {
        return cartId;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public double getSubtotal() {
        return price * quantity;
    }
}