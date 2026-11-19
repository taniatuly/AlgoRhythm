package org.example.ui;

import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import org.example.music.AmbientPlayer;

public class WelcomeScreen {

    private final SceneManager sceneManager;

    public WelcomeScreen(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void show() {
        AmbientPlayer.start();

        Label title = new Label("AlgoRhythm");
        title.setStyle("-fx-font-size: 64px; -fx-text-fill: #ECD0EC; -fx-font-family: 'Pacifico';");

        // Gentle up-down float, like it's breathing.
        TranslateTransition floatAnim = new TranslateTransition(Duration.seconds(2.5), title);
        floatAnim.setByY(10);
        floatAnim.setAutoReverse(true);
        floatAnim.setCycleCount(TranslateTransition.INDEFINITE);
        floatAnim.play();

        Label subtitle = new Label("Watch algorithms come alive — in motion and in sound");
        subtitle.setStyle("-fx-font-size: 17px; -fx-text-fill: #BA71A2; -fx-font-family: 'Pacifico';");

        Button startButton = new Button("Get Started");
        startButton.setStyle("-fx-background-color: #ECD0EC; -fx-text-fill: #461D3A; " +
                "-fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 12 32; -fx-font-size: 16px;");
        startButton.setOnAction(e -> new SelectionScreen(sceneManager).show());

        VBox textBox = new VBox(20, title, subtitle, startButton);
        textBox.setAlignment(Pos.CENTER);

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #461D3A;");
        root.getChildren().addAll(Doodle.backgroundLayer(root), textBox);

        sceneManager.show(root);
    }
}