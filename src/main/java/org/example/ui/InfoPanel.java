package org.example.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class InfoPanel {
    public static VBox build(String algorithmName) {
        Label heading = new Label("📘 " + algorithmName);
        heading.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 18px; -fx-font-weight: bold; -fx-font-family: 'Baloo 2';");

        Label note = new Label(AlgorithmNotes.get(algorithmName));
        note.setWrapText(true);
        note.setStyle("-fx-text-fill: #F0D4E8; -fx-font-size: 14px; -fx-font-family: 'Baloo 2';");

        VBox box = new VBox(10, heading, note);
        box.setStyle("-fx-background-color: #502A50; -fx-padding: 18; -fx-background-radius: 12;");
        return box;
    }
}