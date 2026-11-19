package org.example.algorithms;

import org.example.model.AlgorithmEvent;
import org.example.model.EventType;

import java.util.ArrayList;
import java.util.List;

public class LinearSearch implements Algorithm {

    private final int[] data;
    private final int target;

    public LinearSearch(int[] data, int target) {
        this.data = data;
        this.target = target;
    }

    @Override
    public List<AlgorithmEvent> execute() {
        List<AlgorithmEvent> events = new ArrayList<>();

        for (int i = 0; i < data.length; i++) {
            events.add(new AlgorithmEvent(EventType.VISIT, i, -1, data[i]));

            if (data[i] == target) {
                events.add(new AlgorithmEvent(EventType.FOUND, i, -1, data[i]));
                events.add(new AlgorithmEvent(EventType.COMPLETE, -1, -1, -1));
                return events;
            }
        }

        events.add(new AlgorithmEvent(EventType.COMPLETE, -1, -1, -1));
        return events;
    }

    @Override
    public String getName() {
        return "Linear Search";
    }
}