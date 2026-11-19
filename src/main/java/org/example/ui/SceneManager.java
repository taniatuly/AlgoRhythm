package org.example.ui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class SceneManager {

    private final Stage stage;
    private Scene scene;

    public SceneManager(Stage stage) {
        this.stage = stage;
    }

    public void show(Parent root) {
        if (root instanceof Region region) {
            region.prefWidthProperty().bind(stage.widthProperty());
            region.prefHeightProperty().bind(stage.heightProperty());
        }

        if (scene == null) {
            scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
            stage.setScene(scene);
        } else {
            scene.setRoot(root);
        }
    }
}