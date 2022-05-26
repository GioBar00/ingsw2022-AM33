package it.polimi.ingsw.client.gui.controllers;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class is the controller of the lobby view
 */
public interface LobbyController extends GUIController {

    /**
     * Activates the start button
     */
    void setCanStart();

    /**
     * Deactivates the start button
     */
    void setCantStart();

    /**
     * @return if the lobby can handle teams.
     */
    default boolean canHandleTeams() {
        return false;
    }

    /**
     * This method is used to load the scene of the controller on the stage.
     *
     * @param stage the stage to load the scene on.
     */
    @Override
    default void loadScene(Stage stage) {
        stage.setScene(new Scene(getParent()));
        stage.setResizable(true);
        stage.setMinHeight(500.0);
        stage.setMinWidth(680.0);
        stage.setHeight(500.0);
        stage.setWidth(680.0);
        stage.setResizable(false);
    }
}
