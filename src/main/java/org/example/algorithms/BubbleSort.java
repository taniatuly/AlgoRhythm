package org.example.algorithms;

import org.example.model.AlgorithmEvent;
import org.example.model.EventType;
import java.util.ArrayList;
import java.util.List;

// --- OOP Concept: POLYMORPHISM + INHERITANCE-type relationship ---

public class BubbleSort implements Algorithm {

    // --- OOP Concept: ENCAPSULATION ---

    private final int[] data;

    public BubbleSort(int[] data) {
        this.data = data;
    }

    @Override
    public List<AlgorithmEvent> execute() {
        List<AlgorithmEvent> events = new ArrayList<>();
        int n = data.length;

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {

                events.add(new AlgorithmEvent(EventType.COMPARE, j, j + 1, data[j]));

                if (data[j] > data[j + 1]) {
                    int temp = data[j];
                    data[j] = data[j + 1];
                    data[j + 1] = temp;

                    // Swap howar por notun event create
                    events.add(new AlgorithmEvent(EventType.SWAP, j, j + 1, data[j]));
                }
            }

            events.add(new AlgorithmEvent(EventType.SORTED, n - i - 1, n - i - 1, data[n - i - 1]));
        }

        if (n > 0) {
            events.add(new AlgorithmEvent(EventType.SORTED, 0, 0, data[0]));
        }
        events.add(new AlgorithmEvent(EventType.COMPLETE, -1, -1, -1));
        return events;
    }

    @Override
    public String getName() {
        return "Bubble Sort";
    }
}