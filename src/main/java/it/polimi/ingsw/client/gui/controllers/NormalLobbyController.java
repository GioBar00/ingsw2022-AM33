package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.GUIUtils;
import it.polimi.ingsw.client.gui.ResourceLoader;
import it.polimi.ingsw.network.messages.client.StartGame;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;


public class NormalLobbyController implements LobbyController {

    private GUI gui;

    private Pane root;

    @FXML
    private Button startBtn;

    @FXML
    public ImageView startImg;


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
        GUIUtils.changeImageHoverButton(startBtn, startImg, ResourceLoader.loadImage(ImagePath.START), ResourceLoader.loadImage(ImagePath.START_HIGHLIGHTED));
        GUIUtils.hideButton(startBtn);
    }

    /**
     * This method is used to set the parent of the controller.
     *
     * @param root the parent of the controller.
     */
    @Override
    public void setRootPane(Pane root) {
        this.root = root;
    }

    /**
     * This method returns the node of the controller.
     *
     * @return the node of the controller.
     */
    @Override
    public Pane getRootPane() {
        return root;
    }

    /**
     * Activates the start button
     */
    @Override
    public void setCanStart() {
        GUIUtils.showButton(startBtn);
    }

    /**
     * Deactivates the start button
     */
    @Override
    public void setCantStart() {
        GUIUtils.hideButton(startBtn);
    }

    /**
     * This method sends a start game request to the server
     */
    public void sendStart(){
        gui.notifyViewListener(new StartGame());
        GUIUtils.hideButton(startBtn);
    }
}
