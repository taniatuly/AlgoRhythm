package org.example.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import org.example.events.EventListener;
import org.example.model.AlgorithmEvent;

public class StepNarrator implements EventListener {

    private final ListView<AlgorithmEvent> logView;

    public StepNarrator(ListView<AlgorithmEvent> logView) {
        this.logView = logView;
        logView.setCellFactory(list -> new StepCell());
    }

    @Override
    public void onEvent(AlgorithmEvent event) {
        logView.getItems().add(event);
        logView.scrollTo(logView.getItems().size() - 1);
    }

    private static class StepCell extends ListCell<AlgorithmEvent> {
        @Override
        protected void updateItem(AlgorithmEvent event, boolean empty) {
            super.updateItem(event, empty);
            if (empty || event == null) {
                setText(null);
                setGraphic(null);
                setStyle("-fx-background-color: transparent;");
                return;
            }

            String icon = switch (event.getType()) {
                case COMPARE -> "🔍";
                case SWAP -> "🔄";
                case VISIT -> "👁";
                case FOUND -> "🎯";
                case MOVE -> "➡";
                case BACKTRACK -> "↩";
                case UPDATE -> "➖";
                case SORTED -> "📌";
                case RELAX -> "🔍";
                case IMPROVE -> "⬇";
                case SETTLE -> "📍";
                case COMPLETE -> "✅";
            };

            // Purple/rose family only — no yellow or green anywhere.
            String bg = switch (event.getType()) {
                case COMPARE -> "#6B3F63";
                case SWAP -> "#8A4F7E";
                case FOUND -> "#A2578E";
                case COMPLETE -> "#3A1F30";
                case SORTED -> "#9B5C8A";
                case IMPROVE -> "#8A4F7E";
                case SETTLE -> "#9B5C8A";
                default -> "#5A3550";
            };

            Label label = new Label(icon + "  " + describe(event));
            label.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 15px; -fx-font-family: 'Baloo 2'; -fx-font-weight: bold;");
            label.setWrapText(true);
            label.setMaxWidth(320);

            setGraphic(label);
            setPadding(new Insets(10, 8, 10, 8));
            setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 8;");
        }

        private String describe(AlgorithmEvent event) {
            return switch (event.getType()) {
                case COMPARE -> "Comparing position " + event.getIndexA() + " & " + event.getIndexB();
                case SWAP -> "Swapping position " + event.getIndexA() + " & " + event.getIndexB();
                case VISIT -> "Visiting node " + event.getIndexA();
                case FOUND -> "Target found at position " + event.getIndexA() + "!";
                case MOVE -> "Moving node " + event.getIndexA() + " → " + event.getIndexB();
                case BACKTRACK -> "Backtracking from node " + event.getIndexA() + " to node " + event.getIndexB();
                case UPDATE -> "Position " + event.getIndexA() + " updated to " + event.getValue();
                case SORTED -> event.getIndexA() == event.getIndexB()
                        ? "Position " + event.getIndexA() + " is now in its final place"
                        : "Positions " + event.getIndexA() + " to " + event.getIndexB() + " are now in their final place";
                case RELAX -> "Checking " + event.getIndexA() + " → " + event.getIndexB()
                        + ": distance would be " + event.getValue();
                case IMPROVE -> event.getIndexB() < 0
                        ? "Start node " + event.getIndexA() + ": distance 0"
                        : "Shorter path to node " + event.getIndexA() + " found: distance "
                        + event.getValue() + " (via " + event.getIndexB() + ")";
                case SETTLE -> "Node " + event.getIndexA() + " is final: shortest distance = " + event.getValue();
                case COMPLETE -> "All done!";
            };
        }
    }
}