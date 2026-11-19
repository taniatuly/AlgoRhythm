package org.example.visualization;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;

import org.example.events.EventManager;
import org.example.model.AlgorithmEvent;
import org.example.model.EventType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GraphVisualizer {

    private static final double NODE_RADIUS = 22;
    private static final double AREA_W = 600, AREA_H = 340, PAD = 48;
    private static final double MIN_NODE_DISTANCE = 100;  // duita node-er majhe minimum duroto

    private static final double ARROW_POS = 0.5;          // line-er thik majhkhane
    private static final double ARROW_OFFSET = 9;        // line theke koto dure (pashe) boshbe
    private static final double ARROW_OFFSET_WEIGHTED = 20; // weight label thakle aro dure
    private static final double WEIGHT_POS = 0.5;         // weight label line-er majhkhane

    private static final Color NODE_COLOR = Palette.DEFAULT;
    private static final Color CURRENT_COLOR = Palette.COMPARE;   // ekhon process hocche
    private static final Color FOUND_COLOR = Palette.SWAP;        // path/node pawa geche, final na
    private static final Color SETTLED_COLOR = Palette.SORTED;    // final (green)
    private static final Color LINE_COLOR = Color.web("#9C6B92"); // normal line
    private static final Color LOOKING_COLOR = Palette.SWAP;      // Dijkstra: ekhon check hocche
    private static final Color FORWARD_COLOR = Palette.COMPARE;   // forward arrow + traverse howa line
    private static final Color BACKTRACK_COLOR = Color.web("#FF8FB1"); // backward arrow
    private static final Color PATH_COLOR = Color.web("#FFFFFF");

    private final Pane pane;
    private final Map<Integer, Circle> nodeCircles = new HashMap<>();
    private final Map<Integer, Label> distLabels = new HashMap<>();
    private final Map<String, Line> edgeLines = new HashMap<>();
    private final Map<Integer, double[]> positions = new HashMap<>();
    private final Map<Integer, Polygon> pathArrows = new HashMap<>();
    private final Set<Integer> settled = new HashSet<>();
    private final Map<Integer, Integer> parentOf = new HashMap<>();
    private final Set<Integer> visitedNodes = new HashSet<>();
    private final Set<String> traversed = new HashSet<>();
    private boolean weighted = false;
    private boolean dfsMode = false;
    private Integer currentNode = null;
    private String lastLooked = null;
    private Timeline currentTimeline;

    public GraphVisualizer(Pane pane) {
        this.pane = pane;
    }

    public void layout(List<Integer> nodes, List<int[]> edges) {
        pane.getChildren().clear();
        nodeCircles.clear();
        distLabels.clear();
        edgeLines.clear();
        positions.clear();
        pathArrows.clear();
        settled.clear();
        parentOf.clear();
        visitedNodes.clear();
        traversed.clear();
        currentNode = null;
        lastLooked = null;

        weighted = false;
        for (int[] e : edges) if (e.length > 2) weighted = true;

        computePositions(nodes, edges);

        // 1. Line-gulo
        for (int[] edge : edges) {
            double[] a = positions.get(edge[0]);
            double[] b = positions.get(edge[1]);
            if (a == null || b == null) continue;
            Line line = new Line(a[0], a[1], b[0], b[1]);
            line.setStroke(LINE_COLOR);
            line.setStrokeWidth(2.5);
            pane.getChildren().add(line);
            edgeLines.put(edgeKey(edge[0], edge[1]), line);
        }

        // 2. Weight label (shudhu Dijkstra-te)
        for (int[] edge : edges) {
            if (edge.length < 3) continue;
            double[] a = positions.get(edge[0]);
            double[] b = positions.get(edge[1]);
            if (a == null || b == null) continue;
            double mx = a[0] + (b[0] - a[0]) * WEIGHT_POS;
            double my = a[1] + (b[1] - a[1]) * WEIGHT_POS;

            Label w = new Label(String.valueOf(edge[2]));
            w.setStyle("-fx-background-color: #461D3A; -fx-text-fill: #FFFFFF; -fx-font-size: 12px; " +
                    "-fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 1 5; -fx-font-family: 'Baloo 2';");
            w.setMinWidth(26);
            w.setAlignment(Pos.CENTER);
            w.setLayoutX(mx - 13);
            w.setLayoutY(my - 10);
            w.setMouseTransparent(true);
            pane.getChildren().add(w);
        }

        // 3. Node-gulo
        for (int node : nodes) {
            double[] p = positions.get(node);
            Circle circle = new Circle(p[0], p[1], NODE_RADIUS, NODE_COLOR);
            circle.setStroke(Color.web("#461D3A"));
            circle.setStrokeWidth(2);

            Label label = new Label(String.valueOf(node));
            label.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-family: 'Baloo 2';");
            label.setMinWidth(NODE_RADIUS * 2);
            label.setPrefWidth(NODE_RADIUS * 2);
            label.setAlignment(Pos.CENTER);
            label.setLayoutX(p[0] - NODE_RADIUS);
            label.setLayoutY(p[1] - 11);
            label.setMouseTransparent(true);

            pane.getChildren().addAll(circle, label);
            nodeCircles.put(node, circle);

            if (weighted) {
                Label dist = new Label("d=∞");
                dist.setStyle("-fx-text-fill: #ECD0EC; -fx-font-size: 13px; -fx-font-weight: bold; -fx-font-family: 'Baloo 2';");
                dist.setMinWidth(60);
                dist.setAlignment(Pos.CENTER);
                dist.setLayoutX(p[0] - 30);
                dist.setLayoutY(p[1] + NODE_RADIUS + 1);
                dist.setMouseTransparent(true);
                pane.getChildren().add(dist);
                distLabels.put(node, dist);
            }
        }
    }

    // Force-directed layout + shesh-e separation pass, jeno node gulo ektar upor arekta na ashe.
    private void computePositions(List<Integer> nodes, List<int[]> edges) {
        int n = nodes.size();
        if (n == 0) return;
        if (n == 1) {
            positions.put(nodes.get(0), new double[]{AREA_W / 2, AREA_H / 2});
            return;
        }

        Map<Integer, Integer> index = new HashMap<>();
        for (int i = 0; i < n; i++) index.put(nodes.get(i), i);

        double[] x = new double[n];
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            x[i] = AREA_W / 2 + 120 * Math.cos(angle);
            y[i] = AREA_H / 2 + 120 * Math.sin(angle);
        }

        double k = Math.sqrt(AREA_W * AREA_H / n) * 0.8;
        double temp = AREA_W / 8;
        int iterations = 300;

        for (int it = 0; it < iterations; it++) {
            double[] dx = new double[n];
            double[] dy = new double[n];

            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    double ddx = x[i] - x[j];
                    double ddy = y[i] - y[j];
                    double dist = Math.max(0.01, Math.sqrt(ddx * ddx + ddy * ddy));
                    double force = (k * k) / dist;
                    dx[i] += ddx / dist * force;
                    dy[i] += ddy / dist * force;
                    dx[j] -= ddx / dist * force;
                    dy[j] -= ddy / dist * force;
                }
            }

            for (int[] e : edges) {
                Integer ai = index.get(e[0]);
                Integer bi = index.get(e[1]);
                if (ai == null || bi == null) continue;
                double ddx = x[ai] - x[bi];
                double ddy = y[ai] - y[bi];
                double dist = Math.max(0.01, Math.sqrt(ddx * ddx + ddy * ddy));
                double force = (dist * dist) / k;
                dx[ai] -= ddx / dist * force;
                dy[ai] -= ddy / dist * force;
                dx[bi] += ddx / dist * force;
                dy[bi] += ddy / dist * force;
            }

            for (int i = 0; i < n; i++) {
                double len = Math.max(0.01, Math.sqrt(dx[i] * dx[i] + dy[i] * dy[i]));
                double step = Math.min(len, temp);
                x[i] += dx[i] / len * step;
                y[i] += dy[i] / len * step;
            }
            temp *= 0.97;
        }

        // Area-r bhitore fit kora
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            minX = Math.min(minX, x[i]); maxX = Math.max(maxX, x[i]);
            minY = Math.min(minY, y[i]); maxY = Math.max(maxY, y[i]);
        }
        double spanX = Math.max(1, maxX - minX);
        double spanY = Math.max(1, maxY - minY);

        for (int i = 0; i < n; i++) {
            x[i] = PAD + (x[i] - minX) / spanX * (AREA_W - 2 * PAD);
            y[i] = PAD + (y[i] - minY) / spanY * (AREA_H - 2 * PAD);
        }

        // Separation pass: kono duita node MIN_NODE_DISTANCE-er cheye kache thakle thele soriye dao
        for (int pass = 0; pass < 80; pass++) {
            boolean moved = false;
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    double ddx = x[i] - x[j];
                    double ddy = y[i] - y[j];
                    double d = Math.sqrt(ddx * ddx + ddy * ddy);
                    if (d < MIN_NODE_DISTANCE) {
                        double ux, uy;
                        if (d < 0.01) { ux = 1; uy = 0; } else { ux = ddx / d; uy = ddy / d; }
                        double push = (MIN_NODE_DISTANCE - d) / 2;
                        x[i] += ux * push;
                        y[i] += uy * push;
                        x[j] -= ux * push;
                        y[j] -= uy * push;
                        moved = true;
                    }
                }
            }
            for (int i = 0; i < n; i++) {
                x[i] = Math.max(PAD, Math.min(AREA_W - PAD, x[i]));
                y[i] = Math.max(PAD, Math.min(AREA_H - PAD, y[i]));
            }
            if (!moved) break;
        }

        for (int i = 0; i < n; i++) {
            positions.put(nodes.get(i), new double[]{x[i], y[i]});
        }
    }

    public void play(List<AlgorithmEvent> events, EventManager eventManager, double stepDuration) {
        stop();

        // BACKTRACK event thakle eta DFS, na hole BFS (ba Dijkstra)
        dfsMode = false;
        for (AlgorithmEvent ev : events) {
            if (ev.getType() == EventType.BACKTRACK) { dfsMode = true; break; }
        }

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
        clearLooking();

        switch (event.getType()) {
            // ---------- BFS / DFS ----------
            case VISIT -> {
                int node = event.getIndexA();
                // BFS: porer node visit shuru hole ager node-er kaj shesh, tai green
                if (!dfsMode && currentNode != null && currentNode != node) {
                    setFill(currentNode, SETTLED_COLOR);
                }
                currentNode = node;
                visitedNodes.add(node);
                setFill(node, CURRENT_COLOR);
            }
            case MOVE -> {
                // DFS-e target ekhoni visit hobe (amber), BFS-e shudhu discover hoyeche (halka pink)
                if (!settled.contains(event.getIndexB()) && !visitedNodes.contains(event.getIndexB())) {
                    setFill(event.getIndexB(), dfsMode ? CURRENT_COLOR : FOUND_COLOR);
                }
                String key = edgeKey(event.getIndexA(), event.getIndexB());
                Line line = edgeLines.get(key);
                if (line != null) {
                    line.setStroke(FORWARD_COLOR);
                    line.setStrokeWidth(4);
                    traversed.add(key);
                }
                drawArrow(event.getIndexA(), event.getIndexB(), FORWARD_COLOR);
            }
            case BACKTRACK -> {
                // DFS: ei node-er kaj shesh, green. Parent abar "current".
                setFill(event.getIndexA(), SETTLED_COLOR);
                setFill(event.getIndexB(), CURRENT_COLOR);
                currentNode = event.getIndexB();
                drawArrow(event.getIndexA(), event.getIndexB(), BACKTRACK_COLOR);
            }

            // ---------- Dijkstra ----------
            case SETTLE -> {
                int node = event.getIndexA();
                if (currentNode != null) setFill(currentNode, SETTLED_COLOR);
                currentNode = node;
                settled.add(node);
                setFill(node, CURRENT_COLOR);
                setDist(node, event.getValue());
            }
            case RELAX -> {
                String key = edgeKey(event.getIndexA(), event.getIndexB());
                Line line = edgeLines.get(key);
                if (line != null) {
                    line.setStroke(LOOKING_COLOR);
                    line.setStrokeWidth(4.5);
                    lastLooked = key;
                }
            }
            case IMPROVE -> {
                int node = event.getIndexA();
                int from = event.getIndexB();
                setDist(node, event.getValue());
                if (!settled.contains(node)) setFill(node, FOUND_COLOR);

                if (from >= 0) {
                    parentOf.put(node, from);
                    Polygon old = pathArrows.remove(node);
                    if (old != null) pane.getChildren().remove(old);
                    Polygon arrow = drawArrow(from, node, FORWARD_COLOR);
                    if (arrow != null) pathArrows.put(node, arrow);
                }
            }
            case COMPLETE -> {
                // Shob visited / settled node green
                for (int node : visitedNodes) setFill(node, SETTLED_COLOR);
                for (int node : settled) setFill(node, SETTLED_COLOR);

                // Dijkstra: shortest path-er line ar arrow shada
                for (Map.Entry<Integer, Integer> en : parentOf.entrySet()) {
                    Line line = edgeLines.get(edgeKey(en.getKey(), en.getValue()));
                    if (line != null) {
                        line.setStroke(PATH_COLOR);
                        line.setStrokeWidth(5);
                    }
                    Polygon a = pathArrows.get(en.getKey());
                    if (a != null) a.setFill(PATH_COLOR);
                }
            }
            default -> {}
        }
    }

    private void setFill(int node, Color color) {
        Circle c = nodeCircles.get(node);
        if (c != null) c.setFill(color);
    }

    private void setDist(int node, int value) {
        Label l = distLabels.get(node);
        if (l != null) l.setText("d=" + value);
    }

    // Dijkstra-te je edge ta check hocche, porer step-e sheta ager obosthay phire jay.
    private void clearLooking() {
        if (lastLooked == null) return;
        Line line = edgeLines.get(lastLooked);
        if (line != null) {
            boolean done = traversed.contains(lastLooked);
            line.setStroke(done ? FORWARD_COLOR : LINE_COLOR);
            line.setStrokeWidth(done ? 4 : 2.5);
        }
        lastLooked = null;
    }

    // Arrow line-er PASHE, line-er thik majhkhane boshe. "from" theke "to"-r dike
    // jawar somoy sobsomoy ekই pashe offset hoy, tai ulto direction-er arrow
    // (backtrack) automatically line-er onno pashe porbe.
    private Polygon drawArrow(int fromNode, int toNode, Color color) {
        double[] from = positions.get(fromNode);
        double[] to = positions.get(toNode);
        if (from == null || to == null) return null;

        double dx = to[0] - from[0];
        double dy = to[1] - from[1];
        double len = Math.max(0.01, Math.sqrt(dx * dx + dy * dy));
        double ux = dx / len;
        double uy = dy / len;
        double angle = Math.toDegrees(Math.atan2(dy, dx));

        double offset = weighted ? ARROW_OFFSET_WEIGHTED : ARROW_OFFSET;
        double cx = from[0] + dx * ARROW_POS - uy * offset;
        double cy = from[1] + dy * ARROW_POS + ux * offset;

        Polygon arrow = new Polygon(5.0, 0.0, -5.0, -4.5, -5.0, 4.5);
        arrow.setFill(color);
        arrow.setStroke(Color.web("#461D3A"));
        arrow.setStrokeWidth(0.8);
        arrow.setTranslateX(cx);
        arrow.setTranslateY(cy);
        arrow.setRotate(angle);
        pane.getChildren().add(arrow);
        return arrow;
    }

    private String edgeKey(int a, int b) {
        return Math.min(a, b) + "-" + Math.max(a, b);
    }
}