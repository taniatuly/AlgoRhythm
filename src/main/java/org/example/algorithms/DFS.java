package org.example.algorithms;

import org.example.model.AlgorithmEvent;
import org.example.model.EventType;
import org.example.model.Graph;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DFS implements Algorithm {

    private final Graph graph;
    private final int startNode;
    private final List<AlgorithmEvent> events = new ArrayList<>();
    private final Set<Integer> visited = new HashSet<>();

    public DFS(Graph graph, int startNode) {
        this.graph = graph;
        this.startNode = startNode;
    }

    @Override
    public List<AlgorithmEvent> execute() {
        visit(startNode, -1);
        events.add(new AlgorithmEvent(EventType.COMPLETE, -1, -1, -1));
        return events;
    }

    private void visit(int node, int parent) {
        visited.add(node);
        events.add(new AlgorithmEvent(EventType.VISIT, node, -1, node));

        for (int neighbor : graph.getNeighbors(node)) {
            if (!visited.contains(neighbor)) {
                events.add(new AlgorithmEvent(EventType.MOVE, node, neighbor, neighbor));
                visit(neighbor, node);
            }
        }

        if (parent != -1) {
            events.add(new AlgorithmEvent(EventType.BACKTRACK, node, parent, parent));
        }
    }

    @Override
    public String getName() {
        return "DFS";
    }
}