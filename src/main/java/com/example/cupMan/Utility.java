package com.example.cupMan;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Utility {

    public static String loggedInUser = "";

    public static final String url = "jdbc:mysql://localhost:3306/CupHead";

    public static String getUser() {
        try {
            String[] config = DataBaseConfig.load();
            return config[0];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getPassword() {
        try {
            String[] config = DataBaseConfig.load();
            return config[1];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void changeScene(javafx.event.ActionEvent event, String fxmlName) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Utility.class.getResource(fxmlName));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }
}