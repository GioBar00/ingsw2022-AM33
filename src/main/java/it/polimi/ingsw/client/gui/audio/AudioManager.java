package it.polimi.ingsw.client.gui.audio;

import it.polimi.ingsw.client.enums.AudioPath;
import it.polimi.ingsw.client.gui.ResourceLoader;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * This class manages the audio of the game.
 */
public abstract class AudioManager {

    /**
     * if audio is muted
     */
    private static boolean isMuted = false;
    /**
     * the current media player
     */
    private static MediaPlayer currentPlayer;

    /**
     * @return if the audio is muted
     */
    public static boolean getMuted() {
        return isMuted;
    }

    /**
     * Sets the audio to be muted or not.
     *
     * @param mute true if the audio should be muted, false otherwise
     */
    public static void setMute(boolean mute) {
        isMuted = mute;
        if (currentPlayer != null) {
            currentPlayer.setMute(isMuted);
        }
    }

    /**
     * Substitutes the current player with the new one.
     *
     * @param audio the audio to play
     */
    public static void playAudio(AudioPath audio) {
        MediaPlayer newPlayer = ResourceLoader.loadAudio(audio);
        if (currentPlayer != newPlayer) {
            newPlayer.setMute(isMuted);
            newPlayer.setOnEndOfMedia(() -> {
                newPlayer.seek(newPlayer.getStartTime());
                newPlayer.play();
            });
            if (currentPlayer != null) {
                MediaPlayer oldPlayer = currentPlayer;
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.millis(500),
                                new KeyValue(oldPlayer.volumeProperty(), 0)));
                timeline.setOnFinished(event -> {
                    oldPlayer.stop();
                    oldPlayer.setVolume(1);
                    newPlayer.play();
                });
                timeline.play();
            } else
                newPlayer.play();
            currentPlayer = newPlayer;
        }
    }

    /**
     * Plays an audio effect on top of the current player.
     *
     * @param audio the audio to play
     */
    public static void playEffect(AudioPath audio) {
        MediaPlayer player = ResourceLoader.loadAudio(audio);
        player.setMute(isMuted);
        player.play();
    }
}
