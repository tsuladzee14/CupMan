package com.example.cupMan;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class DataBaseSetUpController {

    @FXML
    private TextField userField;

    @FXML
    private PasswordField passwordField;

    @FXML
    public void saveConfig(ActionEvent event) throws Exception {

        DataBaseConfig.save(
                userField.getText(),
                passwordField.getText()
        );

        Utility.changeScene(event, "/com/example/cupMan/FirstPage.fxml");
    }
}