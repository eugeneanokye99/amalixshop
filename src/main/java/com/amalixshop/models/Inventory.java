package com.amalixshop.models;

import java.sql.Timestamp;

public class Inventory {
    private final int inventoryId;
    private final int productId;
    private final int stockQuantity;
    private final int lowStockThreshold;
    private final Timestamp lastRestocked;
    private final Timestamp createdAt;
    private final Timestamp updatedAt;

    public Inventory(int inventoryId, int productId, int stockQuantity,
                     int lowStockThreshold, Timestamp lastRestocked,
                     Timestamp createdAt, Timestamp updatedAt) {
        this.inventoryId = inventoryId;
        this.productId = productId;
        this.stockQuantity = stockQuantity;
        this.lowStockThreshold = lowStockThreshold;
        this.lastRestocked = lastRestocked;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters only
    public int getInventoryId() { return inventoryId; }
    public int getProductId() { return productId; }
    public int getStockQuantity() { return stockQuantity; }
    public int getLowStockThreshold() { return lowStockThreshold; }
    public Timestamp getLastRestocked() { return lastRestocked; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }

    public boolean isLowStock() {
        return stockQuantity <= lowStockThreshold;
    }

    public boolean isOutOfStock() {
        return stockQuantity == 0;
    }
}