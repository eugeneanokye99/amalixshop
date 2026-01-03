package com.amalixshop.utils;

public class SessionManager {
    private static SessionManager instance;
    private int currentUserId;
    private String currentUsername;
    private String userType;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(int userId, String username, String userType) {
        this.currentUserId = userId;
        this.currentUsername = username;
        this.userType = userType;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public String getUserType() {
        return userType;
    }

    public void clearSession() {
        currentUserId = 0;
        currentUsername = null;
        userType = null;
    }

    public boolean isLoggedIn() {
        return currentUserId > 0;
    }
}