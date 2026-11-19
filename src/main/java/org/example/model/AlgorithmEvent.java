package org.example.model;

// --- OOP Concept: ENCAPSULATION ---
// Shob field "private" kora ache, mane bairer kono class direct
// eigula change korte parbe na. Shudhu getter method diye value pawa jay.
public class AlgorithmEvent {

    private final EventType type;
    private final int indexA;
    private final int indexB;
    private final int value;

    public AlgorithmEvent(EventType type, int indexA, int indexB, int value) {
        this.type = type;
        this.indexA = indexA;
        this.indexB = indexB;
        this.value = value;
    }

    public EventType getType() {
        return type;
    }

    public int getIndexA() {
        return indexA;
    }

    public int getIndexB() {
        return indexB;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return type + " [a=" + indexA + ", b=" + indexB + ", value=" + value + "]";
    }
}