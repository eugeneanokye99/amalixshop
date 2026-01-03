package com.amalixshop.services;

import com.amalixshop.dao.CartDAO;
import com.amalixshop.models.Cart;
import com.amalixshop.models.CartItem;


public class CartService {
    private final CartDAO cartDAO = new CartDAO();

    // Get or create cart for customer
    public Cart getOrCreateCart(String customerId) {
        Cart cart = cartDAO.getCartByCustomerId(customerId);

        if (cart == null) {
            String cartId = cartDAO.createCart(customerId);
            if (cartId != null) {
                cart = new Cart(cartId, customerId);
            }
        }

        return cart;
    }

    // Get cart by customer ID
    public Cart getCartByCustomerId(String customerId) {
        return cartDAO.getCartByCustomerId(customerId);
    }

    // Add product to cart
    public boolean addToCart(String customerId, String productId, int quantity) {
        if (quantity <= 0) {
            return false;
        }

        Cart cart = getOrCreateCart(customerId);
        if (cart == null) {
            return false;
        }

        String cartId = cart.getCartId();
        return cartDAO.addItemToCart(cartId, productId, quantity);
    }

    // Update item quantity in cart
    public boolean updateCartItemQuantity(String customerId, String productId, int quantity) {
        if (quantity <= 0) {
            return removeFromCart(customerId, productId);
        }

        Cart cart = getCartByCustomerId(customerId);
        if (cart == null) {
            return false;
        }

        String cartId = cart.getCartId();
        return cartDAO.updateItemQuantity(cartId, productId, quantity);
    }

    // Remove item from cart
    public boolean removeFromCart(String customerId, String productId) {
        Cart cart = getCartByCustomerId(customerId);
        if (cart == null) {
            return false;
        }

        String cartId = cart.getCartId();
        return cartDAO.removeItemFromCart(cartId, productId);
    }

    // Get cart items count
    public int getCartItemCount(String customerId) {
        Cart cart = getCartByCustomerId(customerId);
        if (cart == null) {
            return 0;
        }

        return cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    // Get cart total amount
    public double getCartTotal(String customerId) {
        Cart cart = getCartByCustomerId(customerId);
        if (cart == null) {
            return 0.0;
        }

        return cart.getTotalAmount();
    }

    // Clear entire cart
    public boolean clearCart(String customerId) {
        Cart cart = getCartByCustomerId(customerId);
        if (cart == null) {
            return false;
        }

        String cartId = cart.getCartId();
        return cartDAO.clearCart(cartId);
    }

    // Check if product is in cart
    public boolean isProductInCart(String customerId, String productId) {
        Cart cart = getCartByCustomerId(customerId);
        if (cart == null) {
            return false;
        }

        return cart.getItems().stream()
                .anyMatch(item -> item.getProductId().equals(productId));
    }

    // Get cart item quantity for a product
    public int getProductQuantityInCart(String customerId, String productId) {
        Cart cart = getCartByCustomerId(customerId);
        if (cart == null) {
            return 0;
        }

        return cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .mapToInt(CartItem::getQuantity)
                .findFirst()
                .orElse(0);
    }
}