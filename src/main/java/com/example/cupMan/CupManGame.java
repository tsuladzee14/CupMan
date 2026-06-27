package com.example.cupMan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CupManGame extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        String[] config = DataBaseConfig.load();

        if (config == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("databaseSetup.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
            return;
        }

        String url = "jdbc:mysql://localhost:3306/sys";
        String user = config[0];
        String password = config[1];

        Connection connection = DriverManager.getConnection(url, user, password);

        Statement statement = connection.createStatement();
        statement.execute("create database if not exists CupHead");
        statement.execute("use CupHead");

        statement.executeUpdate(
                "create table if not exists Users (" +
                        "id int primary key auto_increment, " +
                        "name varchar(50) unique not null, " +
                        "password varchar(50) not null, " +
                        "high_score int default 0)"
        );
        statement.executeUpdate(
                "insert into Users(name, high_score, password) values " +
                        "('User1', 0, 'password1')," +
                        "('User2', 0, 'password2')," +
                        "('User3', 0, 'password3')," +
                        "('User4', 0, 'password4')," +
                        "('User5', 0, 'password5');"
        );

        FXMLLoader loader = new FXMLLoader(getClass().getResource("FirstPage.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}