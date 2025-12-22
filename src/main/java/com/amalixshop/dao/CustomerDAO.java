package com.amalixshop.dao;

import com.amalixshop.database.DatabaseConnection;
import com.amalixshop.models.Customer;
import com.amalixshop.utils.EncryptionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerDAO {

    /**
     * Insert a new customer into the database
     * @param customer The customer object to insert
     * @return The generated encrypted customer_id, or null if failed
     */
    public String insertCustomer(Customer customer) {
        String sql = "INSERT INTO customers (customer_name, email, password_hash, phone, address, role) VALUES (?, ?, ?, ?, ?, ?) RETURNING customer_id";

        try (
                Connection conn = DatabaseConnection.getConnection()
        ) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, customer.getCustomerName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPasswordHash());
            stmt.setString(4, customer.getPhone());
            stmt.setString(5, customer.getAddress());
            stmt.setString(6, "user");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int generatedId = rs.getInt("customer_id");
                    return EncryptionUtil.encrypt(generatedId);
                }
            }

            return null;

        } catch (SQLException e) {
            System.err.println("Error inserting customer: " + e.getMessage());

            if ("23505".equals(e.getSQLState())) {
                System.err.println("Customer already exists (duplicate email)");
            }

            return null;
        }
    }

    /**
     * Authenticate customer by email and password hash
     */
    public Map<String, String> authenticateCustomer(String email, String passwordHash) {
        String sql = "SELECT customer_id, role FROM customers WHERE email = ? AND password_hash = ?";

        try (
                Connection conn = DatabaseConnection.getConnection()
        ) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, passwordHash);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int customerId = rs.getInt("customer_id");
                    String role = rs.getString("role");

                    Map<String, String> result = new HashMap<>();
                    result.put("encryptedId", EncryptionUtil.encrypt(customerId));
                    result.put("role", role);

                    return result;
                }
            }

            return null; // Authentication failed

        } catch (SQLException e) {
            System.err.println("Error authenticating customer: " + e.getMessage());
            return null;
        }
    }


}
