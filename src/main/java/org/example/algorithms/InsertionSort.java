package org.example.algorithms;

import org.example.model.AlgorithmEvent;
import org.example.model.EventType;
import java.util.ArrayList;
import java.util.List;

public class InsertionSort implements Algorithm {

    private final int[] data;

    public InsertionSort(int[] data) {
        this.data = data;
    }

    @Override
    public List<AlgorithmEvent> execute() {
        List<AlgorithmEvent> events = new ArrayList<>();
        int n = data.length;

        for (int i = 1; i < n; i++) {
            int key = data[i];
            int j = i - 1;

            while (j >= 0) {
                events.add(new AlgorithmEvent(EventType.COMPARE, j, j + 1, data[j]));

                if (data[j] > key) {
                    data[j + 1] = data[j];
                    events.add(new AlgorithmEvent(EventType.SWAP, j, j + 1, data[j + 1]));
                    j--;
                } else {
                    break;
                }
            }
            data[j + 1] = key;
        }

        events.add(new AlgorithmEvent(EventType.SORTED, 0, n - 1, -1));
        events.add(new AlgorithmEvent(EventType.COMPLETE, -1, -1, -1));
        return events;
    }

    @Override
    public String getName() {
        return "Insertion Sort";
    }
}