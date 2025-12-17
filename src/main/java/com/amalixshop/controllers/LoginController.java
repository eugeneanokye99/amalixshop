package com.amalixshop.controllers;

import com.amalixshop.services.CustomerService;
import com.amalixshop.utils.NavigationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

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
        String encryptedId = customerService.loginCustomer(email, password);

        if (encryptedId != null) {

            Stage stage = (Stage) emailField.getScene().getWindow();
            NavigationUtil.navigateToDashboard(stage, encryptedId);
        }
    }


    @FXML
    private void handleCreateAccount() {
        Stage currentStage = (Stage) emailField.getScene().getWindow();
        NavigationUtil.navigateToRegister(currentStage);
    }


}