package com.amalixshop.controllers;

import com.amalixshop.services.CustomerService;
import com.amalixshop.utils.NavigationUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;

import static com.amalixshop.utils.AlertUtil.showSuccess;

public class RegisterController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField phoneField;
    @FXML private TextArea addressField;

    private final CustomerService customerService = new CustomerService();




    @FXML
    private void handleRegister() {
        // 1. Get values from UI
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();

        // 2. Call service (service handles validation and DB)
        String encryptedId = customerService.registerCustomer(name, email, password, phone, address);

        // 3. Handle result
        if (encryptedId != null) {
            clearFields();


            // Navigate to login
            Stage stage = (Stage) nameField.getScene().getWindow();
            NavigationUtil.navigateToLogin(stage);
        }
    }


    @FXML
    private void handleClear() {
        clearFields();
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        NavigationUtil.navigateToLogin(stage);
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        phoneField.clear();
        addressField.clear();
    }
}