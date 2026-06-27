package com.example.cupMan;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import java.io.IOException;
import java.sql.*;


public class SignUpController  {

    @FXML
    private TextField usernameTF;

    @FXML
    private PasswordField passwordPF;

    @FXML
    private PasswordField cfpPf;

    @FXML
    private Label errorLabel;


    @FXML
    void confirm(javafx.event.ActionEvent event) {


            String username = usernameTF.getText().trim();
            String password = passwordPF.getText();
            String confirmPassword = cfpPf.getText();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                errorLabel.setText("pls enter all the fields");
                return;
            }

            if (!password.equals(confirmPassword)) {
                errorLabel.setText("Passwords do not match");
                return;
            }

            try(Connection connection= DriverManager.getConnection(Utility.url,Utility.getUser(),Utility.getPassword())) {


                PreparedStatement checkStatement = connection.prepareStatement("select * from Users where name = ?");
                checkStatement.setString(1, username);

                ResultSet resultSet = checkStatement.executeQuery();

                if (resultSet.next()) {
                    errorLabel.setText("Username is already taken");
                    return;
                }

                PreparedStatement insertStatement=connection.prepareStatement("insert into Users (name,password) values(?,?)");
                insertStatement.setString(1, username);
                insertStatement.setString(2, password);
                insertStatement.executeUpdate();

                Utility.changeScene(event, "/com/example/cupMan/LogIn.fxml");

            }catch (SQLException e){
                e.printStackTrace();
                errorLabel.setText("Connection error");
            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    @FXML
    void Back(javafx.event.ActionEvent event) throws IOException {
        Utility.changeScene(event, "/com/example/cupMan/FirstPage.fxml");
    }


}

