package it.polimi.ingsw.client.gui.audio;


import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.gui.ResourceLoader;
import javafx.scene.image.ImageView;

/**
 * This interface handles the mute toggle.
 */
public interface MuteToggle {

    /**
     * Updates the mute image.
     *
     * @param imageView the image view.
     */
    default void updateImageViewMute(ImageView imageView) {
        imageView.setImage(AudioManager.getMuted() ?
                ResourceLoader.loadImage(ImagePath.MUTE) :
                ResourceLoader.loadImage(ImagePath.VOLUME));
    }

    /**
     * Toggle the mute state and change the image of the mute button.
     *
     * @param imgView current image of the button.
     */
    default void toggleMute(ImageView imgView) {
        AudioManager.setMute(!AudioManager.getMuted());
        updateImageViewMute(imgView);
    }

    /**
     * handles the mute toggle.
     */
    void handleMuteButton();
}
