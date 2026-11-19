package org.example.algorithms;

import org.example.model.AlgorithmEvent;
import org.example.model.EventType;
import java.util.ArrayList;
import java.util.List;

// Important: Binary Search only works correctly on a SORTED array.
// The array passed to this class should already be sorted.
public class BinarySearch implements Algorithm {

    private final int[] data;
    private final int target;

    public BinarySearch(int[] data, int target) {
        this.data = data;
        this.target = target;
    }

    @Override
    public List<AlgorithmEvent> execute() {
        List<AlgorithmEvent> events = new ArrayList<>();
        int low = 0;
        int high = data.length - 1;

        while (low <= high) {
            int mid = (low + high) / 2;

            events.add(new AlgorithmEvent(EventType.COMPARE, mid, -1, data[mid]));

            if (data[mid] == target) {
                events.add(new AlgorithmEvent(EventType.FOUND, mid, -1, data[mid]));
                events.add(new AlgorithmEvent(EventType.COMPLETE, -1, -1, -1));
                return events;
            } else if (data[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        events.add(new AlgorithmEvent(EventType.COMPLETE, -1, -1, -1));
        return events;
    }

    @Override
    public String getName() {
        return "Binary Search";
    }
}