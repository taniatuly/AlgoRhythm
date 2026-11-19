package org.example.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.example.events.EventListener;
import org.example.model.AlgorithmEvent;
import org.example.model.EventType;

// Tracks live stats as events come in — comparisons, swaps, total
// steps, and how long the run took from Start to Complete.
public class MetricsPanel implements EventListener {

    private final Label comparisonsValue = new Label("0");
    private final Label swapsValue = new Label("0");
    private final Label stepsValue = new Label("0");
    private final Label timeValue = new Label("0 ms");

    private int comparisons = 0;
    private int swaps = 0;
    private int steps = 0;
    private long startTime = 0;

    public VBox getView() {
        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: #502A50; -fx-padding: 16; -fx-background-radius: 10; -fx-font-family: 'Baloo 2';");
        box.setPrefWidth(230);

        Label heading = new Label("Metrics");
        heading.setStyle("-fx-text-fill: #ECD0EC; -fx-font-size: 16px; -fx-font-weight: bold;");

        box.getChildren().addAll(heading,
                row("Comparisons", comparisonsValue),
                row("Swaps", swapsValue),
                row("Steps", stepsValue),
                row("Time", timeValue));
        return box;
    }

    private HBox row(String labelText, Label valueLabel) {
        Label label = new Label(labelText + ":");
        label.setStyle("-fx-text-fill: #D9A8CE; -fx-font-size: 13px;");
        valueLabel.setStyle("-fx-text-fill: #ECD0EC; -fx-font-size: 13px; -fx-font-weight: bold;");

        HBox h = new HBox(8, label, valueLabel);
        h.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(label, Priority.ALWAYS);
        return h;
    }

    public void reset() {
        comparisons = 0;
        swaps = 0;
        steps = 0;
        startTime = System.currentTimeMillis();
        refresh();
    }

    @Override
    public void onEvent(AlgorithmEvent event) {
        steps++;
        if (event.getType() == EventType.COMPARE) comparisons++;
        if (event.getType() == EventType.SWAP) swaps++;
        if (event.getType() == EventType.COMPLETE) {
            long elapsed = System.currentTimeMillis() - startTime;
            timeValue.setText(elapsed + " ms");
        }
        refresh();
    }

    private void refresh() {
        comparisonsValue.setText(String.valueOf(comparisons));
        swapsValue.setText(String.valueOf(swaps));
        stepsValue.setText(String.valueOf(steps));
    }
}