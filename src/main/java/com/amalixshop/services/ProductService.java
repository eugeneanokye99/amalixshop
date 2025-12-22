package com.amalixshop.services;

import com.amalixshop.dao.ProductDAO;
import com.amalixshop.dao.InventoryDAO;
import com.amalixshop.models.Product;
import com.amalixshop.utils.EncryptionUtil;

import java.util.List;

public class ProductService {
    private final ProductDAO productDAO = new ProductDAO();
    private final InventoryDAO inventoryDAO = new InventoryDAO();

    public boolean saveProduct(Product product, int stockQuantity) {
        try {
            // 1. Save product
            int productId = productDAO.insertProduct(product);

            // 2. If product saved, create inventory entry
            if (productId > 0) {
                return inventoryDAO.createInventoryEntry(productId, stockQuantity);
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error saving product: " + e.getMessage());
            return false;
        }
    }

    public boolean updateProduct(Product product, int newStockQuantity) {
        try {
            // Update product details
            boolean productUpdated = productDAO.updateProduct(product) > 0;

            // Update inventory stock
            boolean inventoryUpdated = inventoryDAO.updateInventoryStock(
                    EncryptionUtil.decrypt(product.getProductId()),
                    newStockQuantity
            );

            return productUpdated && inventoryUpdated;
        } catch (Exception e) {
            System.err.println("Error updating product: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteProduct(int productId) {
        try {
            return productDAO.deleteProduct(productId) > 0;
        } catch (Exception e) {
            System.err.println("Error deleting product: " + e.getMessage());
            return false;
        }
    }

    public List<Product> getAllProducts() {
        try {
            return productDAO.getAllProducts();
        } catch (Exception e) {
            System.err.println("Error fetching products: " + e.getMessage());
            return List.of();
        }
    }

    public Product getProductById(int productId) {
        try {
            return productDAO.getProductById(productId);
        } catch (Exception e) {
            System.err.println("Error fetching product: " + e.getMessage());
            return null;
        }
    }


}