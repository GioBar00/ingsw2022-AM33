package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.UI;
import it.polimi.ingsw.client.enums.AudioPath;
import it.polimi.ingsw.client.enums.FXMLPath;
import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.enums.ViewState;
import it.polimi.ingsw.client.gui.audio.AudioManager;
import it.polimi.ingsw.client.gui.controllers.*;
import it.polimi.ingsw.network.listeners.ViewListener;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.MoveActionRequest;
import it.polimi.ingsw.network.messages.actions.requests.*;
import it.polimi.ingsw.network.messages.enums.CommMsgType;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.enums.MoveLocation;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.network.messages.server.Winners;
import it.polimi.ingsw.network.messages.views.GameView;
import it.polimi.ingsw.network.messages.views.PlayerView;
import it.polimi.ingsw.network.messages.views.TeamsView;
import it.polimi.ingsw.network.messages.views.WizardsView;
import it.polimi.ingsw.server.model.enums.GameState;
import it.polimi.ingsw.server.model.enums.Tower;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.*;

/**
 * The GUI class handle all the view controllers for the graphic user interface.
 */
public class GUI extends Application implements UI {

    /**
     * The stage of the application.
     */
    private Stage stage;

    /**
     * {@link Client}.
     */
    private Client client;

    /**
     * The view listener used to notify a new message.
     */
    private ViewListener listener;

    /**
     * The nickname of the player.
     */
    private String nickname;

    /**
     * The controller of the choose-wizard view.
     */
    private ChooseWizardController chooseWizardController;

    /**
     * The controller of the lobby view.
     */
    private LobbyController lobbyController;

    /**
     * The controller of the start-screen view.
     */
    private StartScreenController startScreenController;

    /**
     * The controller of the game-screen view.
     */
    private GameController gameController;

    /**
     * The current state of the view.
     */
    private ViewState viewState = ViewState.SETUP;

    /**
     * A list of the playerView in the game.
     */
    private List<PlayerView> players;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This method set the player's nickname.
     *
     * @param nickname the nickname to set.
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Getter of the nickname.
     *
     * @return the nickname.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Init method of the application. Check the path of the resources.
     */
    @Override
    public void init() {
        try {
            ResourceLoader.checkResources();
        } catch (MissingResourceException e) {
            System.err.println(e.getMessage());
            stop();
        }
    }

    /**
     * Create a new {@link Client} and starts it.
     *
     * @param stage the stage of the application.
     */
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

    /**
     * This method close the UI.
     */
    @Override
    public void stop() {
        System.exit(0);
    }

    /**
     * Getter of the client.
     *
     * @return the client.
     */
    public Client getClient() {
        return client;
    }

    /**
     * Getter of the stage.
     *
     * @return the stage.
     */
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
     * This method sets the WizardView.
     *
     * @param wizardsView the WizardView to set.
     */
    @Override
    public void setWizardView(WizardsView wizardsView) {
        boolean show = checkChooseWizardController();
        Platform.runLater(() -> chooseWizardController.updateWizards(wizardsView));
        if (show)
            showWizardMenu();
    }

    /**
     * This method sets the TeamView.
     *
     * @param teamsView the TeamView to set.
     */
    @Override
    public void setTeamsView(TeamsView teamsView) {
        boolean show = viewState == ViewState.CHOOSE_TEAM && checkTeamLobbyController();
        Platform.runLater(() -> ((TeamLobbyController) lobbyController).updateTeams(teamsView, nickname));
        if (show)
            showLobbyScreen();
    }

    /**
     * This method sets the GameView.
     *
     * @param gameView the GameView to set.
     */
    @Override
    public void setGameView(GameView gameView) {
        boolean show = checkGameController();
        if (this.players == null) {
            players = gameView.getPlayersView();
        }

        if (gameView.getState() == GameState.ENDED) {
            show = false;
        } else
            Platform.runLater(() -> gameController.updateGameView(gameView, nickname));
        if (show) {
            viewState = ViewState.PLAYING;
            showGameScreen();
        }

    }

    /**
     * This method requests to the user to choose the game mode and the number of players.
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
            chooseGameStage.onCloseRequestProperty().set(event -> {
                viewState = ViewState.SETUP;
                client.closeConnection();
                if (startScreenController != null)
                    startScreenController.disableCenter(false);
            });
            chooseGameStage.show();
        });
    }

    /**
     * This method shows the start screen.
     */
    @Override
    public void showStartScreen() {
        System.out.println("Showing start screen");
        viewState = ViewState.SETUP;
        checkStartScreenController();
        Platform.runLater(() -> {
            chooseWizardController = null;
            lobbyController = null;
            if (gameController != null)
                gameController.unload();
            gameController = null;
            startScreenController.disableCenter(false);
            startScreenController.loadScene(stage);
            if (!stage.isShowing())
                stage.show();
            AudioManager.playAudio(AudioPath.START);
        });

    }

    /**
     * This method shows the wizard menu where the player can choose the wizard.
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
                    if (startScreenController != null)
                        startScreenController.disableCenter(false);
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
     * This method shows the game menu where the player can choose the team.
     */
    @Override
    public void showLobbyScreen() {
        Platform.runLater(() -> {
            lobbyController.loadScene(stage);
            AudioManager.playAudio(AudioPath.LOBBY);
        });
        startScreenController = null;
    }

    /**
     * This method shows the main game screen.
     */
    @Override
    public void showGameScreen() {
        Platform.runLater(() -> {
            gameController.loadScene(stage);
            AudioManager.playAudio(AudioPath.GAME);
        });
        lobbyController = null;
    }

    /**
     * This method shows the winner screen.
     *
     * @param winners a list with the winners.
     */
    private void showWinnerScreen(EnumSet<Tower> winners) {
        viewState = ViewState.END_GAME;
        if (gameController != null)
            gameController.unload();
        gameController = null;
        WinnerScreenController controller = ResourceLoader.loadFXML(FXMLPath.WINNER_SCREEN, this);
        Platform.runLater(() -> {
            controller.init();
            controller.loadScene(stage);
            controller.updateWinners(this.players, winners);
        });
    }

    /**
     * This method notifies the host that the game can start.
     */
    @Override
    public void hostCanStart() {
        if (lobbyController != null) {
            Platform.runLater(() -> lobbyController.setCanStart());
        }
    }

    /**
     * This method notifies the host that the game cant start.
     */
    @Override
    public void hostCantStart() {
        if (lobbyController != null) {
            Platform.runLater(() -> lobbyController.setCantStart());
        }
    }

    /**
     * This method sets the possible actions the player can do.
     *
     * @param message the message containing the possible actions.
     */
    @Override
    public void setPossibleActions(Message message) {
        processLastRequest(message);
    }

    /**
     * Private method used to process the last request sent from the server.
     *
     * @param message the message containing the possible actions.
     */
    private void processLastRequest(Message message) {
        Platform.runLater(() -> gameController.clearAllButtons(nickname));

        switch (MessageType.retrieveByMessage(message)) {
            case PLAY_ASSISTANT_CARD ->
                    Platform.runLater(() -> gameController.processPlayAssistantCard(((PlayAssistantCard) message).getPlayableAssistantCards()));
            case CHOOSE_CLOUD -> Platform.runLater(() -> gameController.processChooseCloud((ChooseCloud) message));
            case CHOOSE_ISLAND -> Platform.runLater(() -> gameController.processChooseIsland((ChooseIsland) message));
            case CHOOSE_STUDENT_COLOR -> Platform.runLater(() -> {
                Stage chooseColor = new Stage();
                ChooseColorController controller = ResourceLoader.loadFXML(FXMLPath.CHOOSE_COLOR, this);
                controller.init();
                controller.setAvailableButtons(((ChooseStudentColor) message).getAvailableStudentColors());
                controller.loadScene(chooseColor);
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

    /**
     * This method handles a {@link MoveStudent} request.
     *
     * @param moveStudent the {@link MoveStudent} request.
     */
    private void handleMoveStudent(MoveStudent moveStudent) {
        switch (moveStudent.getTo()) {
            case ISLAND ->
                    gameController.processMoveCardIsland(moveStudent.getFromIndexesSet(), moveStudent.getToIndexesSet());
            case HALL -> gameController.processMoveCardHall(moveStudent.getFromIndexesSet());
        }
    }

    /**
     * This method handles a {@link MultiplePossibleMoves} request.
     *
     * @param multiplePossibleMoves the {@link MultiplePossibleMoves} request.
     */
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

    /**
     * This method handles a {@link SwapStudents} request.
     *
     * @param swapStudents the {@link SwapStudents} request.
     */
    private void handleSwap(SwapStudents swapStudents) {
        if (swapStudents.getFrom().equals(MoveLocation.CARD)) {
            gameController.processSwapCardEntrance(swapStudents.getFromIndexesSet(), swapStudents.getToIndexesSet());
        }

        if (swapStudents.getFrom().equals(MoveLocation.ENTRANCE)) {
            gameController.processSwapEntranceHall(swapStudents.getFromIndexesSet(), swapStudents.getToIndexesSet());
        }
    }

    /**
     * This method notifies the player when the server is unavailable.
     */
    @Override
    public void serverUnavailable() {
        if (viewState != ViewState.END_GAME) {
            System.out.println("Server unavailable");
            if (viewState == ViewState.SETUP) {
                showAlert("Server unavailable, please try again later.");
            } else {
                showAlert("Lost connection with the server or server unavailable.\n" +
                        "Please try again later.");
            }
            showStartScreen();
        }
    }

    /**
     * This method close the UI.
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
            messageAlert.initOwner(stage);
            messageAlert.show();
        });
    }

    /**
     * This method shows a {@link CommMessage} to the user.
     *
     * @param message the message to show.
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
     * This method shows the winners of the game.
     *
     * @param winners a {@link Winners} message containing the winners.
     */
    @Override
    public void showWinners(Winners winners) {
        if (winners.getWinners() != null && !winners.getWinners().isEmpty()) {
            showWinnerScreen(winners.getWinners());
        }
    }

    /**
     * This method notifies the player that they have to wait for the other players.
     */
    @Override
    public void showWaiting() {
        if (gameController != null)
            gameController.showWaiting();

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
