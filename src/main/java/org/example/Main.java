package org.example;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import org.example.ui.SceneManager;
import org.example.ui.WelcomeScreen;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("AlgoRhythm");

        // Loading custom fonts once at startup — after this, any CSS
        // using -fx-font-family: 'Pacifico' or 'Baloo 2' will work.
        Font.loadFont(getClass().getResourceAsStream("/fonts/Pacifico-Regular.ttf"), 12);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Baloo2-Regular.ttf"), 12);

        primaryStage.setMaximized(true);

        SceneManager sceneManager = new SceneManager(primaryStage);
        new WelcomeScreen(sceneManager).show();

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}