package org.example.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import org.example.visualization.Palette;

public class ColorLegend {

    public static VBox build() {
        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: #502A50; -fx-padding: 16; -fx-background-radius: 10; -fx-font-family: 'Baloo 2';");
        box.setPrefWidth(230);

        Label heading = new Label("Color Guide");
        heading.setStyle("-fx-text-fill: #ECD0EC; -fx-font-size: 16px; -fx-font-weight: bold;");

        box.getChildren().addAll(heading,
                swatch(Palette.DEFAULT, "Default"),
                swatch(Palette.COMPARE, "Comparing"),
                swatch(Palette.SWAP, "Swapping"),
                swatch(Palette.FOUND, "Found"),
                swatch(Palette.DONE, "Completed"));
        return box;
    }

    private static HBox swatch(Color color, String label) {
        Rectangle rect = new Rectangle(18, 18, color);
        rect.setArcWidth(5);
        rect.setArcHeight(5);

        Label text = new Label(label);
        text.setStyle("-fx-text-fill: #ECD0EC; -fx-font-size: 13px;");

        HBox row = new HBox(10, rect, text);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }
}