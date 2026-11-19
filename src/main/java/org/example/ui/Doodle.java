package org.example.ui;

import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public class Doodle {

    public static ImageView backgroundLayer(Region parent) {
        ImageView view = new ImageView();
        try {
            Image image = new Image(Doodle.class.getResourceAsStream("/images/doodle_hearts.jpg"));
            view.setImage(image);
        } catch (Exception e) {
            System.out.println("Doodle image not found at /images/doodle_hearts.jpg");
        }
        view.setPreserveRatio(false);
        view.setOpacity(0.18);
        view.setMouseTransparent(true);

        // Oversized by 120px and centered (StackPane centers it
        // automatically), so the slow drift below never exposes a
        // bare edge — there's always buffer to slide within.
        view.fitWidthProperty().bind(parent.widthProperty().add(120));
        view.fitHeightProperty().bind(parent.heightProperty().add(120));

        TranslateTransition drift = new TranslateTransition(Duration.seconds(22), view);
        drift.setByX(30);
        drift.setAutoReverse(true);
        drift.setCycleCount(TranslateTransition.INDEFINITE);
        drift.play();

        return view;
    }
}