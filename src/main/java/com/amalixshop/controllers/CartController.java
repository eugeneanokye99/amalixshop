package com.amalixshop.controllers;

import com.amalixshop.models.Cart;
import com.amalixshop.models.CartItem;
import com.amalixshop.services.CartService;
import com.amalixshop.services.OrderService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.amalixshop.utils.NavigationUtil;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Optional;

public class CartController {

    @FXML private VBox itemsContainer;
    @FXML private VBox cartSummary;
    @FXML private VBox emptyCartView;
    @FXML private Label messageLabel;
    @FXML private Label cartTitleLabel;
    @FXML private Label totalItemsLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label shippingLabel;
    @FXML private Label totalLabel;
    @FXML private Button clearCartButton;

    private String currentCustomerId;
    private final CartService cartService = new CartService();
    private Cart currentCart;

    @FXML
    private void initialize() {
        System.out.println("CartController initialized");

        // Initialize UI state
        messageLabel.setText("");
        cartSummary.setVisible(false);
//        emptyCartView.setVisible(false);
        clearCartButton.setVisible(false);
    }

    @FXML
    private void handleBackToDashboard() {
        Stage stage = (Stage) messageLabel.getScene().getWindow();
        NavigationUtil.navigateToDashboard(stage, currentCustomerId);
    }

    @FXML
    private void handleCheckout() {
        if (currentCustomerId == null) {
            showError("Please login to checkout");
            return;
        }

        if (currentCart == null || currentCart.getItems().isEmpty()) {
            showError("Your cart is empty");
            return;
        }

        // Show checkout dialog to get shipping/billing info
        CheckoutDialog dialog = new CheckoutDialog();
        Optional<CheckoutDialog.CheckoutInfo> result = dialog.showAndWait();

        if (result.isPresent()) {
            CheckoutDialog.CheckoutInfo info = result.get();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Proceed to Checkout");
            alert.setHeaderText("Ready to checkout?");
            alert.setContentText("You have " + currentCart.getItems().size() + " items in your cart totaling ₵" +
                    String.format("%.2f", currentCart.getTotalAmount()) +
                    "\n\nShipping to: " + info.getShippingAddress());

            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                try {
                    // Create order from cart
                    OrderService orderService = new OrderService();
                    String orderId = orderService.createOrderFromCart(
                            currentCustomerId,
                            info.getShippingAddress(),
                            info.getBillingAddress(),
                            info.getPaymentMethod()
                    );

                    if (orderId != null) {
                        showMessage("Order created successfully! Order ID: " + orderId);

                        // Clear cart and refresh
                        loadCartItems();

                        // Navigate to order confirmation page
                        Stage stage = (Stage) messageLabel.getScene().getWindow();
                        NavigationUtil.navigateToOrderConfirmation(stage, currentCustomerId, orderId);
                    } else {
                        showError("Failed to create order. Please try again.");
                    }
                } catch (Exception e) {
                    showError("Error during checkout: " + e.getMessage());
                    System.err.println("Checkout error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void handleClearCart() {
        if (currentCustomerId == null) {
            showError("Please login to modify cart");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear Cart");
        alert.setHeaderText("Clear Shopping Cart");
        alert.setContentText("Are you sure you want to remove all items from your cart?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                boolean success = cartService.clearCart(currentCustomerId);

                if (success) {
                    loadCartItems();
                    showMessage("Cart cleared successfully!");
                } else {
                    showError("Failed to clear cart");
                }
            } catch (Exception e) {
                showError("Failed to clear cart: " + e.getMessage());
                System.err.println("Error clearing cart: " + e.getMessage());
            }
        }
    }

    public void setCurrentCustomerId(String customerId) {
        this.currentCustomerId = customerId;
        System.out.println("Setting customer ID for cart: " + customerId);
        loadCartItems();
    }

    private void loadCartItems() {
        itemsContainer.getChildren().clear();

        if (currentCustomerId == null) {
            showEmptyCart();
            messageLabel.setText("Please login to view your cart");
            return;
        }

        try {
            currentCart = cartService.getOrCreateCart(currentCustomerId);

            if (currentCart == null) {
                showError("Failed to load cart");
                showEmptyCart();
                return;
            }

            List<CartItem> items = currentCart.getItems();

            if (items == null || items.isEmpty()) {
                showEmptyCart();
                return;
            }

            // Update cart title with item count
            int totalItems = items.stream().mapToInt(CartItem::getQuantity).sum();
            cartTitleLabel.setText("Shopping Cart (" + totalItems + " items)");

            // Display cart items
            displayCartItems(items);

            // Update cart summary
            updateCartSummary();

            // Show clear cart button
            clearCartButton.setVisible(true);

        } catch (Exception e) {
            showError("Error loading cart: " + e.getMessage());
            System.err.println("Error loading cart: " + e.getMessage());
            showEmptyCart();
        }
    }

    private void displayCartItems(List<CartItem> items) {
        for (CartItem item : items) {
            HBox itemCard = createCartItemCard(item);
            itemsContainer.getChildren().add(itemCard);
        }

        cartSummary.setVisible(true);
//        emptyCartView.setVisible(false);
    }

    private HBox createCartItemCard(CartItem item) {
        HBox card = new HBox(15);
        card.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 8; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-radius: 8; " +
                "-fx-border-width: 1; " +
                "-fx-padding: 15;");
        card.setPrefHeight(120);

        // Product Details
        VBox details = new VBox(8);
        details.setPrefWidth(300);

        Label nameLabel = new Label(item.getProductName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        nameLabel.setWrapText(true);

        Label priceLabel = new Label("₵" + String.format("%.2f", item.getPrice()));
        priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        details.getChildren().addAll(nameLabel, priceLabel);

        // Quantity Controls
        HBox quantityBox = new HBox(10);
        quantityBox.setAlignment(Pos.CENTER);

        Button decreaseBtn = new Button("-");
        decreaseBtn.setStyle("-fx-background-color: #dfe6e9; " +
                "-fx-background-radius: 4; " +
                "-fx-min-width: 30; " +
                "-fx-min-height: 30; " +
                "-fx-cursor: hand;");
        decreaseBtn.setOnAction(e -> updateQuantity(item.getProductId(), -1));

        Label quantityLabel = new Label(String.valueOf(item.getQuantity()));
        quantityLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-min-width: 30; -fx-alignment: center;");

        Button increaseBtn = new Button("+");
        increaseBtn.setStyle("-fx-background-color: #dfe6e9; " +
                "-fx-background-radius: 4; " +
                "-fx-min-width: 30; " +
                "-fx-min-height: 30; " +
                "-fx-cursor: hand;");
        increaseBtn.setOnAction(e -> updateQuantity(item.getProductId(), 1));

        quantityBox.getChildren().addAll(decreaseBtn, quantityLabel, increaseBtn);

        // Item Total
        double itemTotal = item.getPrice() * item.getQuantity();
        Label itemTotalLabel = new Label("₵" + String.format("%.2f", itemTotal));
        itemTotalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Remove Button
        Button removeBtn = new Button("✕");
        removeBtn.setStyle("-fx-background-color: transparent; " +
                "-fx-text-fill: #e74c3c; " +
                "-fx-font-size: 16px; " +
                "-fx-cursor: hand;");
        removeBtn.setOnAction(e -> removeItem(item.getProductId(), item.getProductName()));

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(details, quantityBox, spacer, itemTotalLabel, removeBtn);

        // Add hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: #f8f9fa; " +
                    "-fx-background-radius: 8; " +
                    "-fx-border-color: #3498db; " +
                    "-fx-border-radius: 8; " +
                    "-fx-border-width: 1; " +
                    "-fx-padding: 15;");
        });

        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; " +
                    "-fx-background-radius: 8; " +
                    "-fx-border-color: #e0e0e0; " +
                    "-fx-border-radius: 8; " +
                    "-fx-border-width: 1; " +
                    "-fx-padding: 15;");
        });

        return card;
    }

    private void updateQuantity(String productId, int change) {
        if (currentCustomerId == null) {
            showError("Please login to update cart");
            return;
        }

        try {
            // Get the current quantity from the currentCart object
            int currentQuantity = 0;
            if (currentCart != null && currentCart.getItems() != null) {
                for (CartItem item : currentCart.getItems()) {
                    if (item.getProductId().equals(productId)) {
                        currentQuantity = item.getQuantity();
                        break;
                    }
                }
            }

            int newQuantity = currentQuantity + change;

            if (newQuantity < 1) {
                // Find the product name
                String productName = "this item";
                if (currentCart != null) {
                    for (CartItem item : currentCart.getItems()) {
                        if (item.getProductId().equals(productId)) {
                            productName = item.getProductName();
                            break;
                        }
                    }
                }
                removeItem(productId, productName);
                return;
            }

            boolean success = cartService.updateCartItemQuantity(currentCustomerId, productId, newQuantity);

            if (success) {
                loadCartItems(); // Refresh the entire cart
                showMessage("Quantity updated");
            } else {
                showError("Failed to update quantity");
            }
        } catch (Exception e) {
            showError("Failed to update quantity: " + e.getMessage());
            System.err.println("Error updating quantity: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void removeItem(String productId, String productName) {
        if (currentCustomerId == null) {
            showError("Please login to modify cart");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove Item");
        alert.setHeaderText("Remove from cart");
        alert.setContentText("Remove \"" + productName + "\" from your cart?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                boolean success = cartService.removeFromCart(currentCustomerId, productId);

                if (success) {
                    loadCartItems(); // Refresh the cart
                    showMessage(productName + " removed from cart");
                } else {
                    showError("Failed to remove item");
                }
            } catch (Exception e) {
                showError("Failed to remove item: " + e.getMessage());
                System.err.println("Error removing item: " + e.getMessage());
            }
        }
    }

    private void updateCartSummary() {
        if (currentCustomerId == null) {
            return;
        }

        try {
            double cartTotal = cartService.getCartTotal(currentCustomerId);
            int itemCount = cartService.getCartItemCount(currentCustomerId);

            // Calculate shipping (free over ₵50)
            final double FREE_SHIPPING_THRESHOLD = 50.0;
            final double SHIPPING_RATE = 5.99;
            double shipping = cartTotal >= FREE_SHIPPING_THRESHOLD ? 0 : SHIPPING_RATE;
            double total = cartTotal + shipping;

            // Update labels
            totalItemsLabel.setText(String.valueOf(itemCount));
            subtotalLabel.setText("₵" + String.format("%.2f", cartTotal));
            shippingLabel.setText(shipping == 0 ? "FREE" : "₵" + String.format("%.2f", shipping));
            totalLabel.setText("₵" + String.format("%.2f", total));

            // Show free shipping message if needed
            if (cartTotal > 0 && cartTotal < FREE_SHIPPING_THRESHOLD) {
                double amountNeeded = FREE_SHIPPING_THRESHOLD - cartTotal;
                showMessage("Add ₵" + String.format("%.2f", amountNeeded) + " more for FREE shipping!");
            }

        } catch (Exception e) {
            System.err.println("Error updating cart summary: " + e.getMessage());
        }
    }

    private void showEmptyCart() {
        cartSummary.setVisible(false);
//        emptyCartView.setVisible(true);
        clearCartButton.setVisible(false);
        cartTitleLabel.setText("Shopping Cart");
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #27ae60; -fx-background-color: #d4edda;");
    }

    private void showError(String error) {
        messageLabel.setText("Error: " + error);
        messageLabel.setStyle("-fx-text-fill: #721c24; -fx-background-color: #f8d7da;");
    }

    // Method to refresh cart from other controllers
    public void refreshCart() {
        loadCartItems();
    }
}