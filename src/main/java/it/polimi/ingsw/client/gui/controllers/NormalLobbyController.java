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

/**
 * This class is the controller of the lobby screen when the game isn't a team one.
 */
public class NormalLobbyController implements LobbyController {

    /**
     * {@link GUI} instance.
     */
    private GUI gui;

    /**
     * The root of the scene.
     */
    private Pane root;

    @FXML
    private Button startBtn;

    @FXML
    public ImageView startImg;

    @FXML
    public ImageView imageViewBackground;
    @FXML
    public ImageView imageViewMute;


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
        updateImageViewMute(imageViewMute);
        root.heightProperty().addListener((observable, oldValue, newValue) -> imageViewBackground.setFitHeight(newValue.doubleValue()));
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
    public void sendStart() {
        gui.notifyViewListener(new StartGame());
        GUIUtils.hideButton(startBtn);
    }

    /**
     * handles the mute toggle.
     */
    @FXML
    @Override
    public void handleMuteButton() {
        toggleMute(imageViewMute);
    }
}
