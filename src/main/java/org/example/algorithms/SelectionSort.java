package org.example.algorithms;

import org.example.model.AlgorithmEvent;
import org.example.model.EventType;
import java.util.ArrayList;
import java.util.List;

public class SelectionSort implements Algorithm {

    private final int[] data;

    public SelectionSort(int[] data) {
        this.data = data;
    }

    @Override
    public List<AlgorithmEvent> execute() {
        List<AlgorithmEvent> events = new ArrayList<>();
        int n = data.length;

        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;

            for (int j = i + 1; j < n; j++) {
                events.add(new AlgorithmEvent(EventType.COMPARE, minIndex, j, data[j]));

                if (data[j] < data[minIndex]) {
                    minIndex = j;
                }
            }

            if (minIndex != i) {
                int temp = data[i];
                data[i] = data[minIndex];
                data[minIndex] = temp;

                events.add(new AlgorithmEvent(EventType.SWAP, i, minIndex, data[i]));
            }
            // Position i ekhon final
            events.add(new AlgorithmEvent(EventType.SORTED, i, i, data[i]));
        }

        if (n > 0) {
            events.add(new AlgorithmEvent(EventType.SORTED, n - 1, n - 1, data[n - 1]));
        }
        events.add(new AlgorithmEvent(EventType.COMPLETE, -1, -1, -1));
        return events;
    }

    @Override
    public String getName() {
        return "Selection Sort";
    }
}