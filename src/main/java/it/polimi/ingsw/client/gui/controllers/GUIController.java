package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GUI;
import javafx.scene.layout.Pane;
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
     * @param root the parent of the controller.
     */
    void setRootPane(Pane root);

    /**
     * This method returns the node of the controller.
     *
     * @return the node of the controller.
     */
    Pane getRootPane();

    /**
     * This method is used to load the scene of the controller on the stage.
     *
     * @param stage the stage to load the scene on.
     */
    default void loadScene(Stage stage) {

    }

    /**
     * This method is used to show a new stage that disables the current one until it is closed.
     *
     * @param stage the stage to show.
     */
    default void showNewDisablingStage(Stage stage) {
        getRootPane().setDisable(true);
        stage.setOnHiding(event -> getRootPane().setDisable(false));
        stage.show();
    }
}
