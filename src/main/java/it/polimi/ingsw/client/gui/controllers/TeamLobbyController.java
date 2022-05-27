package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.network.messages.client.ChosenTeam;
import it.polimi.ingsw.network.messages.client.StartGame;
import it.polimi.ingsw.network.messages.views.TeamsView;
import it.polimi.ingsw.server.model.enums.Tower;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class TeamLobbyController implements LobbyController {

    private GUI gui;

    private Pane root;

    @FXML
    public Button startButton;

    @FXML
    public Label firstBName;

    @FXML
    public Label secondBName;

    @FXML
    public Label thirdBName;

    @FXML
    public Label fourthBName;

    @FXML
    public Label firstWName;

    @FXML
    public Label secondWName;

    @FXML
    public Label thirdWName;

    @FXML
    public Label fourthWName;

    @FXML
    public Label firstLName;

    @FXML
    public Label secondLName;

    @FXML
    public Label thirdLName;

    @FXML
    public Label fourthLName;

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
        startButton.setVisible(false);
        startButton.setDisable(true);
        fillLabels();
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
    }

    /**
     * This method sends a change team request to the server, the chosen team is the black one
     */
    @FXML
    public void sendBlackTeam() {
        gui.notifyViewListener(new ChosenTeam(Tower.BLACK));
    }

    /**
     * This method sends a start game request to the server
     */
    @FXML
    public void sendStart() {
        gui.notifyViewListener(new StartGame());
    }

    /**
     * Activates the start button
     */
    @Override
    public void setCanStart() {
        startButton.setVisible(true);
        startButton.setDisable(false);
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
    public void updateTeams(TeamsView view) {


        int i = 0;
        for (String s : view.getTeams().get(Tower.BLACK)) {
            blackLabels[i].setText(s);
            i++;
        }
        for(;i < 4; i++){
            blackLabels[i].setText("");
        }

        i = 0;
        for (String s : view.getTeams().get(Tower.WHITE)) {
            whiteLabels[i].setText(s);
            i++;
        }

        for(;i < 4; i++){
            whiteLabels[i].setText("");
        }

        i = 0;
        for (String s : view.getLobby()) {
            lobbyLabels[i].setText(s);
            i++;
        }
        for(;i < 4; i++){
            lobbyLabels[i].setText("");
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
}
