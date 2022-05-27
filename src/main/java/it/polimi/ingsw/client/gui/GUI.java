package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.UI;
import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.enums.FXMLPath;
import it.polimi.ingsw.client.enums.ViewState;
import it.polimi.ingsw.client.gui.controllers.*;
import it.polimi.ingsw.network.listeners.ViewListener;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.actions.requests.PlayAssistantCard;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.network.messages.views.GameView;
import it.polimi.ingsw.network.messages.views.PlayerView;
import it.polimi.ingsw.network.messages.views.TeamsView;
import it.polimi.ingsw.network.messages.views.WizardsView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.MissingResourceException;

public class GUI extends Application implements UI {
    private Stage stage;

    private Client client;

    private ViewListener listener;
    private Message lastRequest;

    private String nickname;

    private ChooseWizardController chooseWizardController;

    private LobbyController lobbyController;

    private StartScreenController startScreenController;

    private GameController gameController;

    private ViewState viewState = ViewState.SETUP;

    @Override
    public void init() {
        try {
            ResourceLoader.checkResources();
        } catch (MissingResourceException e) {
            System.err.println(e.getMessage());
            stop();
        }
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Eriantys");
        stage.getIcons().add(ResourceLoader.loadImage(ImagePath.ICON));
        stage.setOnCloseRequest(event -> stop());

        client = new Client(this);
        setViewListener(client);
        client.startClient();
    }

    @Override
    public void stop() {
       System.exit(0);
    }

    public Client getClient() {
        return client;
    }

    public Stage getStage() {
        return stage;
    }

    /**
     * This method checks if the wizard controller is already loaded.
     *
     * @return true if the controller was loaded, false otherwise.
     */
    private boolean checkChooseWizardController() {
        if (chooseWizardController == null) {
            chooseWizardController = (ChooseWizardController) ResourceLoader.loadFXML(FXMLPath.CHOOSE_WIZARD, this);
            Platform.runLater(chooseWizardController::init);
            return true;
        }
        return false;
    }

    /**
     * This method checks if the team lobby controller is already loaded.
     *
     * @return true if the controller was loaded, false otherwise.
     */
    private boolean checkTeamLobbyController() {
        if (lobbyController == null || !lobbyController.canHandleTeams()) {
            lobbyController = (TeamLobbyController) ResourceLoader.loadFXML(FXMLPath.TEAM_LOBBY, this);
            Platform.runLater(lobbyController::init);
            return true;
        }
        return false;
    }

    /**
     * This method checks if the lobby controller is already loaded.
     */
    private void checkLobbyController() {
        if(lobbyController == null) {
            lobbyController = (NormalLobbyController) ResourceLoader.loadFXML(FXMLPath.LOBBY, this);
            Platform.runLater(lobbyController::init);
        }
    }

    /**
     * This method checks if the game controller is already loaded.
     *
     * @return true if the controller was loaded, false otherwise.
     */
    private boolean checkGameController() {
        if (gameController == null) {
            gameController = (GameController) ResourceLoader.loadFXML(FXMLPath.GAME_SCREEN, this);
            Platform.runLater(gameController::init);
            return true;
        }
        return false;
    }

    /**
     * This method checks if the start screen controller is already loaded.
     *
     * @return true if the controller was loaded, false otherwise.
     */
    private boolean checkStartScreenController() {
        if (startScreenController == null) {
            startScreenController = (StartScreenController) ResourceLoader.loadFXML(FXMLPath.START_SCREEN, this);
            Platform.runLater(startScreenController::init);
            return true;
        }
        return false;
    }

    /**
     * @param wizardsView
     */
    @Override
    public void setWizardView(WizardsView wizardsView) {
        boolean show = checkChooseWizardController();
        Platform.runLater(() -> chooseWizardController.updateWizards(wizardsView));
        if (show)
            showWizardMenu();
    }

    /**
     * @param teamsView
     */
    @Override
    public void setTeamsView(TeamsView teamsView) {
        boolean show = viewState == ViewState.CHOOSE_TEAM && checkTeamLobbyController();
        Platform.runLater(() -> ((TeamLobbyController)lobbyController).updateTeams(teamsView));
        if (show)
            showLobbyScreen();
    }

    /**
     * @param gameView
     */
    @Override
    public void setGameView(GameView gameView) {
        boolean show = checkGameController();
        Platform.runLater(() -> gameController.updateGameView(gameView));
        if (show) {
            viewState = ViewState.PLAYING;
            showGameScreen();
        }
    }

    /**
     *
     */
    @Override
    public void chooseGame() {
        Platform.runLater(() -> {
            if (startScreenController != null)
                startScreenController.disableCenter(true);
            Stage chooseGameStage = new Stage();
            GUIController controller = ResourceLoader.loadFXML(FXMLPath.CHOOSE_GAME, this);
            chooseGameStage.setTitle("Create a new game");
            chooseGameStage.getIcons().add(ResourceLoader.loadImage(ImagePath.ICON));
            controller.loadScene(chooseGameStage);
            chooseGameStage.setAlwaysOnTop(true);
            chooseGameStage.show();
        });
    }

    /**
     *
     */
    @Override
    public void showStartScreen() {
        System.out.println("Showing start screen");
        checkStartScreenController();
        Platform.runLater(() -> {
            startScreenController.loadScene(stage);
            if (!stage.isShowing())
                stage.show();
        });
    }

    /**
     *
     */
    @Override
    public void showWizardMenu() {
        System.out.println("Showing wizard menu");
        checkLobbyController();
        Platform.runLater(() -> {
            if (startScreenController != null)
                startScreenController.disableCenter(true);
            Stage chooseWizardStage = new Stage();
            chooseWizardStage.setTitle("Choose a Wizard");
            chooseWizardStage.getIcons().add(ResourceLoader.loadImage(ImagePath.ICON));
            chooseWizardStage.onHidingProperty().set(event -> {
                if (chooseWizardController.hasChosenWizard()) {
                    viewState = ViewState.CHOOSE_TEAM;
                    if (lobbyController != null)
                        showLobbyScreen();
                } else {
                    viewState = ViewState.SETUP;
                    client.closeConnection();
                }
                chooseWizardController = null;
            });
            chooseWizardController.loadScene(chooseWizardStage);
            chooseWizardStage.setAlwaysOnTop(true);
            chooseWizardStage.show();
            viewState = ViewState.CHOOSE_WIZARD;
        });
    }

    /**
     *
     */
    @Override
    public void showLobbyScreen() {
        Platform.runLater(() -> lobbyController.loadScene(stage));
        startScreenController = null;
    }

    /**
     *
     */
    @Override
    public void hostCanStart() {
        if(lobbyController != null) {
            Platform.runLater(() -> lobbyController.setCanStart());
        }
    }

    /**
     *
     */
    @Override
    public void hostCantStart() {
        if(lobbyController != null) {
            Platform.runLater(() -> lobbyController.setCantStart());
        }
    }

    /**
     *
     */
    @Override
    public void showGameScreen() {
        Platform.runLater(() -> gameController.loadScene(stage));
        lobbyController = null;
    }

    /**
     * @param message
     */
    @Override
    public void setPossibleActions(Message message) {
        lastRequest = message;
        processLastRequest(lastRequest);
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    private void processLastRequest(Message message) {
        /*
        switch (MessageType.retrieveByMessage(message)){
            case PLAY_ASSISTANT_CARD -> {
                ((AssistantCardController)sceneByPath.get(FXMLPath.CHOOSE_ASSISTANT).getUserData()).setPlayable(((PlayAssistantCard)message).getPlayableAssistantCards());
            }
        }

         */
    }
    @Override
    public void serverUnavailable() {
        System.out.println("Server unavailable");
        if (viewState == ViewState.SETUP) {
            if (startScreenController != null) {
                startScreenController.disableCenter(false);
            }
        }
    }

    /**
     *
     */
    @Override
    public void close() {
        stop();
    }

    /**
     * @param message
     */
    @Override
    public void showCommMessage(CommMessage message) {
        System.out.println("Showing comm message");
        System.out.println(MessageBuilder.toJson(message));
    }

    /**
     * Sets the view listener.
     *
     * @param listener the listener to set
     */
    @Override
    public void setViewListener(ViewListener listener) {
        this.listener = listener;
    }

    /**
     * Notifies the listener that a request has occurred.
     *
     * @param message the request to notify
     */
    @Override
    public void notifyViewListener(Message message) {
        listener.onMessage(message);
    }
}
