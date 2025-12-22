package com.amalixshop.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ProductDetailController {

    @FXML private TextField quantityField;
    @FXML private Label totalPriceLabel;

    @FXML
    private void handleBack() {
        // TODO: Navigate back to products
        System.out.println("Back clicked");
    }

    @FXML
    private void handleAddToCart() {
        // TODO: Add product to cart
        System.out.println("Add to cart clicked");
    }

    @FXML
    private void handleBuyNow() {
        // TODO: Proceed to checkout
        System.out.println("Buy now clicked");
    }

    @FXML
    private void handleDecreaseQuantity() {
        // TODO: Decrease quantity
        System.out.println("Decrease quantity clicked");
    }

    @FXML
    private void handleIncreaseQuantity() {
        // TODO: Increase quantity
        System.out.println("Increase quantity clicked");
    }

    @FXML
    private void handleWriteReview() {
        // TODO: Open review dialog
        System.out.println("Write review clicked");
    }
}