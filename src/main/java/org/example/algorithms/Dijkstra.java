package org.example.algorithms;

import org.example.model.AlgorithmEvent;
import org.example.model.EventType;
import org.example.model.WeightedGraph;
import java.util.*;

public class Dijkstra implements Algorithm {

    private final WeightedGraph graph;
    private final int startNode;

    public Dijkstra(WeightedGraph graph, int startNode) {
        this.graph = graph;
        this.startNode = startNode;
    }

    @Override
    public List<AlgorithmEvent> execute() {
        List<AlgorithmEvent> events = new ArrayList<>();
        Map<Integer, Integer> distance = new HashMap<>();
        Set<Integer> visited = new HashSet<>();

        for (int node : graph.getAllNodes()) {
            distance.put(node, Integer.MAX_VALUE);
        }
        distance.put(startNode, 0);

        // Start node distance 0
        events.add(new AlgorithmEvent(EventType.IMPROVE, startNode, -1, 0));

        // Queue {node, distance} pair
        PriorityQueue<int[]> pq = new PriorityQueue<>(
                Comparator.comparingInt((int[] a) -> a[1])
        );
        pq.add(new int[]{startNode, 0});

        while (!pq.isEmpty()) {
            int current = pq.poll()[0];
            if (visited.contains(current)) continue;
            visited.add(current);

            events.add(new AlgorithmEvent(EventType.SETTLE, current, -1, distance.get(current)));

            for (WeightedGraph.Edge edge : graph.getEdges(current)) {

                if (visited.contains(edge.target)) continue;

                int newDist = distance.get(current) + edge.weight;

                // Edge check
                events.add(new AlgorithmEvent(EventType.RELAX, current, edge.target, newDist));

                if (newDist < distance.get(edge.target)) {
                    distance.put(edge.target, newDist);
                    pq.add(new int[]{edge.target, newDist});

                    events.add(new AlgorithmEvent(EventType.IMPROVE, edge.target, current, newDist));
                }
            }
        }

        events.add(new AlgorithmEvent(EventType.COMPLETE, -1, -1, -1));
        return events;
    }

    @Override
    public String getName() {
        return "Dijkstra";
    }
}