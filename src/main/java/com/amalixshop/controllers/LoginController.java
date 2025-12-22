package com.amalixshop.controllers;

import com.amalixshop.services.CustomerService;
import com.amalixshop.utils.NavigationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Map;

import static com.amalixshop.utils.AlertUtil.showSuccess;


public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private final CustomerService customerService = new CustomerService();


    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Call service to authenticate
        Map<String,String> authResult = customerService.loginCustomer(email, password);

        if (authResult != null) {

            String encryptedId = authResult.get("encryptedId");
            String role = authResult.get("role");

            Stage stage = (Stage) emailField.getScene().getWindow();
            if ("user".equals(role)) {
                NavigationUtil.navigateToDashboard(stage, encryptedId);
            } else {
                NavigationUtil.navigateToAdminDashboard(stage, encryptedId);
            }

        }
    }


    @FXML
    private void handleCreateAccount() {
        Stage currentStage = (Stage) emailField.getScene().getWindow();
        NavigationUtil.navigateToRegister(currentStage);
    }


}