package org.example.events;

import org.example.model.AlgorithmEvent;

// --- OOP Concept: ABSTRACTION ---
// Eita ekta "contract" — je kono class ei interface implement korle,
// take obosshoi "onEvent()" method likhte hobe. Eita bole na method-er
// bhitore ki hobe, shudhu bole method-ta thakte hobe.
public interface EventListener {
    void onEvent(AlgorithmEvent event);
}