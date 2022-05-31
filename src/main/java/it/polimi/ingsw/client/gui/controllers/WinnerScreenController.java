package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.GUIUtils;
import it.polimi.ingsw.client.gui.ResourceLoader;
import it.polimi.ingsw.client.gui.audio.MuteToggle;
import it.polimi.ingsw.network.messages.views.GameView;
import it.polimi.ingsw.network.messages.views.PlayerView;
import it.polimi.ingsw.server.model.enums.Tower;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.EnumSet;
import java.util.List;

public class WinnerScreenController implements GUIController, MuteToggle {
    @FXML
    public Label lblResult;
    @FXML
    public Label lblWinner;
    @FXML
    public Button btnReload;
    @FXML
    public Button btnMute;
    @FXML
    public ImageView imgViewMute;
    @FXML
    public ImageView imgViewReload;
    @FXML
    public ImageView imgViewBackground;

    private GUI gui;

    private Pane root;


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
        root.heightProperty().addListener((observable, oldValue, newValue) -> imgViewBackground.setFitHeight(newValue.doubleValue()));
        GUIUtils.changeImageHoverButton(btnReload, imgViewReload, ResourceLoader.loadImage(ImagePath.RELOAD), ResourceLoader.loadImage(ImagePath.RELOAD_HIGHLIGHTED));
        updateImageViewMute(imgViewMute);
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
     * This method is used to load the scene of the controller on the stage.
     *
     * @param stage the stage to load the scene on.
     */
    @Override
    public void loadScene(Stage stage) {
        if (stage.getScene() != null)
            stage.getScene().setRoot(new Region());
        stage.setScene(new Scene(root));
        stage.setMinHeight(800.0);
        stage.setMinWidth(1200.0);
        stage.setHeight(800.0);
        stage.setWidth(1200.0);
        stage.setResizable(false);
    }

    /**
     * This method is used to set the winner/s of the game.
     *
     * @param gameView the game view of the game.
     */
    public void updateGameView(GameView gameView) {
        EnumSet<Tower> winners = gameView.getWinners();
        if (winners.stream().findAny().isPresent()) {
            List<String> players = gameView.getPlayersView().stream().filter(p -> winners.contains(p.getSchoolBoardView().getTower())).map(PlayerView::getNickname).toList();

            if (winners.size() > 1)
                lblResult.setText("Draw!");
            else if (players.contains(gui.getNickname()))
                lblResult.setText("You won!");
            else
                lblResult.setText(winners.stream().findAny().get().toString().toLowerCase() + " won!");

            String sb = "Congratulations to " +
                    String.join(", ", players) +
                    "!";
            lblWinner.setText(sb);
        }
    }

    /**
     * This method is used to restart the game.
     */
    @FXML
    public void handleReloadButton() {
        gui.showStartScreen();
    }

    /**
     * handles the mute toggle.
     */
    @FXML
    @Override
    public void handleMuteButton() {
        toggleMute(imgViewMute);
    }
}
