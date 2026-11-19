package org.example.algorithms;

import org.example.model.AlgorithmEvent;
import org.example.model.EventType;
import java.util.ArrayList;
import java.util.List;

public class QuickSort implements Algorithm {

    private final int[] data;
    private final List<AlgorithmEvent> events = new ArrayList<>();

    public QuickSort(int[] data) {
        this.data = data;
    }

    @Override
    public List<AlgorithmEvent> execute() {
        quickSort(0, data.length - 1);
        events.add(new AlgorithmEvent(EventType.COMPLETE, -1, -1, -1));
        return events;
    }

    // Recursive helper method — this is how Quick Sort naturally works
    private void quickSort(int low, int high) {
        if (low < high) {
            int pivotIndex = partition(low, high);
            // Pivot ekhon final position-e
            events.add(new AlgorithmEvent(EventType.SORTED, pivotIndex, pivotIndex, data[pivotIndex]));
            quickSort(low, pivotIndex - 1);
            quickSort(pivotIndex + 1, high);
        } else if (low == high) {
            // Ekta-i element baki, oita-o final
            events.add(new AlgorithmEvent(EventType.SORTED, low, low, data[low]));
        }
    }

    private int partition(int low, int high) {
        int pivot = data[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            events.add(new AlgorithmEvent(EventType.COMPARE, j, high, data[j]));

            if (data[j] < pivot) {
                i++;
                swap(i, j);
            }
        }
        swap(i + 1, high);
        return i + 1;
    }

    private void swap(int i, int j) {
        if (i == j) return;
        int temp = data[i];
        data[i] = data[j];
        data[j] = temp;
        events.add(new AlgorithmEvent(EventType.SWAP, i, j, data[i]));
    }

    @Override
    public String getName() {
        return "Quick Sort";
    }
}