package com.amalixshop.utils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Pure validation utility - follows SRP
 * Only validates, doesn't show UI or business logic
 */
public class ValidationUtil {

    // Pre-compiled patterns for better performance
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[+]?[0-9]{10,15}$");

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[A-Za-z\\s]{2,50}$");

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    // ==================== VALIDATION METHODS ====================

    /**
     * Validate email format only
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Get email validation message
     */
    public static String getEmailMessage(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email cannot be empty";
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return "Invalid email format (example: user@example.com)";
        }
        return null; // No error
    }


    /**
     * Get phone validation message
     */
    public static String getPhoneMessage(String phone) {
        if (phone == null || phone.trim().isEmpty()) return null; // Optional
        String cleanPhone = phone.replaceAll("[\\s\\-\\(\\)]", "");
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            return "Invalid phone (10-15 digits, can start with +)";
        }
        return null;
    }

    /**
     * Validate name only
     */
    public static boolean isValidName(String name) {
        if (name == null) return false;
        String trimmed = name.trim();
        return trimmed.length() > 2 && NAME_PATTERN.matcher(trimmed).matches();
    }

    /**
     * Get name validation message
     */
    public static String getNameMessage(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Name cannot be empty";
        }
        String trimmed = name.trim();
        if (trimmed.length() < 2) {
            return "Name must be at least 2 characters";
        }
        if (!NAME_PATTERN.matcher(trimmed).matches()) {
            return "Name can only contain letters and spaces (2-50 chars)";
        }
        return null;
    }

    /**
     * Validate password strength only
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) return false;
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*[0-9].*") &&
                password.matches(".*[@#$%^&+=].*");
    }

    /**
     * Get password validation message
     */
    public static String getPasswordMessage(String password) {
        if (password == null || password.isEmpty()) {
            return "Password cannot be empty";
        }
        if (password.length() < 8) {
            return "Password must be at least 8 characters";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter";
        }
        if (!password.matches(".*[0-9].*")) {
            return "Password must contain at least one number";
        }
        if (!password.matches(".*[@#$%^&+=].*")) {
            return "Password must contain at least one special character (@#$%^&+=)";
        }
        return null;
    }


    /**
     * Get address validation message
     */
    public static String getAddressMessage(String address) {
        if (address == null || address.trim().isEmpty()) return null;
        String trimmed = address.trim();
        if (trimmed.length() < 5) {
            return "Address is too short (minimum 5 characters)";
        }
        if (trimmed.length() > 255) {
            return "Address is too long (maximum 255 characters)";
        }
        return null;
    }

    // ==================== BATCH VALIDATION ====================

    /**
     * Validate all customer fields at once (returns null if all valid)
     */
    public static String validateCustomerFields(String name, String email, String password,
                                                String phone, String address) {
        String error = getNameMessage(name);
        if (error != null) return "Name: " + error;

        error = getEmailMessage(email);
        if (error != null) return "Email: " + error;

        error = getPasswordMessage(password);
        if (error != null) return "Password: " + error;

        error = getPhoneMessage(phone);
        if (error != null) return "Phone: " + error;

        error = getAddressMessage(address);
        if (error != null) return "Address: " + error;

        return null; // All valid
    }


}