package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.network.messages.client.StartGame;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;


public class NormalLobbyController implements GUIController, LobbyController {

    private GUI gui;

    private Parent root;

    @FXML
    private Button startBtn;


    /**
     * This method is used to set the GUI of the controller.
     *
     * @param gui the GUI of the controller.
     */
    @Override
    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    /**
     * This method is used to initialize the controller and stage.
     */
    @Override
    public void init() {
        startBtn.setVisible(false);
        startBtn.setDisable(true);
    }

    /**
     * This method is used to set the parent of the controller.
     *
     * @param parent the parent of the controller.
     */
    @Override
    public void setParent(Parent parent) {
        root = parent;
    }

    /**
     * This method returns the node of the controller.
     *
     * @return the node of the controller.
     */
    @Override
    public Parent getParent() {
        return root;
    }

    /**
     * Activates the start button
     */
    @Override
    public void setCanStart() {
        startBtn.setVisible(true);
        startBtn.setDisable(false);

    }

    /**
     * Deactivates the start button
     */
    @Override
    public void setCantStart() {
        startBtn.setVisible(false);
        startBtn.setDisable(true);
    }

    /**
     * This method sends a start game request to the server
     */
    public void sendStart(){
        gui.notifyViewListener(new StartGame());
    }
}
