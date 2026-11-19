package org.example.visualization;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.effect.DropShadow;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import org.example.events.EventManager;
import org.example.model.AlgorithmEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SortingVisualizer {

    private static final double MIN_HEIGHT = 70;
    private static final double MAX_HEIGHT = 380;

    private final HBox container;
    private final List<Rectangle> bars = new ArrayList<>();
    private final List<Label> labels = new ArrayList<>();
    private int[] values;
    private Timeline currentTimeline;
    private int foundIndex = -1;
    private int runningMax = 1;
    private final Set<Integer> sortedIndices = new HashSet<>();

    public SortingVisualizer(HBox container) {
        this.container = container;
        this.container.setAlignment(Pos.BOTTOM_CENTER);
        this.container.setSpacing(18);
    }

    private double barHeight(int value) {
        double ratio = Math.min(1.0, (double) value / runningMax);
        return MIN_HEIGHT + ratio * (MAX_HEIGHT - MIN_HEIGHT);
    }

    public void setData(int[] data) {
        this.values = data.clone();
        this.foundIndex = -1;
        sortedIndices.clear();

        runningMax = 1;
        for (int v : values) runningMax = Math.max(runningMax, v);

        bars.clear();
        labels.clear();
        container.getChildren().clear();

        for (int i = 0; i < values.length; i++) {
            Rectangle bar = new Rectangle(42, barHeight(values[i]));
            bar.setFill(Palette.DEFAULT);
            bar.setArcWidth(8);
            bar.setArcHeight(8);

            DropShadow glow = new DropShadow();
            glow.setColor(Palette.GLOW);
            glow.setRadius(6);
            glow.setSpread(0.12);
            bar.setEffect(glow);

            Label label = new Label(String.valueOf(values[i]));
            label.setStyle("-fx-font-size: 13px; -fx-text-fill: #F4E6F0; -fx-font-weight: bold;");

            VBox column = new VBox(label, bar);
            column.setAlignment(Pos.BOTTOM_CENTER);
            column.setSpacing(4);

            bars.add(bar);
            labels.add(label);
            container.getChildren().add(column);
        }
    }

    public void play(List<AlgorithmEvent> events, EventManager eventManager, double stepDuration) {
        stop();
        currentTimeline = new Timeline();

        for (int i = 0; i < events.size(); i++) {
            AlgorithmEvent event = events.get(i);
            double time = (i + 1) * stepDuration;
            KeyFrame frame = new KeyFrame(Duration.millis(time), e -> {
                applyEvent(event);
                eventManager.publish(event);
            });
            currentTimeline.getKeyFrames().add(frame);
        }
        currentTimeline.play();
    }

    public void stop() {
        if (currentTimeline != null) currentTimeline.stop();
    }

    private void applyEvent(AlgorithmEvent event) {
        for (int i = 0; i < bars.size(); i++) {
            if (i != foundIndex) {
                bars.get(i).setFill(sortedIndices.contains(i) ? Palette.SORTED : Palette.DEFAULT);
            }
        }

        switch (event.getType()) {
            case COMPARE -> {
                highlight(event.getIndexA(), Palette.COMPARE);
                highlight(event.getIndexB(), Palette.COMPARE);
            }
            case SWAP -> {
                swapValues(event.getIndexA(), event.getIndexB());
                highlight(event.getIndexA(), Palette.SWAP);
                highlight(event.getIndexB(), Palette.SWAP);
            }
            case UPDATE -> {
                int value = event.getValue();
                if (value > runningMax) runningMax = value;
                bars.get(event.getIndexA()).setHeight(barHeight(value));
                labels.get(event.getIndexA()).setText(String.valueOf(value));
                highlight(event.getIndexA(), Palette.SWAP);
            }
            case VISIT -> highlight(event.getIndexA(), Palette.COMPARE);
            case FOUND -> {
                foundIndex = event.getIndexA();
                highlight(foundIndex, Palette.FOUND);
            }
            case SORTED -> {
                for (int i = event.getIndexA(); i <= event.getIndexB(); i++) {
                    if (i >= 0 && i < bars.size()) {
                        sortedIndices.add(i);
                        highlight(i, Palette.SORTED);
                    }
                }
            }
            case COMPLETE -> {
                for (int i = 0; i < bars.size(); i++) {
                    if (i != foundIndex) {
                        bars.get(i).setFill(sortedIndices.contains(i) ? Palette.SORTED : Palette.DONE);
                    }
                }
            }
            default -> {}
        }
    }

    private void highlight(int index, Color color) {
        if (index >= 0 && index < bars.size()) bars.get(index).setFill(color);
    }

    private void swapValues(int i, int j) {
        if (i < 0 || j < 0 || i >= values.length || j >= values.length) return;
        int temp = values[i];
        values[i] = values[j];
        values[j] = temp;

        bars.get(i).setHeight(barHeight(values[i]));
        bars.get(j).setHeight(barHeight(values[j]));
        labels.get(i).setText(String.valueOf(values[i]));
        labels.get(j).setText(String.valueOf(values[j]));
    }
}