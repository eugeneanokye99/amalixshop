package com.amalixshop.controllers;

import com.amalixshop.models.Order;
import com.amalixshop.models.OrderItem;
import com.amalixshop.services.OrderService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.amalixshop.utils.NavigationUtil;
import javafx.geometry.Insets;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrdersController {

    @FXML private Label titleLabel;
    @FXML private VBox ordersContainer;
    @FXML private VBox noOrdersContainer;
    @FXML private Label noOrdersMessage;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private ComboBox<String> sortCombo;
    @FXML private TextField searchField;
    @FXML private Label statsLabel;

    private String currentCustomerId;
    private final OrderService orderService = new OrderService();
    private List<Order> allOrders;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    @FXML
    private void initialize() {
        System.out.println("OrdersController initialized");

        // Initialize filter options
        statusFilterCombo.getItems().addAll("All", "Pending", "Confirmed", "Processing",
                "Shipped", "Delivered", "Cancelled");
        statusFilterCombo.setValue("All");

        sortCombo.getItems().addAll("Newest First", "Oldest First", "Price: High to Low",
                "Price: Low to High");
        sortCombo.setValue("Newest First");

        // Set up event listeners
        statusFilterCombo.setOnAction(e -> loadOrders());
        sortCombo.setOnAction(e -> loadOrders());

        // Search field listener with debounce
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Debounce search - wait for user to stop typing
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            javafx.application.Platform.runLater(() -> loadOrders());
                        }
                    },
                    500 // 500ms delay
            );
        });
    }

    public void setCurrentCustomerId(String customerId) {
        this.currentCustomerId = customerId;
        System.out.println("Setting customer ID for orders: " + customerId);
        titleLabel.setText("My Orders");
        loadOrders();
    }

    private void loadOrders() {
        ordersContainer.getChildren().clear();

        if (currentCustomerId == null || currentCustomerId.isEmpty()) {
            if (noOrdersContainer != null) {
                noOrdersContainer.setVisible(true);
            }
            if (noOrdersMessage != null) {
                noOrdersMessage.setText("Please login to view your orders");
            }
            statsLabel.setText("");
            return;
        }

        try {
            allOrders = orderService.getOrdersByCustomerId(currentCustomerId);

            if (allOrders == null || allOrders.isEmpty()) {
                if (noOrdersContainer != null) {
                    noOrdersContainer.setVisible(true);
                }
                if (noOrdersMessage != null) {
                    noOrdersMessage.setText("You haven't placed any orders yet");
                }
                statsLabel.setText("0 orders found");
                return;
            }

            // Apply filters
            List<Order> filteredOrders = applyFilters(allOrders);

            if (filteredOrders.isEmpty()) {
                if (noOrdersContainer != null) {
                    noOrdersContainer.setVisible(true);
                }
                if (noOrdersMessage != null) {
                    noOrdersMessage.setText("No orders match your filters");
                }
            } else {
                if (noOrdersContainer != null) {
                    noOrdersContainer.setVisible(false);
                }
                displayOrders(filteredOrders);
            }

            updateStats(filteredOrders);

        } catch (Exception e) {
            System.err.println("Error loading orders: " + e.getMessage());
            e.printStackTrace();
            noOrdersMessage.setText("Error loading orders. Please try again.");
            noOrdersContainer.setVisible(true);
        }
    }

    private List<Order> applyFilters(List<Order> orders) {
        List<Order> filtered = orders;

        // Apply status filter
        String statusFilter = statusFilterCombo.getValue();
        if (!"All".equals(statusFilter)) {
            filtered = filtered.stream()
                    .filter(order -> statusFilter.equalsIgnoreCase(order.getStatus()))
                    .toList();
        }

        // Apply search filter
        String searchText = searchField.getText().trim().toLowerCase();
        if (!searchText.isEmpty()) {
            filtered = filtered.stream()
                    .filter(order -> order.getOrderId().toLowerCase().contains(searchText) ||
                            String.valueOf(order.getTotalAmount()).contains(searchText) ||
                            order.getItems().stream().anyMatch(item ->
                                    item.getProductName().toLowerCase().contains(searchText)))
                    .toList();
        }

        // Apply sorting
        String sortOption = sortCombo.getValue();
        filtered.sort((o1, o2) -> {
            switch (sortOption) {
                case "Oldest First":
                    return o1.getOrderDate().compareTo(o2.getOrderDate());
                case "Price: High to Low":
                    return Double.compare(o2.getTotalAmount(), o1.getTotalAmount());
                case "Price: Low to High":
                    return Double.compare(o1.getTotalAmount(), o2.getTotalAmount());
                case "Newest First":
                default:
                    return o2.getOrderDate().compareTo(o1.getOrderDate());
            }
        });

        return filtered;
    }

    private void displayOrders(List<Order> orders) {
        for (Order order : orders) {
            VBox orderCard = createOrderCard(order);
            ordersContainer.getChildren().add(orderCard);
        }
    }

    private VBox createOrderCard(Order order) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                "-fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-border-width: 1; " +
                "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        // Order header
        HBox header = new HBox();
        header.setSpacing(10);

        // Order ID and date
        VBox orderInfo = new VBox(5);
        Label orderIdLabel = new Label("Order #" + getDisplayOrderId(order.getOrderId()));
        orderIdLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label dateLabel = new Label("Placed on: " + order.getOrderDate().format(dateFormatter));
        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        orderInfo.getChildren().addAll(orderIdLabel, dateLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Order status and total
        VBox statusInfo = new VBox(5);
        statusInfo.setAlignment(javafx.geometry.Pos.TOP_RIGHT);

        Label statusLabel = new Label(getStatusText(order.getStatus()));
        statusLabel.setStyle(getStatusStyle(order.getStatus()));

        Label totalLabel = new Label("Total: ₵" + String.format("%.2f", order.getTotalAmount()));
        totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        statusInfo.getChildren().addAll(statusLabel, totalLabel);

        header.getChildren().addAll(orderInfo, spacer, statusInfo);

        // Order items preview
        VBox itemsPreview = createItemsPreview(order.getItems());

        // Action buttons
        HBox actions = new HBox(10);
        actions.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        Button viewDetailsBtn = new Button("View Details");
        viewDetailsBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-background-radius: 4; -fx-padding: 8 20;");
        viewDetailsBtn.setOnAction(e -> viewOrderDetails(order));

        Button trackBtn = new Button("Track Order");
        trackBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-background-radius: 4; -fx-padding: 8 20;");
        trackBtn.setOnAction(e -> trackOrder(order));

        if ("delivered".equalsIgnoreCase(order.getStatus()) ||
                "cancelled".equalsIgnoreCase(order.getStatus())) {
            trackBtn.setDisable(true);
            trackBtn.setText("Completed");
        }

        Button reorderBtn = new Button("Reorder");
        reorderBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-background-radius: 4; -fx-padding: 8 20;");
        reorderBtn.setOnAction(e -> reorderItems(order));

        actions.getChildren().addAll(viewDetailsBtn, trackBtn, reorderBtn);

        card.getChildren().addAll(header, itemsPreview, actions);

        // Add hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8; " +
                    "-fx-border-color: #3498db; -fx-border-radius: 8; -fx-border-width: 1; " +
                    "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(52,152,219,0.2), 8, 0, 0, 3);");
        });

        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                    "-fx-border-color: #e0e0e0; -fx-border-radius: 8; -fx-border-width: 1; " +
                    "-fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        });

        return card;
    }

    private VBox createItemsPreview(List<OrderItem> items) {
        VBox preview = new VBox(8);

        // Show first 3 items or all if less than 3
        int showCount = Math.min(3, items.size());
        for (int i = 0; i < showCount; i++) {
            OrderItem item = items.get(i);
            HBox itemRow = new HBox(10);

            Label nameLabel = new Label(item.getProductName());
            nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
            nameLabel.setWrapText(true);
            nameLabel.setPrefWidth(300);

            Label quantityLabel = new Label("Qty: " + item.getQuantity());
            quantityLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
            quantityLabel.setPrefWidth(80);

            Label priceLabel = new Label("₵" + String.format("%.2f", item.getUnitPrice()));
            priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
            priceLabel.setPrefWidth(100);

            itemRow.getChildren().addAll(nameLabel, quantityLabel, priceLabel);
            preview.getChildren().add(itemRow);
        }

        // Show "and X more..." if there are more items
        if (items.size() > 3) {
            Label moreLabel = new Label("and " + (items.size() - 3) + " more items...");
            moreLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #3498db; -fx-font-style: italic;");
            preview.getChildren().add(moreLabel);
        }

        return preview;
    }

    private void viewOrderDetails(Order order) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Details");
        alert.setHeaderText("Order #" + getDisplayOrderId(order.getOrderId()));

        StringBuilder content = new StringBuilder();
        content.append("Order Date: ").append(order.getOrderDate().format(dateFormatter)).append("\n");
        content.append("Status: ").append(getStatusText(order.getStatus())).append("\n");
        content.append("Total Amount: ₵").append(String.format("%.2f", order.getTotalAmount())).append("\n");
        content.append("Payment Method: ").append(order.getPaymentMethod()).append("\n");
        content.append("Payment Status: ").append(order.getPaymentStatus()).append("\n\n");
        content.append("Shipping Address:\n").append(order.getShippingAddress()).append("\n\n");

        content.append("Order Items:\n");
        for (OrderItem item : order.getItems()) {
            content.append("- ").append(item.getProductName())
                    .append(" (Qty: ").append(item.getQuantity())
                    .append(", ₵").append(String.format("%.2f", item.getUnitPrice())).append(" each)\n");
        }

        alert.setContentText(content.toString());
        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(400, 400);
        alert.showAndWait();
    }

    private void trackOrder(Order order) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Track Order");
        alert.setHeaderText("Order #" + getDisplayOrderId(order.getOrderId()));
        alert.setContentText("Tracking information for " + getStatusText(order.getStatus()) +
                " orders will be available soon.\n\n" +
                "Estimated delivery date: Within 3-5 business days");
        alert.showAndWait();
    }

    private void reorderItems(Order order) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Reorder Items");
        confirmAlert.setHeaderText("Add all items to cart?");
        confirmAlert.setContentText("This will add " + order.getItems().size() +
                " items from this order to your cart.");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // In real app: Add each item to cart
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Reorder Successful");
            successAlert.setContentText("All items have been added to your cart.");
            successAlert.showAndWait();
        }
    }

    @FXML
    private void handleClearFilters() {
        statusFilterCombo.setValue("All");
        sortCombo.setValue("Newest First");
        searchField.clear();
        loadOrders();
    }

    @FXML
    private void handleBackToDashboard() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        NavigationUtil.navigateToDashboard(stage, currentCustomerId);
    }

    @FXML
    private void handleRefresh() {
        loadOrders();
        showMessage("Orders list refreshed");
    }

    private void showMessage(String message) {
        // You could add a temporary message label like in CartController
        System.out.println("Message: " + message);
    }

    private void updateStats(List<Order> orders) {
        int totalOrders = orders.size();
        double totalSpent = orders.stream().mapToDouble(Order::getTotalAmount).sum();

        statsLabel.setText(totalOrders + " orders • Total spent: ₵" +
                String.format("%.2f", totalSpent));
    }

    private String getDisplayOrderId(String encryptedId) {
        // Extract first 8 characters for display
        return encryptedId.substring(0, Math.min(8, encryptedId.length())).toUpperCase();
    }

    private String getStatusText(String status) {
        if (status == null) return "Pending";
        return status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
    }

    private String getStatusStyle(String status) {
        if (status == null) status = "pending";

        return switch (status.toLowerCase()) {
            case "delivered" -> "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #27ae60;";
            case "shipped" -> "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #3498db;";
            case "processing" -> "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #f39c12;";
            case "confirmed" -> "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #9b59b6;";
            case "cancelled" -> "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;";
            default -> "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #f39c12;"; // pending
        };
    }


}