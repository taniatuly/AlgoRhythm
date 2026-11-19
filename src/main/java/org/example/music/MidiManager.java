package org.example.music;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

public class MidiManager {

    private Synthesizer synthesizer;
    private MidiChannel channel;

    public MidiManager() {
        try {
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            channel = synthesizer.getChannels()[0];

            // Program 10 = Music Box — a standard, universally
            // supported General MIDI instrument. Bright, sweet,
            // bell-like — much cuter than the earlier experimental ones.
            channel.programChange(10);
            channel.controlChange(7, 115);
        } catch (MidiUnavailableException e) {
            System.out.println("MIDI could not start: " + e.getMessage());
        }
    }

    public void playNote(int note, int velocity) {
        if (channel != null) channel.noteOn(note, velocity);
    }

    public void stopNote(int note) {
        if (channel != null) channel.noteOff(note);
    }

    public void close() {
        if (synthesizer != null) synthesizer.close();
    }
}