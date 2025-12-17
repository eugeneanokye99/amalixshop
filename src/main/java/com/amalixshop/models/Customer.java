package com.amalixshop.models;

import java.sql.Timestamp;

public class Customer {
    private String customerName;
    private String email;
    private String passwordHash;
    private String phone;
    private String address;


    public Customer(String customerName, String email, String phone, String address,  String passwordHash) {
        this.customerName = customerName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.passwordHash = passwordHash;
    }

    // Getters

    public String getCustomerName() { return customerName; }

    public String getEmail() { return email; }

    public String getPasswordHash() { return passwordHash; }

    public String getPhone() { return phone; }

    public String getAddress() { return address; }



}