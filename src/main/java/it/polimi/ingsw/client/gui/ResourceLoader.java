package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.enums.FXMLPath;
import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.gui.controllers.GUIController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;

public abstract class ResourceLoader {

    private static final EnumMap<ImagePath, Image> images = new EnumMap<>(ImagePath.class);

    /**
     * Checks if all the resources can be loaded.
     *
     * @throws MissingResourceException if a resource is missing
     */
    public static void checkResources() throws MissingResourceException {
        checkFXML();
        checkImages();
        //TODO: check audio
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
     * Loads a FXML file.
     *
     * @param path the path of the FXML file
     * @return the controller of the FXML.
     */
    public static GUIController loadFXML(FXMLPath path, GUI gui) {
        try {
            FXMLLoader loader = new FXMLLoader(ResourceLoader.class.getResource(path.getPath()));
            Parent parent = loader.load();
            GUIController controller = loader.getController();
            controller.setParent(parent);
            controller.setGUI(gui);
            return controller;
        } catch (IOException | NullPointerException e) {
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
}
