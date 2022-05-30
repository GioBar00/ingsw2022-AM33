package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.UI;
import it.polimi.ingsw.client.enums.FXMLPath;
import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.enums.ViewState;
import it.polimi.ingsw.client.gui.controllers.*;
import it.polimi.ingsw.network.listeners.ViewListener;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.MoveActionRequest;
import it.polimi.ingsw.network.messages.actions.requests.*;
import it.polimi.ingsw.network.messages.actions.requests.*;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.enums.MoveLocation;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.network.messages.views.GameView;
import it.polimi.ingsw.network.messages.views.TeamsView;
import it.polimi.ingsw.network.messages.views.WizardsView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.Set;

public class GUI extends Application implements UI {
    private Stage stage;

    private Client client;

    private ViewListener listener;

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
            chooseWizardController = ResourceLoader.loadFXML(FXMLPath.CHOOSE_WIZARD, this);
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
            lobbyController = ResourceLoader.loadFXML(FXMLPath.TEAM_LOBBY, this);
            Platform.runLater(lobbyController::init);
            return true;
        }
        return false;
    }

    /**
     * This method checks if the lobby controller is already loaded.
     */
    private void checkLobbyController() {
        if (lobbyController == null) {
            lobbyController = ResourceLoader.loadFXML(FXMLPath.LOBBY, this);
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
            gameController = ResourceLoader.loadFXML(FXMLPath.GAME_SCREEN, this);
            Platform.runLater(gameController::init);
            return true;
        }
        return false;
    }

    /**
     * This method checks if the start screen controller is already loaded.
     */
    private void checkStartScreenController() {
        if (startScreenController == null) {
            startScreenController = ResourceLoader.loadFXML(FXMLPath.START_SCREEN, this);
            Platform.runLater(startScreenController::init);
        }
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
        Platform.runLater(() -> ((TeamLobbyController) lobbyController).updateTeams(teamsView, nickname));
        if (show)
            showLobbyScreen();
    }

    /**
     * @param gameView
     */
    @Override
    public void setGameView(GameView gameView) {
        boolean show = checkGameController();
        if (gameView.getWinners() != null && !gameView.getWinners().isEmpty()) {
            show = false;
            viewState = ViewState.END_GAME;
            gameController = null;
            showWinnerScreen(gameView);
        } else
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
        viewState = ViewState.SETUP;
        Platform.runLater(() -> {
            chooseWizardController = null;
            lobbyController = null;
            gameController = null;
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

    @Override
    public void showGameScreen() {
        Platform.runLater(() -> gameController.loadScene(stage));
        lobbyController = null;
    }

    private void showWinnerScreen(GameView gameView) {
        WinnerScreenController controller = ResourceLoader.loadFXML(FXMLPath.WINNER_SCREEN, this);
        Platform.runLater(() -> {
            controller.loadScene(stage);
            controller.updateGameView(gameView);
        });
    }

    /**
     *
     */
    @Override
    public void hostCanStart() {
        if (lobbyController != null) {
            Platform.runLater(() -> lobbyController.setCanStart());
        }
    }

    /**
     *
     */
    @Override
    public void hostCantStart() {
        if (lobbyController != null) {
            Platform.runLater(() -> lobbyController.setCantStart());
        }
    }

    /**
     * @param message
     */
    @Override
    public void setPossibleActions(Message message) {
        processLastRequest(message);
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    private void processLastRequest(Message message) {
        Platform.runLater(() -> gameController.clearAllButtons(nickname));

        switch (MessageType.retrieveByMessage(message)) {
            case PLAY_ASSISTANT_CARD ->
                    Platform.runLater(() -> gameController.processPlayAssistantCard(((PlayAssistantCard) message).getPlayableAssistantCards()));
            case CHOOSE_CLOUD -> Platform.runLater(() -> gameController.processChooseCloud((ChooseCloud) message));
            case CHOOSE_ISLAND -> Platform.runLater(() -> gameController.processChooseIsland((ChooseIsland) message));
            case CHOOSE_STUDENT_COLOR -> Platform.runLater(() -> {
                Stage chooseColor = new Stage();
                ChooseColorController controller = ResourceLoader.loadFXML(FXMLPath.CHOOSE_GAME, this);
                controller.setAvailableButtons(((ChooseStudentColor) message).getAvailableStudentColors());
                chooseColor.setTitle("Choose a color");
                chooseColor.getIcons().add(ResourceLoader.loadImage(ImagePath.ICON));
                controller.loadScene(chooseColor);
                chooseColor.setAlwaysOnTop(true);
                chooseColor.show();
            });
            case MOVE_MOTHER_NATURE ->
                    Platform.runLater(() -> gameController.processMoveMotherNature((MoveMotherNature) message));
            case MOVE_STUDENT -> Platform.runLater(() -> handleMoveStudent((MoveStudent) message));
            case MULTIPLE_POSSIBLE_MOVES ->
                    Platform.runLater(() -> handlePossibleMoves((MultiplePossibleMoves) message));
            case SWAP_STUDENTS -> Platform.runLater(() -> handleSwap((SwapStudents) message));
        }
    }

    private void handleMoveStudent(MoveStudent moveStudent) {
        switch (moveStudent.getTo()) {
            case ISLAND ->
                    gameController.processMoveCardIsland(moveStudent.getFromIndexesSet(), moveStudent.getToIndexesSet());
            case HALL -> gameController.processMoveCardHall(moveStudent.getFromIndexesSet());
        }
    }

    private void handlePossibleMoves(MultiplePossibleMoves multiplePossibleMoves) {
        Set<Integer> entrance = new HashSet<>();
        Set<Integer> islands = new HashSet<>();
        Set<Integer> entranceToHallIndexes = new HashSet<>();
        for (MoveActionRequest moveActionRequest : multiplePossibleMoves.getPossibleMoves()) {
            if (moveActionRequest.getTo().equals(MoveLocation.ISLAND)) {
                islands.addAll(moveActionRequest.getToIndexesSet());
                entrance.addAll(moveActionRequest.getFromIndexesSet());
            } else if (moveActionRequest.getTo().equals(MoveLocation.HALL)) {
                entranceToHallIndexes.addAll(moveActionRequest.getFromIndexesSet());
            }
        }
        gameController.processMultiplePossibleMoves(entrance, islands, entranceToHallIndexes);
    }

    private void handleSwap(SwapStudents swapStudents) {
        if (swapStudents.getFrom().equals(MoveLocation.CARD)) {
            gameController.processSwapCardEntrance(swapStudents.getFromIndexesSet(), swapStudents.getToIndexesSet());
        }

        if (swapStudents.getFrom().equals(MoveLocation.ENTRANCE)) {
            gameController.processSwapEntranceHall(swapStudents.getFromIndexesSet(), swapStudents.getToIndexesSet());
        }
    }

    @Override
    public void serverUnavailable() {
        System.out.println("Server unavailable");
        if (viewState != ViewState.END_GAME) {
            showAlert("Lost connection with the server or server unavailable.\n" +
                    "Please try again later.");
            if (viewState == ViewState.SETUP) {
                if (startScreenController != null) {
                    Platform.runLater(() -> startScreenController.disableCenter(false));
                }
            } else {
                showStartScreen();
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
     * This method shows an alert to the user.
     *
     * @param message The message to be shown.
     */
    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert messageAlert = new Alert(Alert.AlertType.ERROR);
            messageAlert.setContentText(message);
            messageAlert.show();
        });
    }

    /**
     * @param message
     */
    @Override
    public void showCommMessage(CommMessage message) {
        if (message.getType().equals(CommMsgType.OK))
            return;

        showAlert(message.getType().getMessage());

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
