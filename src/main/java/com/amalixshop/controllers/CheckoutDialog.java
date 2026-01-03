// CheckoutDialog.java
package com.amalixshop.controllers;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.util.Pair;

public class CheckoutDialog extends Dialog<CheckoutDialog.CheckoutInfo> {

    public CheckoutDialog() {
        setTitle("Checkout");
        setHeaderText("Enter your shipping and billing information");

        // Create form fields
        TextField shippingField = new TextField();
        shippingField.setPromptText("Full shipping address");
        shippingField.setPrefWidth(300);

        TextField billingField = new TextField();
        billingField.setPromptText("Billing address (same as shipping if empty)");
        billingField.setPrefWidth(300);

        ComboBox<String> paymentMethod = new ComboBox<>();
        paymentMethod.getItems().addAll("Credit Card", "Debit Card", "Mobile Money", "Cash on Delivery");
        paymentMethod.setValue("Credit Card");

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Shipping Address:"), 0, 0);
        grid.add(shippingField, 1, 0);
        grid.add(new Label("Billing Address:"), 0, 1);
        grid.add(billingField, 1, 1);
        grid.add(new Label("Payment Method:"), 0, 2);
        grid.add(paymentMethod, 1, 2);

        getDialogPane().setContent(grid);

        // Buttons
        ButtonType checkoutButton = new ButtonType("Place Order", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(checkoutButton, ButtonType.CANCEL);

        // Result converter
        setResultConverter(dialogButton -> {
            if (dialogButton == checkoutButton) {
                return new CheckoutInfo(
                        shippingField.getText(),
                        billingField.getText().isEmpty() ? shippingField.getText() : billingField.getText(),
                        paymentMethod.getValue()
                );
            }
            return null;
        });
    }

    // Simple data class for checkout info
    public static class CheckoutInfo {
        private final String shippingAddress;
        private final String billingAddress;
        private final String paymentMethod;

        public CheckoutInfo(String shippingAddress, String billingAddress, String paymentMethod) {
            this.shippingAddress = shippingAddress;
            this.billingAddress = billingAddress;
            this.paymentMethod = paymentMethod;
        }

        public String getShippingAddress() { return shippingAddress; }
        public String getBillingAddress() { return billingAddress; }
        public String getPaymentMethod() { return paymentMethod; }
    }
}