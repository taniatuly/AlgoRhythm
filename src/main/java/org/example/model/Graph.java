package org.example.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Graph ke "adjacency list" diye represent kora hoyeche.
// Map<Integer, List<Integer>> mane: protyek number (node) er sathe
// kon kon number (neighbor) connected ache, shei list.
public class Graph {

    private final Map<Integer, List<Integer>> adjacency = new HashMap<>();

    public void addNode(int node) {
        adjacency.putIfAbsent(node, new ArrayList<>());
    }

    public void addEdge(int nodeA, int nodeB) {
        addNode(nodeA);
        addNode(nodeB);
        adjacency.get(nodeA).add(nodeB);
        adjacency.get(nodeB).add(nodeA);
    }

    public List<Integer> getNeighbors(int node) {
        return adjacency.getOrDefault(node, new ArrayList<>());
    }
    public List<Integer> getAllNodes() {
        return new ArrayList<>(adjacency.keySet());
    }
}