package com.amalixshop.controllers;

import com.amalixshop.models.Order;
import com.amalixshop.models.OrderItem;
import com.amalixshop.services.OrderService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.amalixshop.utils.NavigationUtil;
import javafx.geometry.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderConfirmationController {

    @FXML private Label orderIdLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label shippingLabel;
    @FXML private Label totalLabel;
    @FXML private Label shippingAddressLabel;
    @FXML private Label paymentMethodLabel;
    @FXML private Label paymentStatusLabel;
    @FXML private Label deliveryDateLabel;
    @FXML private VBox orderItemsContainer;

    private String currentCustomerId;
    private String currentOrderId;
    private final OrderService orderService = new OrderService();

    @FXML
    private void initialize() {
        System.out.println("OrderConfirmationController initialized");
    }

    public void setCurrentCustomerId(String customerId) {
        this.currentCustomerId = customerId;
        System.out.println("Setting customer ID for order confirmation: " + customerId);
        loadOrderDetails();
    }

    public void setOrderId(String orderId) {
        this.currentOrderId = orderId;
        System.out.println("Setting order ID: " + orderId);

        // Update order ID label
        if (orderId != null && orderIdLabel != null) {
            // Extract numeric part from encrypted ID for display
            String displayId = "ORD" + orderId.substring(0, Math.min(8, orderId.length())).toUpperCase();
            orderIdLabel.setText("Order ID: " + displayId);
        }

        loadOrderDetails();
    }

    private void loadOrderDetails() {
        if (currentOrderId == null || currentCustomerId == null) {
            return;
        }

        try {
            // In a real app, you would fetch order details from service
            // For now, we'll simulate with current cart data
            displaySimulatedOrder();

        } catch (Exception e) {
            System.err.println("Error loading order details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void displaySimulatedOrder() {
        // Clear existing items
        orderItemsContainer.getChildren().clear();

        // Simulate order items (in real app, fetch from database)
        // You would typically have: orderService.getOrderById(currentOrderId)

        // For demo, let's show some sample items
        addOrderItem("4K Smart TV", 2, 6449.99);
        addOrderItem("Wireless Headphones", 1, 299.99);

        // Update totals
        double subtotal = 6449.99 * 2 + 299.99;
        double shipping = 0.0; // Free shipping
        double total = subtotal + shipping;

        subtotalLabel.setText(String.format("₵%.2f", subtotal));
        shippingLabel.setText("FREE");
        totalLabel.setText(String.format("₵%.2f", total));

        // Update shipping info
        shippingAddressLabel.setText("123 Main Street, Accra, Ghana\nPhone: +233 123 456 789");
        paymentMethodLabel.setText("Credit Card");
        paymentStatusLabel.setText("Paid");
        paymentStatusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        // Calculate delivery date (3-5 business days from now)
        LocalDateTime deliveryDate = LocalDateTime.now().plusDays(4);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        deliveryDateLabel.setText(deliveryDate.format(formatter));
    }

    private void addOrderItem(String productName, int quantity, double price) {
        HBox itemRow = new HBox(15);
        itemRow.setPadding(new Insets(10, 0, 10, 0));

        // Product name and quantity
        VBox productInfo = new VBox(5);
        productInfo.setPrefWidth(300);

        Label nameLabel = new Label(productName);
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        nameLabel.setWrapText(true);

        Label quantityLabel = new Label("Qty: " + quantity);
        quantityLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        productInfo.getChildren().addAll(nameLabel, quantityLabel);

        // Price per unit
        Label unitPriceLabel = new Label(String.format("₵%.2f", price));
        unitPriceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        unitPriceLabel.setPrefWidth(100);

        // Item total
        double itemTotal = price * quantity;
        Label totalLabel = new Label(String.format("₵%.2f", itemTotal));
        totalLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        totalLabel.setPrefWidth(100);

        itemRow.getChildren().addAll(productInfo, unitPriceLabel, totalLabel);
        orderItemsContainer.getChildren().add(itemRow);
    }

    @FXML
    private void handleViewOrderDetails() {
        // Show order details in a dialog or new window
        showAlert("Order Details",
                "Order ID: " + (currentOrderId != null ? currentOrderId : "N/A") +
                        "\nCustomer: " + (currentCustomerId != null ? currentCustomerId : "N/A") +
                        "\n\nView your full order history in the 'My Orders' section.");
    }

    @FXML
    private void handleContinueShopping() {
        Stage stage = (Stage) orderIdLabel.getScene().getWindow();
        NavigationUtil.navigateToDashboard(stage, currentCustomerId);
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION
        );
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to refresh order details if needed
    public void refreshOrderDetails() {
        loadOrderDetails();
    }
}