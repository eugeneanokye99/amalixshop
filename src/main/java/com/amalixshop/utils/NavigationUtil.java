package com.amalixshop.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class NavigationUtil {

    public static void navigateToLogin(Stage stage) {
        navigateTo("com/amalixshop/LoginView.fxml", stage, "Login", 450, 400);
    }

    public static void navigateToRegister(Stage stage) {
        navigateTo("com/amalixshop/RegisterView.fxml", stage, "Register", 450, 550);
    }

    public static void navigateToDashboard(Stage stage, String encryptedId) {
        navigateTo("com/amalixshop/DashboardView.fxml", stage, "Dashboard", 800, 600);
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
            stage.show();

        } catch (Exception e) {
            System.err.println("Navigation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}