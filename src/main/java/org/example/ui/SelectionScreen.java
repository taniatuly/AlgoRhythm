package org.example.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import org.example.music.AmbientPlayer;

import java.util.*;

public class SelectionScreen {

    private final SceneManager sceneManager;
    private static final String FONT = "-fx-font-family: 'Baloo 2';";

    private static final String FIELD_STYLE =
            "-fx-background-color: #6B3F63; -fx-text-fill: #FFFFFF; -fx-prompt-text-fill: #E0C2D6; " +
                    "-fx-background-radius: 14; -fx-padding: 12 16; -fx-font-size: 15px; " + FONT;

    private static final String COMBO_STYLE =
            "-fx-background-color: #6B3F63; -fx-background-radius: 14; -fx-text-fill: #FFFFFF; " +
                    "-fx-mark-color: #ECD0EC; -fx-font-size: 15px; " + FONT;

    private static final Map<String, List<String>> CATEGORIES = new LinkedHashMap<>();
    static {
        CATEGORIES.put("Sorting", List.of("Bubble Sort", "Selection Sort", "Insertion Sort", "Quick Sort", "Merge Sort"));
        CATEGORIES.put("Searching", List.of("Linear Search", "Binary Search"));
        CATEGORIES.put("Graph", List.of("BFS", "DFS", "Dijkstra"));
    }

    public SelectionScreen(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void show() {
        AmbientPlayer.start();

        Label title = new Label("Choose an Algorithm");
        title.setStyle("-fx-font-size: 38px; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; " + FONT);

        DropShadow titleGlow = new DropShadow();
        titleGlow.setColor(Color.web("#ECD0EC"));
        titleGlow.setRadius(18);
        titleGlow.setSpread(0.3);
        title.setEffect(titleGlow);

        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll(CATEGORIES.keySet());
        categoryBox.setValue("Sorting");
        categoryBox.setStyle(COMBO_STYLE);

        ComboBox<String> algorithmBox = new ComboBox<>();
        algorithmBox.getItems().addAll(CATEGORIES.get("Sorting"));
        algorithmBox.setValue("Bubble Sort");
        algorithmBox.setStyle(COMBO_STYLE);

        Label hintLabel = new Label(hintFor("Sorting"));
        hintLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 15px; -fx-font-weight: bold; " + FONT);
        hintLabel.setWrapText(true);

        TextField inputField = new TextField();
        inputField.setStyle(FIELD_STYLE);
        inputField.setPrefWidth(360);

        TextField targetField = new TextField();
        targetField.setStyle(FIELD_STYLE);
        targetField.setPrefWidth(360);

        updatePrompts("Sorting", inputField, targetField);

        categoryBox.setOnAction(e -> {
            algorithmBox.getItems().setAll(CATEGORIES.get(categoryBox.getValue()));
            algorithmBox.setValue(algorithmBox.getItems().get(0));
            hintLabel.setText(hintFor(categoryBox.getValue()));
            updatePrompts(categoryBox.getValue(), inputField, targetField);
        });

        Button randomButton = new Button("Generate Random Data");
        randomButton.setStyle("-fx-background-color: #8A4F7E; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; " +
                "-fx-background-radius: 14; -fx-padding: 10 20; -fx-font-size: 14px; " + FONT);
        randomButton.setOnAction(e -> {
            if (categoryBox.getValue().equals("Graph")) {
                inputField.setText(generateRandomGraph(algorithmBox.getValue().equals("Dijkstra")));
            } else {
                inputField.setText(generateRandomNumbers());
            }
        });

        Button startButton = new Button("Start");
        startButton.setStyle("-fx-background-color: #ECD0EC; -fx-text-fill: #461D3A; " +
                "-fx-font-weight: bold; -fx-background-radius: 16; -fx-padding: 12 40; -fx-font-size: 16px;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold; " + FONT);

        startButton.setOnAction(e -> {
            try {
                new VisualizationScreen(sceneManager).launch(
                        categoryBox.getValue(), algorithmBox.getValue(),
                        inputField.getText(), targetField.getText());
            } catch (Exception ex) {
                errorLabel.setText("Please check your input — " + ex.getMessage());
            }
        });

        VBox form = new VBox(18, title, categoryBox, algorithmBox, hintLabel, inputField, targetField, randomButton, startButton, errorLabel);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(50));
        form.setMaxWidth(480);
        form.setStyle("-fx-background-color: rgba(70,29,58,0.6); -fx-background-radius: 24;");

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #461D3A;");
        StackPane.setAlignment(form, Pos.CENTER);
        root.getChildren().addAll(Doodle.backgroundLayer(root), form);

        sceneManager.show(root);
    }

    private String hintFor(String category) {
        return switch (category) {
            case "Searching" -> "Input: numbers + a target, e.g. numbers: 5, 2, 8, 1, 6 | target: 8";
            case "Graph" -> "Input: edges as 1-2, 1-3, 2-4 (add :weight for Dijkstra, e.g. 1-2:4). Start node goes in the second field — optional.";
            default -> "Input: comma-separated numbers, e.g. 5, 2, 8, 1, 6";
        };
    }

    private void updatePrompts(String category, TextField inputField, TextField targetField) {
        switch (category) {
            case "Searching" -> {
                inputField.setPromptText("e.g. 5, 2, 8, 1, 6");
                targetField.setPromptText("Target value to find");
                targetField.setDisable(false);
            }
            case "Graph" -> {
                inputField.setPromptText("e.g. 1-2, 1-3, 2-4:5 (edges, weight optional)");
                targetField.setPromptText("Start node (optional)");
                targetField.setDisable(false);
            }
            default -> {
                inputField.setPromptText("e.g. 5, 2, 8, 1, 6");
                targetField.setPromptText("Not used for this category");
                targetField.setDisable(true);
            }
        }
    }

    // Random connected graph: 5-7 nodes, protita node kono na kono node-er sathe connected.
    private String generateRandomGraph(boolean weighted) {
        Random random = new Random();
        int nodeCount = 5 + random.nextInt(3);
        Set<String> used = new HashSet<>();
        List<String> edges = new ArrayList<>();

        // Spanning tree: protita notun node ager ekta node-er sathe jure
        for (int i = 2; i <= nodeCount; i++) {
            addRandomEdge(edges, used, 1 + random.nextInt(i - 1), i, weighted, random);
        }

        // Kichu extra edge, jeno cycle thake
        int extra = 2 + random.nextInt(2);
        for (int tries = 0; tries < 20 && extra > 0; tries++) {
            int a = 1 + random.nextInt(nodeCount);
            int b = 1 + random.nextInt(nodeCount);
            if (a != b && !used.contains(Math.min(a, b) + "-" + Math.max(a, b))) {
                addRandomEdge(edges, used, a, b, weighted, random);
                extra--;
            }
        }
        return String.join(", ", edges);
    }

    private void addRandomEdge(List<String> edges, Set<String> used, int a, int b,
                               boolean weighted, Random random) {
        used.add(Math.min(a, b) + "-" + Math.max(a, b));
        edges.add(a + "-" + b + (weighted ? ":" + (1 + random.nextInt(9)) : ""));
    }

    private String generateRandomNumbers() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        int size = 6 + random.nextInt(3);
        for (int i = 0; i < size; i++) {
            sb.append(random.nextInt(50) + 1);
            if (i < size - 1) sb.append(", ");
        }
        return sb.toString();
    }
}