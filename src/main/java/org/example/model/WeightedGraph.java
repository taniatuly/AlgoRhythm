package org.example.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeightedGraph {

    // Each node maps to a list of Edge objects (neighbor + weight)
    private final Map<Integer, List<Edge>> adjacency = new HashMap<>();

    public void addNode(int node) {
        adjacency.putIfAbsent(node, new ArrayList<>());
    }

    public void addEdge(int nodeA, int nodeB, int weight) {
        addNode(nodeA);
        addNode(nodeB);
        adjacency.get(nodeA).add(new Edge(nodeB, weight));
        adjacency.get(nodeB).add(new Edge(nodeA, weight));
    }

    public List<Edge> getEdges(int node) {
        return adjacency.getOrDefault(node, new ArrayList<>());
    }

    public List<Integer> getAllNodes() {
        return new ArrayList<>(adjacency.keySet());
    }

    // --- OOP Concept: a small inner class representing one edge ---
    public static class Edge {
        public final int target;
        public final int weight;

        public Edge(int target, int weight) {
            this.target = target;
            this.weight = weight;
        }
    }
}