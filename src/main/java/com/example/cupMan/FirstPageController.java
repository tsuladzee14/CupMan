package com.example.cupMan;

import javafx.application.Platform;
import javafx.fxml.FXML;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

public class FirstPageController implements Initializable {

    @FXML
    void login(javafx.event.ActionEvent event) throws IOException {
        Utility.changeScene(event, "/com/example/cupMan/LogIn.fxml");
    }

    @FXML
    void signup(javafx.event.ActionEvent event) throws IOException {
        Utility.changeScene(event, "/com/example/cupMan/SignUp.fxml");
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            Music.getInstance().play1();
        });
    }
}
