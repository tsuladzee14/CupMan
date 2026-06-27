package com.example.cupMan;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Music {

    private static Music instance;
    private MediaPlayer mediaPlayer;

    private Music() {}

    public static Music getInstance() {
        if (instance == null) {
            instance = new Music();
        }
        return instance;
    }

    public void play1() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            return;
        }

        Media media = new Media(
                getClass().getResource("/Music/CupManMusic1.mp3").toExternalForm()
        );

        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setVolume(0.3);
        mediaPlayer.play();
    }

    public void play2() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            return;
        }
        Media media = new Media(
                getClass().getResource("/Music/CupManBossFight1.mp3").toExternalForm()
        );

        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setVolume(0.3);
        mediaPlayer.play();
    }

    public void play3() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            return;
        }
        Media media = new Media(
                getClass().getResource("/Music/CupManBossFight2.mp3").toExternalForm()
        );

        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.setVolume(0.3);
        mediaPlayer.play();
    }

    public void playWin() {
        stop();

        Media media = new Media(
                getClass().getResource("/Music/Win.mp3").toExternalForm()
        );

        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0.4);
        mediaPlayer.play();
    }

    public void playLose() {
        stop();

        Media media = new Media(
                getClass().getResource("/Music/Lose.mp3").toExternalForm()
        );

        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0.4);
        mediaPlayer.play();
    }

    public void playShoot() {

        Media media = new Media(
                getClass().getResource("/Music/Shoot.mp3").toExternalForm()
        );

        MediaPlayer shootPlayer = new MediaPlayer(media);
        shootPlayer.setVolume(0.2);
        shootPlayer.play();
    }




    public void stop() {

        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}
