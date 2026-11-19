package org.example.algorithms;

import org.example.model.AlgorithmEvent;
import java.util.List;

// --- OOP Concept: ABSTRACTION ---

public interface Algorithm {

    List<AlgorithmEvent> execute();

    String getName();
}