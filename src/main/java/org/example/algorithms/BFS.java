package org.example.algorithms;

import org.example.model.AlgorithmEvent;
import org.example.model.EventType;
import org.example.model.Graph;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class BFS implements Algorithm {

    private final Graph graph;
    private final int startNode;

    public BFS(Graph graph, int startNode) {
        this.graph = graph;
        this.startNode = startNode;
    }

    @Override
    public List<AlgorithmEvent> execute() {
        List<AlgorithmEvent> events = new ArrayList<>();

        // "visited"
        Set<Integer> visited = new HashSet<>();

        // "queue" — BFS-er main data structure, FIFO (first in first out)
        Queue<Integer> queue = new ArrayDeque<>();

        queue.add(startNode);
        visited.add(startNode);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            events.add(new AlgorithmEvent(EventType.VISIT, current, -1, current));

            for (int neighbor : graph.getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    events.add(new AlgorithmEvent(EventType.MOVE, current, neighbor, neighbor));
                }
            }
        }

        events.add(new AlgorithmEvent(EventType.COMPLETE, -1, -1, -1));
        return events;
    }

    @Override
    public String getName() {
        return "BFS";
    }
}