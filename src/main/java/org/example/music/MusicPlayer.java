package org.example.music;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

import org.example.events.EventListener;
import org.example.model.AlgorithmEvent;

public class MusicPlayer implements EventListener {

    private static final int[] SCALE = {48, 50, 52, 55, 57, 60, 62, 64};

    // Green (sorted) howar somoy shob bar-er jonno ek-i high "ting" note
    private static final int SORTED_NOTE = 81;

    private final MidiManager midiManager;
    private int lastNote = -1;

    public MusicPlayer(MidiManager midiManager) {
        this.midiManager = midiManager;
    }

    @Override
    public void onEvent(AlgorithmEvent event) {
        int note = mapEventToNote(event);
        if (note == -1) return;

        if (lastNote != -1) midiManager.stopNote(lastNote);

        midiManager.playNote(note, 95); // louder velocity
        lastNote = note;

        PauseTransition pause = new PauseTransition(Duration.millis(450));
        pause.setOnFinished(e -> midiManager.stopNote(note));
        pause.play();
    }

    private int mapEventToNote(AlgorithmEvent event) {
        return switch (event.getType()) {
            case COMPARE -> SCALE[1];
            case SWAP -> SCALE[3];
            case VISIT -> SCALE[2];
            case FOUND -> SCALE[6];
            case MOVE -> SCALE[4];
            case BACKTRACK -> SCALE[0];   // DFS-e fire asha: shobcheye nichu note
            case UPDATE -> SCALE[3];
            case RELAX -> SCALE[1];       // Dijkstra: edge check
            case IMPROVE -> SCALE[4];     // Dijkstra: notun chhoto path
            case SETTLE -> SCALE[5];      // Dijkstra: node final
            case SORTED -> SORTED_NOTE;
            case COMPLETE -> SCALE[7];
            default -> -1;
        };
    }
}