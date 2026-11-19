package org.example.music;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AmbientPlayer {

    private static MediaPlayer player;

    public static void start() {
        if (player != null) {
            if (player.getStatus() != MediaPlayer.Status.PLAYING) {
                player.play();
            }
            return;
        }

        try {
            Media media = new Media(AmbientPlayer.class.getResource("/audio/ambient.mp3").toExternalForm());
            player = new MediaPlayer(media);
            player.setCycleCount(MediaPlayer.INDEFINITE); // loops forever, no gaps
            player.setVolume(0.25); // real volume control — raise this (max 1.0) if still too quiet
            player.play();
        } catch (Exception e) {
            System.out.println("Could not load ambient audio: " + e.getMessage());
        }
    }

    public static void stop() {
        if (player != null) {
            player.pause();
        }
    }
}