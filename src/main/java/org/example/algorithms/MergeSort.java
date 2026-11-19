package org.example.algorithms;

import org.example.model.AlgorithmEvent;
import org.example.model.EventType;
import java.util.ArrayList;
import java.util.List;

public class MergeSort implements Algorithm {

    private final int[] data;
    private final List<AlgorithmEvent> events = new ArrayList<>();

    public MergeSort(int[] data) {
        this.data = data;
    }

    @Override
    public List<AlgorithmEvent> execute() {
        mergeSort(0, data.length - 1);

        events.add(new AlgorithmEvent(EventType.SORTED, 0, data.length - 1, -1));
        events.add(new AlgorithmEvent(EventType.COMPLETE, -1, -1, -1));
        return events;
    }

    private void mergeSort(int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(left, mid);
            mergeSort(mid + 1, right);
            merge(left, mid, right);
        }
    }

    private void merge(int left, int mid, int right) {
        int[] temp = new int[right - left + 1];
        int i = left, j = mid + 1, k = 0;

        while (i <= mid && j <= right) {
            events.add(new AlgorithmEvent(EventType.COMPARE, i, j, data[i]));

            if (data[i] <= data[j]) {
                temp[k++] = data[i++];
            } else {
                temp[k++] = data[j++];
            }
        }

        while (i <= mid) temp[k++] = data[i++];
        while (j <= right) temp[k++] = data[j++];

        for (int m = 0; m < temp.length; m++) {
            data[left + m] = temp[m];
            events.add(new AlgorithmEvent(EventType.UPDATE, left + m, -1, temp[m]));
        }
    }

    @Override
    public String getName() {
        return "Merge Sort";
    }
}