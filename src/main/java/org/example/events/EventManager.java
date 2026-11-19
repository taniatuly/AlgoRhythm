package org.example.events;

import org.example.model.AlgorithmEvent;
import java.util.ArrayList;
import java.util.List;

// --- Design Pattern: OBSERVER PATTERN ---
// EventManager ekta "list" rakhe shob listener-er (jara event shunte
// chay). Jokhon kono notun event ashe, EventManager shei list-er
// SHOBAIKE event ta pathay — ei process-ke "publish" kora bola hoy.
public class EventManager {

    // --- OOP Concept: ENCAPSULATION ---
    // "listeners" list ta private, bairer theke direct access kora jay na.
    private final List<EventListener> listeners = new ArrayList<>();

    // Kono class (jemon Visualizer ba Logger) nijeke EventManager-er
    // "listener list"-e add korte chaile ei method call korbe.
    public void addListener(EventListener listener) {
        listeners.add(listener);
    }

    // Ekta single event, shob listener-ke pathano hocche.
    public void publish(AlgorithmEvent event) {
        for (EventListener listener : listeners) {
            listener.onEvent(event);
        }
    }

    // Pura event list ekbare publish korar jonno shortcut method.
    public void publishAll(List<AlgorithmEvent> events) {
        for (AlgorithmEvent event : events) {
            publish(event);
        }
    }
}