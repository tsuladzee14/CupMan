package com.example.cupMan;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.*;


public class LogInController {

    @FXML
    private TextField UserTF;

    @FXML
     PasswordField passwordPF;

    @FXML
    private Label errorlabel;



    @FXML
    void confirm(javafx.event.ActionEvent event) {
        try {String username=UserTF.getText();
            String password=passwordPF.getText();

            if (username.isEmpty() || password.isEmpty()) {
                errorlabel.setText("pls enter all the fields");
                return;
            }

            Connection connection=DriverManager.getConnection(Utility.url,Utility.getUser(),Utility.getPassword());
            PreparedStatement preparedStatement=connection.prepareStatement("select * from Users where name=? and password=?");

            preparedStatement.setString(1,username);
            preparedStatement.setString(2,password);

            ResultSet resultSet=preparedStatement.executeQuery();

            if (resultSet.next()) {
                Utility.loggedInUser = UserTF.getText().trim();

                Utility.changeScene(event, "/com/example/cupMan/MainMenu.fxml");
            }
            else {
                errorlabel.setText("Username or password is incorrect");
            }

        }catch (SQLException e){
            e.printStackTrace();
            errorlabel.setText("Connection error");
        }catch (IOException e){
            e.printStackTrace();
            errorlabel.setText("MainMenu file not found");
        }
    }

    @FXML
    void Back(javafx.event.ActionEvent event) throws IOException {
        Utility.changeScene(event, "/com/example/cupMan/FirstPage.fxml");
    }



}

