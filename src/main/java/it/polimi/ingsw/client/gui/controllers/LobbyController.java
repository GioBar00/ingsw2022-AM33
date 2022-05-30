package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.audio.MuteToggle;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * This class is the controller of the lobby view
 */
public interface LobbyController extends GUIController, MuteToggle {

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
        stage.getScene().setRoot(new Region());
        stage.setScene(new Scene(getRootPane()));
        stage.setHeight(500.0);
        stage.setWidth(680.0);
        stage.setResizable(false);
    }
}
