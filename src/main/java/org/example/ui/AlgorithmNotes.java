package org.example.ui;

import java.util.Map;

public class AlgorithmNotes {

    private static final Map<String, String> NOTES = Map.ofEntries(
            Map.entry("Bubble Sort", "Repeatedly steps through the list, comparing each pair of neighboring elements and swapping them if they're in the wrong order. This continues, pass after pass, until no more swaps are needed. It's simple to understand and code, but inefficient for large datasets since it has a worst-case and average time complexity of O(n²)."),
            Map.entry("Selection Sort", "Divides the list into a sorted and unsorted part. On each pass, it scans the unsorted part to find the smallest remaining value, then swaps it into place at the front of the unsorted section. It always performs the same number of comparisons regardless of input, giving it O(n²) time complexity but relatively few swaps."),
            Map.entry("Insertion Sort", "Builds the final sorted list one element at a time. It takes each new element and inserts it into its correct position among the already-sorted elements before it, shifting larger values aside. It performs very well on small or nearly-sorted datasets, running close to O(n) in the best case."),
            Map.entry("Quick Sort", "A divide-and-conquer algorithm that picks a 'pivot' element, then partitions the rest of the list into values smaller and larger than the pivot. It recursively applies the same process to each partition. On average it runs in O(n log n), making it one of the fastest general-purpose sorting algorithms in practice."),
            Map.entry("Merge Sort", "Another divide-and-conquer algorithm. It repeatedly splits the list in half until each piece has just one element, then merges those pieces back together in sorted order. It guarantees O(n log n) performance every time, making it very predictable and stable, though it needs extra memory for the merge step."),
            Map.entry("Linear Search", "Checks each element in the list one at a time, in order, comparing it to the target value until a match is found or the list ends. It doesn't require the data to be sorted, but in the worst case it has to check every single element, giving it O(n) time complexity."),
            Map.entry("Binary Search", "Works only on a SORTED list. It repeatedly checks the middle element and compares it to the target — if the target is smaller, it searches the left half; if larger, the right half. By cutting the search space in half each time, it achieves a much faster O(log n) time complexity."),
            Map.entry("BFS", "Breadth-First Search explores a graph level by level, visiting all of a node's direct neighbors before moving on to their neighbors. It uses a Queue (first-in, first-out) to keep track of which nodes to visit next, which naturally produces this layer-by-layer exploration pattern."),
            Map.entry("DFS", "Depth-First Search explores a graph by going as deep as possible down one path before backtracking to try another. It's naturally implemented using recursion, where the call stack itself keeps track of the path taken, as opposed to BFS's explicit Queue."),
            Map.entry("Dijkstra", "Finds the shortest path from a starting node to every other node in a weighted graph. It works greedily — at each step it picks the closest unvisited node, updates the distances to its neighbors if a shorter path is found, and repeats until every reachable node has been visited."),
            Map.entry("Fibonacci", "Generates a sequence where each number is the sum of the two numbers before it (0, 1, 1, 2, 3, 5, 8...). This version uses Dynamic Programming — storing each calculated value in an array — so no value is ever recalculated, making it far faster than naive recursion.")
    );

    public static String get(String algorithmName) {
        return NOTES.getOrDefault(algorithmName, "No notes available.");
    }
}