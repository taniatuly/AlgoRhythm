package org.example.events;

import org.example.model.AlgorithmEvent;

// --- OOP Concept: INTERFACE IMPLEMENTATION ---
// Ei class "EventListener" interface implement korche. Mane
// "ConsoleLogger" ekhon EventManager-er listener list-e add kora
// jabe, karon eta EventListener type-er shorto puron kore.
public class ConsoleLogger implements EventListener {

    @Override
    public void onEvent(AlgorithmEvent event) {
        System.out.println(event);
    }
}