package com.amalixshop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("Starting AmaliXShop...");

        // Start with login screen by default
        URL fxmlUrl = getClass().getClassLoader().getResource("com/amalixshop/LoginView.fxml");

        if (fxmlUrl == null) {
            System.err.println("ERROR: FXML file not found!");
            return;
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();



        Scene scene = new Scene(root, 450, 400);
        stage.setTitle("AmaliXShop - Login");
        stage.setScene(scene);
        stage.show();

        System.out.println("Application started successfully!");
    }

    static void main(String[] args) {
        launch(args);
    }
}