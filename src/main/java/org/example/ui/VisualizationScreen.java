package org.example.ui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import org.example.algorithms.*;
import org.example.events.ConsoleLogger;
import org.example.events.EventManager;
import org.example.model.AlgorithmEvent;
import org.example.model.EventType;
import org.example.model.Graph;
import org.example.model.WeightedGraph;
import org.example.music.AmbientPlayer;
import org.example.music.MidiManager;
import org.example.music.MusicPlayer;
import org.example.visualization.GraphVisualizer;
import org.example.visualization.SortingVisualizer;

import java.util.*;
import java.util.function.Supplier;

public class VisualizationScreen {

    private final SceneManager sceneManager;
    private static final String FONT = "-fx-font-family: 'Baloo 2';";
    private static final String BUTTON_STYLE = "-fx-background-color: #502A50; -fx-text-fill: #ECD0EC; -fx-background-radius: 8; -fx-padding: 8 18; -fx-font-size: 14px; " + FONT;

    private static final String COMBO_STYLE =
            "-fx-background-color: #6B3F63; -fx-background-radius: 10; -fx-text-fill: #FFFFFF; " +
                    "-fx-mark-color: #ECD0EC; -fx-font-size: 14px; " + FONT;

    public VisualizationScreen(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void launch(String category, String algorithmName, String inputText, String targetText) {
        switch (category) {
            case "Sorting" -> {
                int[] data = parseNumbers(inputText);
                buildBarScreen(algorithmName, data, () -> buildSortAlgorithm(algorithmName, data.clone()));
            }
            case "Searching" -> {
                int[] data = parseNumbers(inputText);
                int target = Integer.parseInt(targetText.trim());
                if (algorithmName.equals("Binary Search")) Arrays.sort(data);
                buildBarScreen(algorithmName, data, () -> algorithmName.equals("Binary Search")
                        ? new BinarySearch(data.clone(), target)
                        : new LinearSearch(data.clone(), target));
            }
            case "Graph" -> {
                boolean weighted = algorithmName.equals("Dijkstra");
                String edgesText = inputText.trim().isEmpty() ? "1-2, 1-3, 2-4, 3-4" : inputText;
                List<int[]> rawEdges = parseGraphEdges(edgesText);
                List<Integer> nodes = extractNodes(rawEdges);
                int startNode = targetText.trim().isEmpty() ? nodes.get(0) : Integer.parseInt(targetText.trim());
                List<int[]> displayEdges = new ArrayList<>();
                for (int[] e : rawEdges) {
                    if (weighted) displayEdges.add(new int[]{e[0], e[1], e.length > 2 ? e[2] : 1});
                    else displayEdges.add(new int[]{e[0], e[1]});
                }

                if (weighted) {
                    WeightedGraph wg = new WeightedGraph();
                    for (int[] e : rawEdges) wg.addEdge(e[0], e[1], e.length > 2 ? e[2] : 1);
                    buildGraphScreen(new Dijkstra(wg, startNode), nodes, displayEdges, algorithmName);
                } else {
                    Graph g = new Graph();
                    for (int[] e : rawEdges) g.addEdge(e[0], e[1]);
                    Algorithm algorithm = algorithmName.equals("DFS") ? new DFS(g, startNode) : new BFS(g, startNode);
                    buildGraphScreen(algorithm, nodes, displayEdges, algorithmName);
                }
            }

            default -> throw new IllegalArgumentException("Unknown category");
        }
    }

    private Algorithm buildSortAlgorithm(String name, int[] data) {
        return switch (name) {
            case "Selection Sort" -> new SelectionSort(data);
            case "Insertion Sort" -> new InsertionSort(data);
            case "Quick Sort" -> new QuickSort(data);
            case "Merge Sort" -> new MergeSort(data);
            default -> new BubbleSort(data);
        };
    }

    // Parses "1-2, 1-3:4, 2-4" into [a, b] or [a, b, weight] arrays.
    private List<int[]> parseGraphEdges(String text) {
        String[] tokens = text.trim().split("\\s*,\\s*");
        List<int[]> edges = new ArrayList<>();
        for (String token : tokens) {
            if (token.isBlank()) continue;
            String[] weightSplit = token.split(":");
            String[] nodePair = weightSplit[0].split("-");
            int a = Integer.parseInt(nodePair[0].trim());
            int b = Integer.parseInt(nodePair[1].trim());
            if (weightSplit.length > 1) {
                int w = Integer.parseInt(weightSplit[1].trim());
                edges.add(new int[]{a, b, w});
            } else {
                edges.add(new int[]{a, b});
            }
        }
        return edges;
    }

    private List<Integer> extractNodes(List<int[]> edges) {
        LinkedHashSet<Integer> nodes = new LinkedHashSet<>();
        for (int[] e : edges) { nodes.add(e[0]); nodes.add(e[1]); }
        return new ArrayList<>(nodes);
    }

    private int[] parseNumbers(String text) {
        String[] parts = text.trim().split("\\s*,\\s*");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) result[i] = Integer.parseInt(parts[i]);
        return result;
    }

    private ListView<AlgorithmEvent> buildStepLog() {
        ListView<AlgorithmEvent> log = new ListView<>();
        log.setStyle("-fx-control-inner-background: #461D3A; -fx-background-radius: 10; " + FONT);
        VBox.setVgrow(log, Priority.ALWAYS);
        return log;
    }

    private VBox buildSidebar(VBox infoPanel, ListView<AlgorithmEvent> stepLog, VBox metricsView, VBox legendView) {
        Label logHeading = new Label("🗒 What's Happening");
        logHeading.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 20px; -fx-font-weight: bold; " + FONT);

        VBox logBox = new VBox(10, logHeading, stepLog);
        logBox.setStyle("-fx-background-color: #502A50; -fx-padding: 18; -fx-background-radius: 14; " +
                "-fx-border-color: #ECD0EC; -fx-border-width: 1.5; -fx-border-radius: 14;");
        logBox.setPrefHeight(300);
        VBox.setVgrow(stepLog, Priority.ALWAYS);

        VBox content = new VBox(16, infoPanel, logBox, metricsView, legendView);
        content.setPadding(new Insets(16, 16, 16, 0));

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setPrefWidth(360);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        VBox sidebar = new VBox(scroll);
        sidebar.setPrefWidth(360);
        VBox.setVgrow(sidebar, Priority.ALWAYS);
        return sidebar;
    }

    private void buildBarScreen(String title, int[] originalData, Supplier<Algorithm> algorithmFactory) {
        AmbientPlayer.start();
        AmbientPlayer.stop();

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 26px; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; " + FONT);

        HBox barContainer = new HBox();
        barContainer.setAlignment(Pos.BOTTOM_CENTER);
        VBox.setVgrow(barContainer, Priority.ALWAYS);

        SortingVisualizer visualizer = new SortingVisualizer(barContainer);
        visualizer.setData(originalData);

        ListView<AlgorithmEvent> stepLog = buildStepLog();
        MetricsPanel metrics = new MetricsPanel();
        VBox legend = ColorLegend.build();
        VBox info = InfoPanel.build(title);

        EventManager eventManager = new EventManager();
        eventManager.addListener(new ConsoleLogger());
        eventManager.addListener(new MusicPlayer(new MidiManager()));
        eventManager.addListener(new StepNarrator(stepLog));
        eventManager.addListener(metrics);

        ComboBox<String> speedSelector = new ComboBox<>();
        speedSelector.getItems().addAll("Slow", "Normal", "Fast");
        speedSelector.setValue("Normal");
        speedSelector.setStyle(COMBO_STYLE);

        Button startButton = new Button("Start");
        Button stopButton = new Button("Stop");
        Button backButton = new Button("Back");
        startButton.setStyle(BUTTON_STYLE);
        stopButton.setStyle(BUTTON_STYLE);
        backButton.setStyle(BUTTON_STYLE);

        StackPane rootStack = new StackPane();
        rootStack.setPrefSize(1100, 600);

        boolean[] found = {false};

        startButton.setOnAction(e -> {
            stepLog.getItems().clear();
            metrics.reset();
            double stepDuration = switch (speedSelector.getValue()) {
                case "Slow" -> 1200; case "Fast" -> 300; default -> 700;
            };
            visualizer.setData(originalData);
            Algorithm algorithm = algorithmFactory.get();
            found[0] = false;
            visualizer.play(algorithm.execute(), eventManager, stepDuration);
        });

        eventManager.addListener(event -> {
            if (event.getType() == EventType.FOUND) found[0] = true;
            if (event.getType() == EventType.COMPLETE) showCelebration(rootStack, completionMessage(title, found[0]));
        });

        stopButton.setOnAction(e -> visualizer.stop());
        backButton.setOnAction(e -> { visualizer.stop(); new SelectionScreen(sceneManager).show(); });

        HBox controls = new HBox(28, titleLabel, speedSelector, startButton, stopButton, backButton);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(16));

        VBox leftSide = new VBox(controls, barContainer);
        leftSide.setStyle("-fx-background-color: transparent;");
        HBox.setHgrow(leftSide, Priority.ALWAYS);

        HBox mainArea = new HBox(leftSide, buildSidebar(info, stepLog, metrics.getView(), legend));
        mainArea.setStyle("-fx-background-color: transparent;");

        Region bg = new Region();
        bg.setStyle("-fx-background-color: #461D3A;");
        rootStack.getChildren().addAll(bg, Doodle.backgroundLayer(rootStack), mainArea);

        sceneManager.show(rootStack);
    }

    private void buildGraphScreen(Algorithm algorithm, List<Integer> nodes, List<int[]> edges, String displayName) {
        AmbientPlayer.start();
        AmbientPlayer.stop();

        Label titleLabel = new Label(algorithm.getName());
        titleLabel.setStyle("-fx-font-size: 26px; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; " + FONT);

        Pane graphPane = new Pane();
        graphPane.setPrefSize(600, 360);

        StackPane graphHolder = new StackPane(graphPane);
        graphHolder.setStyle("-fx-background-color: rgba(80,42,80,0.65); -fx-background-radius: 20;");
        graphHolder.setPadding(new Insets(10));
        graphHolder.setMaxSize(640, 400);
        VBox.setVgrow(graphHolder, Priority.ALWAYS);

        GraphVisualizer visualizer = new GraphVisualizer(graphPane);
        visualizer.layout(nodes, edges);

        ListView<AlgorithmEvent> stepLog = buildStepLog();
        MetricsPanel metrics = new MetricsPanel();
        VBox legend = ColorLegend.build();
        VBox info = InfoPanel.build(displayName);

        EventManager eventManager = new EventManager();
        eventManager.addListener(new ConsoleLogger());
        eventManager.addListener(new MusicPlayer(new MidiManager()));
        eventManager.addListener(new StepNarrator(stepLog));
        eventManager.addListener(metrics);

        ComboBox<String> speedSelector = new ComboBox<>();
        speedSelector.getItems().addAll("Slow", "Normal", "Fast");
        speedSelector.setValue("Normal");
        speedSelector.setStyle(COMBO_STYLE);

        Button startButton = new Button("Start");
        Button backButton = new Button("Back");
        startButton.setStyle(BUTTON_STYLE);
        backButton.setStyle(BUTTON_STYLE);

        StackPane rootStack = new StackPane();
        rootStack.setPrefSize(1100, 600);

        boolean[] found = {false};

        startButton.setOnAction(e -> {
            stepLog.getItems().clear();
            metrics.reset();
            double stepDuration = switch (speedSelector.getValue()) {
                case "Slow" -> 1200; case "Fast" -> 300; default -> 700;
            };
            visualizer.layout(nodes, edges);
            found[0] = false;
            visualizer.play(algorithm.execute(), eventManager, stepDuration);
        });

        eventManager.addListener(event -> {
            if (event.getType() == EventType.FOUND) found[0] = true;
            if (event.getType() == EventType.COMPLETE) showCelebration(rootStack, completionMessage(displayName, found[0]));
        });

        backButton.setOnAction(e -> { visualizer.stop(); new SelectionScreen(sceneManager).show(); });

        HBox controls = new HBox(28, titleLabel, speedSelector, startButton, backButton);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(16));

        VBox centerWrap = new VBox(graphHolder);
        centerWrap.setAlignment(Pos.CENTER);
        VBox.setVgrow(centerWrap, Priority.ALWAYS);

        VBox leftSide = new VBox(controls, centerWrap);
        leftSide.setStyle("-fx-background-color: transparent;");
        HBox.setHgrow(leftSide, Priority.ALWAYS);

        HBox mainArea = new HBox(leftSide, buildSidebar(info, stepLog, metrics.getView(), legend));
        mainArea.setStyle("-fx-background-color: transparent;");

        Region bg = new Region();
        bg.setStyle("-fx-background-color: #461D3A;");
        rootStack.getChildren().addAll(bg, Doodle.backgroundLayer(rootStack), mainArea);

        sceneManager.show(rootStack);
    }

    private String completionMessage(String name, boolean found) {
        return switch (name) {
            case "Linear Search", "Binary Search" -> found ? "🎯 Found!" : "🔍 Not Found";
            case "Bubble Sort", "Selection Sort", "Insertion Sort", "Quick Sort", "Merge Sort" -> "Sorted!";
            case "BFS", "DFS" -> "Traversal Complete!";
            case "Dijkstra" -> "Shortest Paths Ready!";
            default -> "Done!";
        };
    }

    private void showCelebration(StackPane overlayHost, String message) {
        Label popup = new Label(message);
        popup.setStyle("-fx-font-size: 52px; -fx-text-fill: #FFFFFF; -fx-font-weight: bold; " +
                "-fx-background-color: rgba(126,42,83,0.95); -fx-padding: 30 60; -fx-background-radius: 30; " +
                "-fx-border-color: #ECD0EC; -fx-border-width: 3; -fx-border-radius: 30;");

        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#ECD0EC"));
        glow.setRadius(30);
        glow.setSpread(0.4);
        popup.setEffect(glow);

        popup.setScaleX(0.2);
        popup.setScaleY(0.2);
        overlayHost.getChildren().add(popup);

        ScaleTransition pop = new ScaleTransition(Duration.millis(350), popup);
        pop.setToX(1); pop.setToY(1);

        PauseTransition stay = new PauseTransition(Duration.seconds(1.8));

        FadeTransition fade = new FadeTransition(Duration.millis(450), popup);
        fade.setFromValue(1); fade.setToValue(0);
        fade.setOnFinished(e -> overlayHost.getChildren().remove(popup));

        new SequentialTransition(pop, stay, fade).play();
    }
}