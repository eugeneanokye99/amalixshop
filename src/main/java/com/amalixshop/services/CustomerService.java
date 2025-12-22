package com.amalixshop.services;

import com.amalixshop.dao.CustomerDAO;
import com.amalixshop.models.Customer;
import com.amalixshop.utils.ValidationUtil;
import com.amalixshop.utils.AlertUtil;
import java.security.MessageDigest;
import java.util.Map;

public class CustomerService {
    private final CustomerDAO customerDAO = new CustomerDAO();

    /**
     * Register customer with proper separation of concerns
     */
    public String registerCustomer(String name, String email, String password,
                                   String phone, String address) {

        //Validation
        String validationError = ValidationUtil.validateCustomerFields(name, email, password, phone, address);
        if (validationError != null) {
            AlertUtil.showValidationError(validationError);
            return null;
        }


        String hashedPassword = hashPassword(password);

        Customer customer = new Customer(name, email, phone, address, hashedPassword);



        String encryptedId = customerDAO.insertCustomer(customer);

        if (encryptedId != null) {
            AlertUtil.showSuccess("Registration Successful",
                    "Customer registered successfully!");
            return encryptedId;
        } else {
            AlertUtil.showError("Registration Failed",
                    "Could not register. Email may already exist.");
            return null;
        }
    }

    /**
     * Customer login
     */
    public Map<String, String> loginCustomer(String email, String password) {
        // Validate
        if (!ValidationUtil.isValidEmail(email)) {
            AlertUtil.showValidationError("Please enter a valid email");
            return null;
        }
        if (password == null || password.isEmpty()) {
            AlertUtil.showValidationError("Password is required");
            return null;
        }

        // Business logic
        String hashedPassword = hashPassword(password);

        // Data access
        Map<String, String> authResult = customerDAO.authenticateCustomer(email, hashedPassword);

        // Result
        if (authResult != null) {
            AlertUtil.showSuccess("Login Successful", "Welcome back!");
            return authResult;
        } else {
            AlertUtil.showError("Login Failed", "Invalid email or password");
            return null;
        }
    }


    /**
     * Hash password - pure function
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}