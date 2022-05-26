package it.polimi.ingsw.client.gui.controllers;

/**
 * This class is the controller of the lobby view
 */
public interface LobbyController extends GUIController{

    /**
     * Activates the start button
     */
    void setCanStart();

    /**
     * Deactivates the start button
     */
    void setCantStart();

}
