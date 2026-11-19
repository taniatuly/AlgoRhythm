package org.example;

import org.example.algorithms.Algorithm;
import org.example.algorithms.BFS;
import org.example.algorithms.BubbleSort;
import org.example.algorithms.LinearSearch;
import org.example.events.EventManager;
import org.example.model.Graph;

public class TestRunner {
    public static void main(String[] args) {

        EventManager eventManager = new EventManager();
        eventManager.addListener(event -> System.out.println(event));

        System.out.println("=== Bubble Sort ===");
        Algorithm bubbleSort = new BubbleSort(new int[]{5, 2, 8, 1, 6});
        eventManager.publishAll(bubbleSort.execute());

        System.out.println("\n=== Linear Search ===");
        Algorithm linearSearch = new LinearSearch(new int[]{5, 2, 8, 1, 6}, 8);
        eventManager.publishAll(linearSearch.execute());

        System.out.println("\n=== BFS ===");
        Graph graph = new Graph();
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);
        Algorithm bfs = new BFS(graph, 1);
        eventManager.publishAll(bfs.execute());
    }
}