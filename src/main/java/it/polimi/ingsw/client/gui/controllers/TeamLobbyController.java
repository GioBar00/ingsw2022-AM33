package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.enums.AudioPath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.GUIUtils;
import it.polimi.ingsw.client.gui.audio.AudioManager;
import it.polimi.ingsw.network.messages.client.ChosenTeam;
import it.polimi.ingsw.network.messages.client.StartGame;
import it.polimi.ingsw.network.messages.views.TeamsView;
import it.polimi.ingsw.server.model.enums.Tower;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

/**
 * This class is the controller of the team lobby screen.
 */
public class TeamLobbyController implements LobbyController {

    /**
     * {@link GUI} instance.
     */
    private GUI gui;

    /**
     * The root of the scene.
     */
    private Pane root;

    @FXML
    private Button blackBtn;

    @FXML
    private Button whiteBtn;

    @FXML
    private Button startButton;

    @FXML
    private Label firstBName;

    @FXML
    private Label secondBName;

    @FXML
    private Label thirdBName;

    @FXML
    private Label fourthBName;

    @FXML
    private Label firstWName;

    @FXML
    private Label secondWName;

    @FXML
    private Label thirdWName;

    @FXML
    private Label fourthWName;

    @FXML
    private Label firstLName;

    @FXML
    private Label secondLName;

    @FXML
    private Label thirdLName;

    @FXML
    private Label fourthLName;

    @FXML
    ImageView startImg;

    @FXML
    private ImageView imgViewBackground;
    @FXML
    private ImageView imgViewMute;
    @FXML
    private Rectangle rectWhite;
    @FXML
    private Rectangle rectLobby;
    @FXML
    private Rectangle rectBlack;
    @FXML
    private AnchorPane anchorPaneBlack;
    @FXML
    private AnchorPane anchorPaneLobby;
    @FXML
    private AnchorPane anchorPaneWhite;

    private final Label[] blackLabels;

    private final Label[] whiteLabels;

    private final Label[] lobbyLabels;

    /**
     * Public Constructor of the controller
     */
    public TeamLobbyController() {
        blackLabels = new Label[4];
        whiteLabels = new Label[4];
        lobbyLabels = new Label[4];

    }

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
        updateImageViewMute(imgViewMute);
        startButton.setVisible(false);
        startButton.setDisable(true);
        fillLabels();
        root.heightProperty().addListener((observable, oldValue, newValue) -> imgViewBackground.setFitHeight(newValue.doubleValue()));
        GUIUtils.bindSize(anchorPaneLobby, rectLobby);
        GUIUtils.bindSize(anchorPaneBlack, rectBlack);
        GUIUtils.bindSize(anchorPaneWhite, rectWhite);
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
     * This method sends a change team request to the server, the chosen team is the white one
     */
    @FXML
    public void sendWhiteTeam() {
        gui.notifyViewListener(new ChosenTeam(Tower.WHITE));
        GUIUtils.hideButton(whiteBtn);
    }

    /**
     * This method sends a change team request to the server, the chosen team is the black one
     */
    @FXML
    public void sendBlackTeam() {
        gui.notifyViewListener(new ChosenTeam(Tower.BLACK));
        GUIUtils.hideButton(blackBtn);
    }

    /**
     * This method sends a start game request to the server
     */
    @FXML
    public void sendStart() {
        gui.notifyViewListener(new StartGame());
        GUIUtils.hideButton(whiteBtn);
    }

    /**
     * Activates the start button
     */
    @Override
    public void setCanStart() {
        startButton.setVisible(true);
        startButton.setDisable(false);
        AudioManager.playEffect(AudioPath.START_EFFECT);
    }

    /**
     * Deactivates the start button
     */
    @Override
    public void setCantStart() {
        startButton.setVisible(false);
        startButton.setDisable(true);
    }

    /**
     * @return if the lobby can handle teams.
     */
    @Override
    public boolean canHandleTeams() {
        return true;
    }

    /**
     * Show the name of the players in the right team
     *
     * @param view a TeamView
     */
    public void updateTeams(TeamsView view, String nickname) {


        int i = 0;
        for (String s : view.getTeams().get(Tower.BLACK)) {
            blackLabels[i].setText(s);
            i++;
        }
        for (; i < 4; i++) {
            blackLabels[i].setText("");
        }

        i = 0;
        for (String s : view.getTeams().get(Tower.WHITE)) {
            whiteLabels[i].setText(s);
            i++;
        }

        for (; i < 4; i++) {
            whiteLabels[i].setText("");
        }

        i = 0;
        for (String s : view.getLobby()) {
            lobbyLabels[i].setText(s);
            i++;
        }
        for (; i < 4; i++) {
            lobbyLabels[i].setText("");
        }

        if (view.getTeams().get(Tower.BLACK).contains(nickname)) {
            GUIUtils.showButton(whiteBtn);
        } else if (view.getTeams().get(Tower.WHITE).contains(nickname)) {
            GUIUtils.showButton(blackBtn);
        } else {
            GUIUtils.showButton(blackBtn);
            GUIUtils.showButton(whiteBtn);
        }
    }


    /**
     * Fill the array with the corresponding label
     */
    private void fillLabels() {
        blackLabels[0] = firstBName;
        blackLabels[1] = secondBName;
        blackLabels[2] = thirdBName;
        blackLabels[3] = fourthBName;

        whiteLabels[0] = firstWName;
        whiteLabels[1] = secondWName;
        whiteLabels[2] = thirdWName;
        whiteLabels[3] = fourthWName;

        lobbyLabels[0] = firstLName;
        lobbyLabels[1] = secondLName;
        lobbyLabels[2] = thirdLName;
        lobbyLabels[3] = fourthLName;
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
