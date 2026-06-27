package com.example.cupMan;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class SecondGameController implements Initializable {

    @FXML
    private ImageView CupManIV;
    @FXML
    private ImageView MoonBossIV;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label WinLabel;
    @FXML
    private Label gameOverLabel;
    @FXML
    private Button menuButton;
    @FXML
    private ProgressBar bossHPBar;
    @FXML
    private Label countdownLabel;
    @FXML
    private ProgressBar cupManHPBar;

    private final double leftLimit = 0;
    private final double rightLimit = 760;
    private final double UpLimit = 0;
    private final double DownLimit = 695;



    private final double pixelsPerMove = 5;
    private long lastFrameNano = 0;
    private final int fps = 15;

    private final long frameDurationNano = 1_000_000_000L / fps;

    private int currentFrameIndex = 0;

    private long lastBossShootTimeNano = 0;

    private final long bossShootCooldownNano = 1_000_000_000L;

    private boolean shooting = false;

    private final double bulletSpeed = 8.0;
    private final double bossBulletSpeed = 25.0;

    private final double Boss_HP = 300.0;
    private double bossCurrentHP = Boss_HP;

    private final double CupManHP = 100.0;
    private double CupManCurrentHp = CupManHP;

    private boolean gameFinished = true;

    private Set<KeyCode> keyCodeSet = new HashSet<>();

    private Set<ImageView> CupManBullets = new HashSet<>();
    private Set<ImageView> bossBullets = new HashSet<>();
    private Set<ImageView> flames = new HashSet<>();

    private Image[] flyingCupMan={
            new Image(getClass().getResource("/flyingCupMan.png").toString()),
            new Image(getClass().getResource("/flyingCupMan2.png").toString()),
            new Image(getClass().getResource("/flyingCupMan3.png").toString())
    };
    private Image cupManBulletImg = new Image(getClass().getResource("/cupmanBullet.png").toString());

    private Image BossBulletImg = new Image(getClass().getResource("/MoonBossShooting.png").toString());
    private Image BossBulletImg2 = new Image(getClass().getResource("/BossOrbs.png").toString());

    private Image BossPhase2=new Image(getClass().getResource("/MoonBossPhase2.png").toString());

    public void MoveRight(long currentNanoTime) {
        if (CupManIV.getLayoutX() + pixelsPerMove <= rightLimit) {
            CupManIV.setLayoutX(CupManIV.getLayoutX() + pixelsPerMove);

            if (currentNanoTime - lastFrameNano >= frameDurationNano) {
                lastFrameNano = currentNanoTime;
                currentFrameIndex = (currentFrameIndex + 1) % flyingCupMan.length;
                CupManIV.setImage(flyingCupMan[currentFrameIndex]);
            }
        }
    }

    public void MoveLeft(long currentNanoTime) {
        if (CupManIV.getLayoutX() - pixelsPerMove >= leftLimit) {
            CupManIV.setLayoutX(CupManIV.getLayoutX() - pixelsPerMove);

            if (currentNanoTime - lastFrameNano >= frameDurationNano) {
                lastFrameNano = currentNanoTime;
                currentFrameIndex = (currentFrameIndex + 1) % flyingCupMan.length;
                CupManIV.setImage(flyingCupMan[currentFrameIndex]);
            }
        }
    }

    public void MoveUp(long currentNanoTime) {
        if (CupManIV.getLayoutY() - pixelsPerMove >= UpLimit) {
            CupManIV.setLayoutY(CupManIV.getLayoutY() - pixelsPerMove);

            if (currentNanoTime - lastFrameNano >= frameDurationNano) {
                lastFrameNano = currentNanoTime;
                currentFrameIndex = (currentFrameIndex + 1) % flyingCupMan.length;
                CupManIV.setImage(flyingCupMan[currentFrameIndex]);
            }
        }
    }

    public void MoveDown(long currentNanoTime) {
        if (CupManIV.getLayoutY() + pixelsPerMove <= DownLimit) {
            CupManIV.setLayoutY(CupManIV.getLayoutY() + pixelsPerMove);

            if (currentNanoTime - lastFrameNano >= frameDurationNano) {
                lastFrameNano = currentNanoTime;
                currentFrameIndex = (currentFrameIndex + 1) % flyingCupMan.length;
                CupManIV.setImage(flyingCupMan[currentFrameIndex]);
            }
        }
    }

    public void Shoot() {
        if (shooting) return;
        Music.getInstance().playShoot();
        shooting = true;
        ImageView bullet = new ImageView(cupManBulletImg);

        bullet.setFitWidth(50);
        bullet.setFitHeight(30);

        bullet.setLayoutY((CupManIV.getLayoutY() + CupManIV.getFitHeight() / 2) + 16 );
        bullet.setLayoutX((CupManIV.getLayoutX() + CupManIV.getFitWidth() / 2) - 20);

        CupManBullets.add(bullet);
        anchorPane.getChildren().add(bullet);

        javafx.animation.PauseTransition pt = new javafx.animation.PauseTransition(javafx.util.Duration.millis(150));
        pt.setOnFinished(e -> shooting = false);
        pt.play();
    }

    public void bossShoot() {

        ImageView bullet = new ImageView(BossBulletImg);

        bullet.setFitWidth(110);
        bullet.setFitHeight(110);

        double startX = MoonBossIV.getLayoutX() + MoonBossIV.getFitWidth();
        double startY = MoonBossIV.getLayoutY() - 90;

        bullet.setLayoutX(startX);
        bullet.setLayoutY(startY);

        double targetX = CupManIV.getLayoutX();
        double targetY = CupManIV.getLayoutY();

        double dx = targetX - startX;
        double dy = targetY - startY;

        double length = Math.sqrt(dx * dx + dy * dy);

        dx = (dx / length) * bossBulletSpeed;
        dy = (dy / length) * bossBulletSpeed;

        bullet.setUserData(new double[]{dx, dy});

        bossBullets.add(bullet);
        anchorPane.getChildren().add(bullet);
    }

    public void bossOrbSpread(ImageView player) {
        double startX = MoonBossIV.getLayoutX();
        double startY = MoonBossIV.getLayoutY() + MoonBossIV.getFitHeight() / 2;

        double playerX = player.getLayoutX() + player.getFitWidth() / 2;
        double playerY = player.getLayoutY() + player.getFitHeight() / 2;


        double baseDirX = playerX - startX;
        double baseDirY = playerY - startY;

        double baseLength = Math.sqrt(baseDirX * baseDirX + baseDirY * baseDirY);

        if (baseLength == 0) baseLength = 1;


        double normX = baseDirX / baseLength;
        double normY = baseDirY / baseLength;


        double[][] spreads = {
                {-0.8, 0.8},
                {-0.4, 0.4},
                {0, 0},
                {0.4, -0.4},
                {0.8, -0.8}
        };

        for (double[] spread : spreads) {
            ImageView bullet = new ImageView(BossBulletImg2);
            bullet.setFitWidth(50);
            bullet.setFitHeight(50);
            bullet.setLayoutX(startX);
            bullet.setLayoutY(startY);

            double dirX = normX + spread[0] * normY;
            double dirY = normY + spread[1] * normX;

            double length = Math.sqrt(dirX * dirX + dirY * dirY);

            double dx = (dirX / length) * bossBulletSpeed;
            double dy = (dirY / length) * bossBulletSpeed;

            bullet.setUserData(new double[]{dx, dy});

            bossBullets.add(bullet);
            anchorPane.getChildren().add(bullet);
        }
    }

    private void startCountdown() {
        if (countdownLabel == null) return;

        countdownLabel.setVisible(true);


        final int[] timeLeft = {5};
        countdownLabel.setText(String.valueOf(timeLeft[0]));

        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), e -> {
                    timeLeft[0]--;

                    if (timeLeft[0] > 0) {
                        countdownLabel.setText(String.valueOf(timeLeft[0]));
                    } else if (timeLeft[0] == 0) {
                        countdownLabel.setText("START!");
                    } else {
                        countdownLabel.setVisible(false);
                        gameFinished = false;
                    }
                })
        );

        timeline.setCycleCount(6);
        timeline.play();
    }


    public void BackToMenu(javafx.event.ActionEvent event) throws IOException {
        Music.getInstance().stop();
        Utility.changeScene(event, "/com/example/cupMan/MainMenu.fxml");

    }


    private void updateHighScore(int scoreChange) {
        String currentUser = Utility.loggedInUser;

        try (Connection conn = DriverManager.getConnection(Utility.url, Utility.getUser(), Utility.getPassword());
             PreparedStatement ps = conn.prepareStatement("update Users set high_score = IFNULL(high_score, 0) + ? where name = ?")) {


            ps.setInt(1, scoreChange);
            ps.setString(2, currentUser);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void clearBullets() {
        anchorPane.getChildren().removeAll(CupManBullets);
        anchorPane.getChildren().removeAll(bossBullets);
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            Music.getInstance().play3();
        });
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                if (gameFinished) {
                    return;
                }

                    if (keyCodeSet.contains(KeyCode.D)) {
                        MoveRight(currentNanoTime);
                    } else if (keyCodeSet.contains(KeyCode.A)) {
                        MoveLeft(currentNanoTime);
                    }else if (keyCodeSet.contains(KeyCode.W)) {
                        MoveUp(currentNanoTime);
                    }
                    else if (keyCodeSet.contains(KeyCode.S)) {
                        MoveDown(currentNanoTime);
                    }

                    if (keyCodeSet.contains(KeyCode.K)) {
                    Shoot();
                    keyCodeSet.remove(KeyCode.K);
                }

                for (ImageView bullet : CupManBullets) {
                        bullet.setLayoutX(bullet.getLayoutX() + bulletSpeed);

                }

                CupManBullets.removeIf(bullet -> {
                    if (gameFinished) {
                        return true;}

                    boolean outOfBounds = bullet.getLayoutX() > anchorPane.getWidth() + 50;
                    boolean hitBoss = bullet.getBoundsInParent().intersects(MoonBossIV.getBoundsInParent());

                    if (hitBoss) {
                        bossCurrentHP -= 5.0;

                        MoonBossIV.setOpacity(0.5);
                        javafx.animation.PauseTransition flash = new javafx.animation.PauseTransition(javafx.util.Duration.millis(150));
                        flash.setOnFinished(e -> MoonBossIV.setOpacity(1.0));
                        flash.play();

                        if (bossCurrentHP < 0) bossCurrentHP = 0;
                        if (bossHPBar != null) bossHPBar.setProgress(bossCurrentHP / Boss_HP);


                        if (bossCurrentHP <= 0) {
                            gameFinished = true;
                            Music.getInstance().playWin();
                            anchorPane.getChildren().remove(MoonBossIV);

                            clearBullets();

                            WinLabel.setVisible(true);
                            menuButton.setVisible(true);
                            menuButton.toFront();
                            updateHighScore((int) Boss_HP);
                        }

                        Platform.runLater(() -> anchorPane.getChildren().remove(bullet));
                        return true;
                    }

                    if (outOfBounds) {
                        Platform.runLater(() -> anchorPane.getChildren().remove(bullet));
                        return true;
                    }
                    return false;
                });

                if (currentNanoTime - lastBossShootTimeNano >= bossShootCooldownNano) {

                    if (bossCurrentHP > 150) {
                        bossShoot();
                    }
                    else {
                        MoonBossIV.setImage(BossPhase2);
                        bossOrbSpread(CupManIV);
                    }
                    lastBossShootTimeNano = currentNanoTime;
                }

                for (ImageView bullet : bossBullets) {

                    double[] velocity = (double[]) bullet.getUserData();

                    bullet.setLayoutX(bullet.getLayoutX() + velocity[0]);
                    bullet.setLayoutY(bullet.getLayoutY() + velocity[1]);
                }

                bossBullets.removeIf(bullet -> {

                    boolean outOfBounds = bullet.getLayoutX() < -50 || bullet.getLayoutX() > anchorPane.getWidth() + 50 ||
                            bullet.getLayoutY() < -50 || bullet.getLayoutY() > anchorPane.getHeight() + 50;

                    boolean hitCupMan = bullet.getBoundsInParent().intersects(CupManIV.getBoundsInParent());

                    if (hitCupMan) {

                        CupManCurrentHp -= 20;

                        if (CupManCurrentHp < 0)
                            CupManCurrentHp = 0;

                        cupManHPBar.setProgress(CupManCurrentHp / CupManHP);

                        CupManIV.setOpacity(0.5);

                        javafx.animation.PauseTransition flash = new javafx.animation.PauseTransition(javafx.util.Duration.millis(150));
                        flash.setOnFinished(e -> CupManIV.setOpacity(1.0));
                        flash.play();

                        if (CupManCurrentHp <= 0) {
                            gameFinished = true;
                            Music.getInstance().playLose();
                            anchorPane.getChildren().remove(CupManIV);

                            clearBullets();

                            gameOverLabel.setVisible(true);
                            menuButton.setVisible(true);
                            menuButton.toFront();

                            int scoreEarned = (int) (Boss_HP - bossCurrentHP);
                            updateHighScore(scoreEarned);
                        }

                        Platform.runLater(() -> anchorPane.getChildren().remove(bullet));

                        return true;
                    }

                    if (outOfBounds) {
                        Platform.runLater(() -> anchorPane.getChildren().remove(bullet));

                        return true;
                    }

                    return false;
                });


            }
        };
        animationTimer.start();

        startCountdown();

        Platform.runLater(() -> {
            CupManIV.getScene().setOnKeyPressed(e -> {keyCodeSet.add(e.getCode());});
            CupManIV.getScene().setOnKeyReleased(e -> {keyCodeSet.remove(e.getCode());});
        });
    }

}
