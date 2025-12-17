package com.amalixshop.controllers;

import com.amalixshop.utils.NavigationUtil;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DashboardController {

    @FXML
    private Button logoutButton;

    @FXML
    private void handleLogout() {
        if (logoutButton != null) {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            NavigationUtil.navigateToLogin(stage);
            return;
        }


    }
}