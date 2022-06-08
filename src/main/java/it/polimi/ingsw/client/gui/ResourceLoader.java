package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.enums.AudioPath;
import it.polimi.ingsw.client.enums.FXMLPath;
import it.polimi.ingsw.client.enums.FontPath;
import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.gui.controllers.GUIController;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.EnumMap;
import java.util.MissingResourceException;
import java.util.Objects;

/**
 * This class is used to load the resources needed by the GUI.
 */
public abstract class ResourceLoader {

    /**
     * This map contains the images loaded by the ResourceLoader.
     */
    private static final EnumMap<ImagePath, Image> images = new EnumMap<>(ImagePath.class);

    /**
     * This map contains the media player loaded by the ResourceLoader.
     */
    private static final EnumMap<AudioPath, MediaPlayer> audios = new EnumMap<>(AudioPath.class);

    /**
     * Checks if all the resources can be loaded.
     *
     * @throws MissingResourceException if a resource is missing
     */
    public static void checkResources() throws MissingResourceException {
        checkFXML();
        checkImages();
        checkFonts();
        checkAudio();
    }

    /**
     * Checks if all the FXML resources can be loaded.
     *
     * @throws MissingResourceException if a resource is missing
     */
    private static void checkFXML() throws MissingResourceException {
        for (FXMLPath path : FXMLPath.values()) {
            if (ResourceLoader.class.getResource(path.getPath()) == null)
                throw new MissingResourceException("FXML file not found: " + path.getPath(), ResourceLoader.class.getName(), path.getPath());
        }
    }

    /**
     * Checks if all the images can be loaded.
     *
     * @throws MissingResourceException if a resource is missing
     */
    private static void checkImages() throws MissingResourceException {
        for (ImagePath path : ImagePath.values()) {
            if (ResourceLoader.class.getResource(path.getPath()) == null)
                throw new MissingResourceException("Image file not found: " + path.getPath(), ResourceLoader.class.getName(), path.getPath());
        }
    }

    /**
     * Checks if all the fonts can be loaded and loads them.
     *
     * @throws MissingResourceException if a resource is missing
     */
    private static void checkFonts() throws MissingResourceException {
        for (FontPath path : FontPath.values()) {
            if (ResourceLoader.class.getResource(path.getPath()) == null)
                throw new MissingResourceException("Font file not found: " + path.getPath(), ResourceLoader.class.getName(), path.getPath());
            else
                Font.loadFont(Objects.requireNonNull(ResourceLoader.class.getResource(path.getPath())).toExternalForm(), 10);
        }
    }

    /**
     * Checks if all audio files can be loaded.
     *
     * @throws MissingResourceException if a resource is missing
     */
    private static void checkAudio() throws MissingResourceException {
        for (AudioPath path : AudioPath.values()) {
            if (ResourceLoader.class.getResource(path.getPath()) == null)
                throw new MissingResourceException("Audio file not found: " + path.getPath(), ResourceLoader.class.getName(), path.getPath());
        }
    }

    /**
     * Loads a FXML file.
     *
     * @param path the path of the FXML file
     * @return the controller of the FXML.
     */
    public static <T extends GUIController> T loadFXML(FXMLPath path, GUI gui) {
        try {
            FXMLLoader loader = new FXMLLoader(ResourceLoader.class.getResource(path.getPath()));
            Pane root = loader.load();
            T controller = loader.getController();
            controller.setRootPane(root);
            controller.setGUI(gui);
            return controller;
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            System.err.println("Error loading fxml: " + path.getPath());
            throw new MissingResourceException("FXML file not found: " + path.getPath(), ResourceLoader.class.getName(), path.getPath());
        }
    }

    /**
     * Loads an image if it is not already loaded.
     *
     * @param path the path of the image
     * @return the image
     */
    public static Image loadImage(ImagePath path) {
        if (images.containsKey(path))
            return images.get(path);
        try {
            Image image = new Image(Objects.requireNonNull(ResourceLoader.class.getResource(path.getPath())).toExternalForm());
            images.put(path, image);
            return image;
        } catch (NullPointerException e) {
            System.err.println("Error loading image: " + path.getPath());
            throw new MissingResourceException("Image file not found: " + path.getPath(), ResourceLoader.class.getName(), path.getPath());
        }
    }

    /**
     * Loads an audio file if it is not already loaded.
     *
     * @param path the path of the audio file
     * @return the media player
     */
    public static MediaPlayer loadAudio(AudioPath path) {
        try {
            MediaPlayer player;
            if (!audios.containsKey(path)) {
                player = new MediaPlayer(new Media(Objects.requireNonNull(ResourceLoader.class.getResource(path.getPath())).toExternalForm()));
                audios.put(path, player);
            } else
                player = audios.get(path);
            player.seek(player.getStartTime());
            return player;
        } catch (NullPointerException e) {
            System.err.println("Error loading audio: " + path.getPath());
            throw new MissingResourceException("Audio file not found: " + path.getPath(), ResourceLoader.class.getName(), path.getPath());
        }
    }
}
