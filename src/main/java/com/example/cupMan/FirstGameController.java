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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class FirstGameController implements Initializable {

    @FXML
    private ImageView cupManIV;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ImageView Boss;
    @FXML
    private Label WinLabel;
    @FXML
    private Label gameOverLabel;
    @FXML
    private Button menuButton;
    @FXML
    private Label countdownLabel;


    private final double Boss_HP = 200.0;
    private double bossCurrentHP = Boss_HP;

    @FXML
    private ProgressBar bossHPBar;

    private final double CupManHP = 100.0;
    private double CupManCurrentHp = CupManHP;

    private boolean gameFinished = true;

    @FXML
    private ProgressBar cupManHPBar;

    private final   double leftLimit = 0;
    private final   double rightLimit = 760;

    private boolean jumping = false;
    private double velocityY = 0;

    private final double gravity = 0.5;
    private final double groundY = 517;

    private final double pixelsPerMove = 5;
    private long lastFrameNano = 0;
    private final int fps = 15;

    private final long frameDurationNano  = 1_000_000_000L / fps;

    private long lastBossShootTimeNano = 0;

    private final long bossShootCooldownNano = 2_000_000_000L;

    private boolean facingRight = true;

    private int currentFrameRightIndex = 0;

    private boolean shooting = false;

    private final double bulletSpeed = 8.0;

    private final double bossBulletSpeed = 6.0;


    private Image right_idle = new Image(getClass().getResource("/IdleCupMan.png").toString());

    private Image[] runRightFrames = {
            new Image(getClass().getResource("/RunningCupMan1.png").toString()),
            new Image(getClass().getResource("/RunningCupMan2.png").toString()),
            new Image(getClass().getResource("/RunningCupMan3.png").toString())
    };

    private int currentFrameLeftIndex = 0;

    private Image left_idle = new Image(getClass().getResource("/IdleCupMan2.png").toString());

    private Image[] runLeftFrames = {
            new Image(getClass().getResource("/RunningBackCupMan1.png").toString()),
            new Image(getClass().getResource("/RunningBackCupMan2.png").toString()),
            new Image(getClass().getResource("/RunningBackCupMan3.png").toString())
    };


    private Image jumpFramesRight = new Image(getClass().getResource("/JumpingCupMan.png").toString());
    private Image jumpFramesLeft = new Image(getClass().getResource("/JumpingCupMan2.png").toString());

    private Image ShootFramesRight=new Image(getClass().getResource("/ShootCupMan.png").toString());
    private Image ShootFramesLeft=new Image(getClass().getResource("/ShootCupMan2.png").toString());

    private Image bossIdle = new Image(getClass().getResource("/Boss2.png").toString());
    private Image bossShoot = new Image(getClass().getResource("/ShootingBoss2.png").toString());

    private Image cupManBulletImg = new Image(getClass().getResource("/cupmanBullet.png").toString());
    private Image bossBulletImg = new Image(getClass().getResource("/Boss2Bullets.png").toString());

    private int bossPhase = 1;

    private Image secondBossIdle = new Image(getClass().getResource("/CupManBoss.png").toString());
    private Image secondBossBullet = new Image(getClass().getResource("/CupManBossAttack.png").toString());

    private Set<ImageView> CupManBullets = new HashSet<>();
    private Set<ImageView> bossBullets = new HashSet<>();

    private Set<KeyCode> keyCodeSet = new HashSet<>();

    public void makeIdle() {
        if (facingRight) {
            if (cupManIV.getImage() != right_idle) {
                cupManIV.setImage(right_idle);
            }
        } else {
            if (cupManIV.getImage() != left_idle) {
                cupManIV.setImage(left_idle);
            }
        }
    }

    public void MoveRight(long currentNanoTime) {
        if (cupManIV.getLayoutX() + pixelsPerMove <= rightLimit) {
            facingRight = true;
            cupManIV.setLayoutX(cupManIV.getLayoutX() + pixelsPerMove);


            if (currentNanoTime - lastFrameNano >= frameDurationNano) {
                lastFrameNano = currentNanoTime;
                currentFrameRightIndex = (currentFrameRightIndex + 1) % runRightFrames.length;

                if (!jumping) {
                    cupManIV.setImage(runRightFrames[currentFrameRightIndex]);
                }
            }
        }
    }

    public void MoveLeft(long currentNanoTime) {
        if (cupManIV.getLayoutX() - pixelsPerMove >= leftLimit) {
            facingRight = false;
            cupManIV.setLayoutX(cupManIV.getLayoutX() - pixelsPerMove);

            if (currentNanoTime - lastFrameNano >= frameDurationNano) {
                lastFrameNano = currentNanoTime;
                currentFrameLeftIndex = (currentFrameLeftIndex + 1) % runLeftFrames.length;

                if (!jumping) {
                    cupManIV.setImage(runLeftFrames[currentFrameLeftIndex]);
                }
            }
        }
    }

    public void Jump(){
        if (!jumping) {
            jumping = true;
            velocityY = -12;

            if (facingRight) {
                cupManIV.setImage(jumpFramesRight);
            } else {
                cupManIV.setImage(jumpFramesLeft);
            }
        }
    }

    public void Shoot() {
        if (shooting) return;
        Music.getInstance().playShoot();
        shooting = true;


        if (facingRight) {
            cupManIV.setImage(ShootFramesRight);
        } else {
            cupManIV.setImage(ShootFramesLeft);
        }


        ImageView bullet = new ImageView(cupManBulletImg);

        bullet.setFitWidth(50);
        bullet.setFitHeight(30);


        bullet.setLayoutY((cupManIV.getLayoutY() + cupManIV.getFitHeight() / 2) - 10);

        if (facingRight) {
            bullet.setLayoutX(cupManIV.getLayoutX() + 50);
            bullet.setUserData("Right");
        } else {
            bullet.setLayoutX(cupManIV.getLayoutX() - 10);
            bullet.setUserData("Left");
            bullet.setScaleX(-1);
        }

        CupManBullets.add(bullet);
        anchorPane.getChildren().add(bullet);

        javafx.animation.PauseTransition pt = new javafx.animation.PauseTransition(javafx.util.Duration.millis(150));
        pt.setOnFinished(e -> shooting = false);
        pt.play();
    }

    public void BossShoot() {

        if (bossPhase == 1) {
            Boss.setImage(bossShoot);

            ImageView bullet = new ImageView(bossBulletImg);
            bullet.setFitWidth(90);
            bullet.setFitHeight(90);
            bullet.setPreserveRatio(true);

            bullet.setLayoutX(Boss.getLayoutX() - 15);
            bullet.setLayoutY(Boss.getLayoutY() + ((Boss.getFitHeight() / 2) + 60));

            bossBullets.add(bullet);
            anchorPane.getChildren().add(bullet);

            javafx.animation.PauseTransition pt = new javafx.animation.PauseTransition(javafx.util.Duration.millis(200));
            pt.setOnFinished(e -> {
                if (bossCurrentHP > 0) {
                    Boss.setImage(bossIdle);
                }
            });
            pt.play();

        } else {

            for (int i = 0; i < 3; i++) {

                double bulletWidth = 90;
                double bulletHeight = 300;

                double maxSpawnX = Boss.getLayoutX() - bulletWidth;
                if (maxSpawnX < leftLimit) maxSpawnX = rightLimit - bulletWidth;

                double randomX = leftLimit + (Math.random() * (maxSpawnX - leftLimit));
                double spawnY = ((groundY + cupManIV.getFitHeight() - bulletHeight) + 20);


                Rectangle warningZone = new Rectangle(bulletWidth, bulletHeight);
                warningZone.setFill(Color.RED);
                warningZone.setOpacity(0.4);
                warningZone.setLayoutX(randomX);
                warningZone.setLayoutY(spawnY);

                anchorPane.getChildren().add(warningZone);


                javafx.animation.PauseTransition triggerBullet = new javafx.animation.PauseTransition(javafx.util.Duration.millis(500));

                final double finalX = randomX;
                final double finalY = spawnY;

                triggerBullet.setOnFinished(e -> {

                    anchorPane.getChildren().remove(warningZone);


                    if (gameFinished) return;


                    ImageView secondBullet = new ImageView(secondBossBullet);
                    secondBullet.setFitWidth(bulletWidth);
                    secondBullet.setFitHeight(bulletHeight);
                    secondBullet.setPreserveRatio(true);
                    secondBullet.setLayoutX(finalX);
                    secondBullet.setLayoutY(finalY);

                    secondBullet.setUserData("Attack");

                    bossBullets.add(secondBullet);
                    anchorPane.getChildren().add(secondBullet);


                    javafx.animation.PauseTransition ptsecondBullet = new javafx.animation.PauseTransition(javafx.util.Duration.millis(1200));
                    ptsecondBullet.setOnFinished(ev -> {
                        anchorPane.getChildren().remove(secondBullet);
                        bossBullets.remove(secondBullet);
                    });
                    ptsecondBullet.play();
                });

                triggerBullet.play();
            }
        }
    }

    private void spawnNextBoss() {
        bossPhase = 2;
        bossCurrentHP = Boss_HP;

        if (bossHPBar != null) {
            bossHPBar.setProgress(1.0);
        }


        bossIdle = secondBossIdle;
        bossShoot = secondBossBullet;


        Boss.setImage(bossIdle);
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

    private void clearBullets(){
        anchorPane.getChildren().removeAll(CupManBullets);
        anchorPane.getChildren().removeAll(bossBullets);

    }


    public void initialize(URL url, ResourceBundle resourceBundle){
        Platform.runLater(() -> {
            Music.getInstance().play2();
        });
            AnimationTimer animationTimer = new AnimationTimer() {
                @Override
                public void handle(long currentNanoTime) {

                    if (gameFinished) {
                        return;
                    }

                    if (!jumping && !shooting) {
                        if (keyCodeSet.contains(KeyCode.D)) {
                            MoveRight(currentNanoTime);
                        } else if (keyCodeSet.contains(KeyCode.A)) {
                            MoveLeft(currentNanoTime);
                        } else {
                            makeIdle();
                        }
                    }

                    if (keyCodeSet.contains(KeyCode.SPACE)) {
                        Jump();
                    }

                    if (keyCodeSet.contains(KeyCode.K)) {
                        Shoot();
                        keyCodeSet.remove(KeyCode.K);
                    }

                    if (jumping) {
                        velocityY += gravity;
                        cupManIV.setLayoutY(cupManIV.getLayoutY() + velocityY);

                        if (cupManIV.getLayoutY() >= groundY) {
                            cupManIV.setLayoutY(groundY);
                            velocityY = 0;
                            jumping = false;

                            if (facingRight) {
                                cupManIV.setImage(right_idle);
                            } else {
                                cupManIV.setImage(left_idle);
                            }
                        }
                    }



                    for (ImageView bullet : CupManBullets) {
                        if (bullet.getUserData().equals("Right")) {
                            bullet.setLayoutX(bullet.getLayoutX() + bulletSpeed);
                        } else {
                            bullet.setLayoutX(bullet.getLayoutX() - bulletSpeed);
                        }
                    }


                    if (currentNanoTime - lastBossShootTimeNano >= bossShootCooldownNano && bossCurrentHP > 0) {
                        BossShoot();
                        lastBossShootTimeNano = currentNanoTime;
                    }


                    for (ImageView bullet : bossBullets) {
                        if (!"Attack".equals(bullet.getUserData())) {
                            bullet.setLayoutX(bullet.getLayoutX() - bossBulletSpeed);
                        }
                    }


                    bossBullets.removeIf(bullet -> {
                        if (gameFinished) {
                            return true;
                        }

                        boolean outOfBounds = bullet.getLayoutX() < -50 || bullet.getLayoutX() > anchorPane.getWidth() + 50;
                        boolean hitPlayer = bullet.getBoundsInParent().intersects(cupManIV.getBoundsInParent());

                        if (hitPlayer) {

                            if ("Attack".equals(bullet.getUserData())) {

                                if ("Damaged".equals(bullet.getProperties().get("state"))) {
                                    return false;
                                }

                                bullet.getProperties().put("state", "Damaged");
                                CupManCurrentHp -= 20.0;
                            } else {

                                CupManCurrentHp -= 20.0;
                            }


                            cupManIV.setOpacity(0.5);
                            javafx.animation.PauseTransition flash = new javafx.animation.PauseTransition(javafx.util.Duration.millis(150));
                            flash.setOnFinished(e -> cupManIV.setOpacity(1.0));
                            flash.play();

                            if (CupManCurrentHp < 0) CupManCurrentHp = 0;

                            if (cupManHPBar != null) {
                                cupManHPBar.setProgress(CupManCurrentHp / CupManHP);
                            }

                            if (CupManCurrentHp <= 0 && !gameFinished) {
                                gameFinished = true;
                                Music.getInstance().playLose();

                                gameOverLabel.setVisible(true);
                                menuButton.setVisible(true);
                                menuButton.toFront();

                                int scoreEarned = 0;
                                if (bossPhase == 1) {
                                    scoreEarned = (int) (Boss_HP - bossCurrentHP);
                                } else if (bossPhase == 2) {
                                    scoreEarned = (int) (Boss_HP + (Boss_HP - bossCurrentHP));
                                }

                                updateHighScore(scoreEarned);


                                javafx.animation.PauseTransition deathDelay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
                                deathDelay.setOnFinished(e -> {
                                    anchorPane.getChildren().remove(cupManIV);
                                    clearBullets();
                                });
                                deathDelay.play();
                            }


                            if (!"Attack".equals(bullet.getUserData())) {
                                anchorPane.getChildren().remove(bullet);
                                return true;
                            }
                            return false;
                        }

                        if (outOfBounds) {
                            anchorPane.getChildren().remove(bullet);
                            return true;
                        }

                        return false;
                    });


                    CupManBullets.removeIf(bullet -> {
                        if (gameFinished) {
                            return true;
                        }

                        boolean outOfBounds = bullet.getLayoutX() < -50 || bullet.getLayoutX() > anchorPane.getWidth() + 50;
                        boolean hitBoss = false;

                        if (Boss != null && Boss.getLayoutX() > 0) {
                            hitBoss = bullet.getBoundsInParent().intersects(Boss.getBoundsInParent());
                        }

                        if (hitBoss) {
                            bossCurrentHP -= 10.0;

                            Boss.setOpacity(0.5);
                            javafx.animation.PauseTransition flash = new javafx.animation.PauseTransition(javafx.util.Duration.millis(150));
                            flash.setOnFinished(e -> Boss.setOpacity(1.0));
                            flash.play();

                            if (bossCurrentHP < 0) bossCurrentHP = 0;

                            if (bossHPBar != null) {
                                bossHPBar.setProgress(bossCurrentHP / Boss_HP);
                            }

                            if (bossCurrentHP <= 0 && Boss.getParent() != null && !gameFinished) {

                                if (bossPhase == 1) {
                                    spawnNextBoss();
                                } else {
                                    gameFinished = true;
                                    Music.getInstance().playWin();
                                    anchorPane.getChildren().remove(Boss);
                                    clearBullets();


                                    WinLabel.setVisible(true);
                                    menuButton.setVisible(true);
                                    menuButton.toFront();

                                    int scoreEarned = 0;
                                    if (bossPhase == 1) {
                                        scoreEarned = (int) (Boss_HP - bossCurrentHP);
                                    } else if (bossPhase == 2) {
                                        scoreEarned = (int) (Boss_HP + (Boss_HP - bossCurrentHP));
                                    }

                                    updateHighScore(scoreEarned);
                                }
                            }

                            anchorPane.getChildren().remove(bullet);
                            return true;
                        }

                        if (outOfBounds) {
                            anchorPane.getChildren().remove(bullet);
                            return true;
                        }

                        return false;
                    });
                }
            };
            animationTimer.start();

            startCountdown();


        Platform.runLater(() -> {
            cupManIV.getScene().setOnKeyPressed(e -> {keyCodeSet.add(e.getCode());});
            cupManIV.getScene().setOnKeyReleased(e -> {keyCodeSet.remove(e.getCode());});
        });

    }



}