package com.amalixshop.utils;

import com.amalixshop.controllers.CartController;
import com.amalixshop.controllers.DashboardController;
import com.amalixshop.controllers.OrderConfirmationController;
import com.amalixshop.controllers.OrdersController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class NavigationUtil {

    public static void navigateToLogin(Stage stage) {
        navigateTo("com/amalixshop/LoginView.fxml", stage, "Login", 450, 400);
    }

    public static void navigateToRegister(Stage stage) {
        navigateTo("com/amalixshop/RegisterView.fxml", stage, "Register", 450, 550);
    }

    public static void navigateToDashboard(Stage stage, String encryptedId) {
        try {
            URL fxmlUrl = NavigationUtil.class.getClassLoader().getResource("com/amalixshop/DashboardView.fxml");

            if (fxmlUrl == null) {
                System.err.println("FXML not found: com/amalixshop/DashboardView.fxml");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Get the controller and set the customer ID
            DashboardController controller = loader.getController();
            controller.setCurrentCustomerId(encryptedId);

            stage.setTitle("AmaliXShop - Home");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            System.err.println("Navigation to dashboard failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void navigateToCart(Stage stage, String encryptedId) {
        System.out.println("Navigating to cart...");

        try {
            URL fxmlUrl = NavigationUtil.class.getClassLoader().getResource("com/amalixshop/Cart.fxml");
            System.out.println("FXML URL: " + fxmlUrl);

            if (fxmlUrl == null) {
                System.err.println("ERROR: Cart.fxml not found!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            CartController controller = loader.getController();
            System.out.println("Controller loaded: " + controller);

            if (controller != null && encryptedId != null) {
                controller.setCurrentCustomerId(encryptedId);
            }

            stage.setTitle("AmaliXShop - Cart");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

            System.out.println("Navigation successful!");

        } catch (Exception e) {
            System.err.println("Navigation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void navigateToAdminDashboard(Stage stage, String encryptedId) {
        navigateTo("com/amalixshop/AdminView.fxml", stage, "Admin Dashboard", 800, 600);
    }

    public static void navigateToProductManagement(Stage stage) {
        navigateTo("com/amalixshop/ProductManagementView.fxml", stage, "Product Management", 900, 650);
    }


    public static void navigateToProfile(Stage stage, String encryptedId) {
        navigateTo("com/amalixshop/profile.fxml", stage, "My Profile", 700, 600);
    }

    // Add this method to NavigationUtil.java
    public static void navigateToOrderConfirmation(Stage stage, String customerId, String orderId) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource("/com/amalixshop/OrderConfirmation.fxml"));
            Parent root = loader.load();

            OrderConfirmationController controller = loader.getController();
            controller.setCurrentCustomerId(customerId);
            controller.setOrderId(orderId);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Order Confirmation - AmaliXShop");
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading order confirmation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void navigateToOrders(Stage stage, String customerId) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource("/com/amalixshop/Orders.fxml"));
            Parent root = loader.load();

            OrdersController controller = loader.getController();
            controller.setCurrentCustomerId(customerId);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("My Orders - AmaliXShop");
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading orders page: " + e.getMessage());
            e.printStackTrace();

            // Fallback
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Could not load orders page. Please try again.");
            alert.showAndWait();
        }
    }

    private static void navigateTo(String fxmlPath, Stage stage, String title,
                                   int width, int height) {
        try {

            URL fxmlUrl = NavigationUtil.class.getClassLoader().getResource(fxmlPath);

            if (fxmlUrl == null) {
                System.err.println("FXML not found: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            stage.setTitle("AmaliXShop - " + title);
            stage.setScene(new Scene(root, width, height));
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            System.err.println("Navigation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}