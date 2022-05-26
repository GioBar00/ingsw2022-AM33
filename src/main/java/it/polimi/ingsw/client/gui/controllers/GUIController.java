package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GUI;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 * This class is a controller of the GUI.
 */
public interface GUIController {

    /**
     * This method is used to set the GUI of the controller.
     *
     * @param gui the GUI of the controller.
     */
    void setGUI(GUI gui);

    /**
     * This method is used to initialize the controller and stage.
     */
    void init();

    /**
     * This method is used to set the parent of the controller.
     *
     * @param parent the parent of the controller.
     */
    void setParent(Parent parent);

    /**
     * This method returns the node of the controller.
     *
     * @return the node of the controller.
     */
    Parent getParent();

    /**
     * This method is used to load the scene of the controller on the stage.
     *
     * @param stage the stage to load the scene on.
     */
    default void loadScene(Stage stage) {

    }
}
