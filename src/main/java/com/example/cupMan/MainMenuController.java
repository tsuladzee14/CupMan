package com.example.cupMan;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import javafx.scene.control.Label;

public class MainMenuController implements Initializable{

    @FXML
    public void startFirstGame(javafx.event.ActionEvent event) throws IOException {
        Music.getInstance().stop();
        Utility.changeScene(event, "/com/example/cupMan/FirstGame.fxml");
    }

    @FXML
    public void startSecondGame(javafx.event.ActionEvent event) throws IOException {
        Music.getInstance().stop();
        Utility.changeScene(event, "/com/example/cupMan/SecondGame.fxml");
    }

    @FXML
    private VBox infoVBox;

    @FXML
    private Label titleLabel;

    @FXML
    private Label infoLabel;

    @FXML
    private Label errorLabel;


    public void showHighScore(){

        try{
            Connection connection= DriverManager.getConnection(Utility.url,Utility.getUser(),Utility.getPassword());
            PreparedStatement preparedStatement= connection.prepareStatement("select high_score from Users where name=?");

            preparedStatement.setString(1,Utility.loggedInUser);
            ResultSet resultSet= preparedStatement.executeQuery();

            if(resultSet.next()){
                int myHighScore=resultSet.getInt("high_score");

                infoLabel.setText("");
                errorLabel.setText("");

                infoVBox.setVisible(true);
                titleLabel.setText(" YOUR PERSONAL HIGH SCORE ");
                infoLabel.setText("Player: " + Utility.loggedInUser + "\nPersonal Score: " + myHighScore + " pts");
            } else {
                infoVBox.setVisible(false);
                errorLabel.setText("profile not found");
            }

        }catch (SQLException e){
            e.printStackTrace();
            infoVBox.setVisible(false);
            errorLabel.setText("error while connecting to database");
        }
    }

    public void ShowLeaderboard() throws  SQLException {

        Connection connection = DriverManager.getConnection(Utility.url, Utility.getUser(), Utility.getPassword());
        PreparedStatement ps = connection.prepareStatement("select name, high_score from Users order by high_score desc limit 5");

        ResultSet rs = ps.executeQuery();

        StringBuilder text = new StringBuilder();
        int rank = 1;

        while (rs.next()) {
            String name = rs.getString("name");
            int score = rs.getInt("high_score");

            text.append(rank)
                    .append(". ")
                    .append(name)
                    .append(" - ")
                    .append(score)
                    .append(" pts\n");

            rank++;
        }

        titleLabel.setText(" TOP 5 LEADERBOARD ");
        errorLabel.setText("");
        infoVBox.setVisible(true);

        infoLabel.setText(text.toString());
    }

    public void ShowRules() {
        infoLabel.setText("");
        errorLabel.setText("");
        infoVBox.setVisible(true);

        titleLabel.setText(" GAME RULES ");
        infoLabel.setText("1.In first round use AD to move, SPACE to jump, K to shoot\n" +
                "In second round use WASD to move and K to shoot\n" +
                "2. Defeat the bosses and avoid getting hit\n"+
                "3.Dont spam shooting\n" +
                "4.Dont run while jumping\n" +
                "5.If you win you get 200pts , if you lose you lose 100pts\n");
    }

    public void Exit(javafx.event.ActionEvent event) throws IOException {
        Utility.changeScene(event, "/com/example/cupMan/FirstPage.fxml");
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Music.getInstance().play1();
    }
}
