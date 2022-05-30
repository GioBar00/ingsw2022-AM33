package it.polimi.ingsw.client.gui.audio;


import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.gui.ResourceLoader;
import javafx.scene.image.ImageView;

/**
 * This interface handles the mute toggle.
 */
public interface MuteToggle {
    /**
     * Toggle the mute state and change the image of the mute button.
     */
    default void toggleMute(ImageView imgView) {
        AudioManager.setMute(!AudioManager.getMuted());
        imgView.setImage(AudioManager.getMuted() ?
                ResourceLoader.loadImage(ImagePath.MUTE) :
                ResourceLoader.loadImage(ImagePath.VOLUME));
    }

    /**
     * handles the mute toggle.
     */
    void handleMuteButton();
}
