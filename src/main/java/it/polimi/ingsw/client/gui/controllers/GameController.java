package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.enums.FXMLPath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.GUIUtils;
import it.polimi.ingsw.client.gui.ResourceLoader;
import it.polimi.ingsw.client.gui.audio.MuteToggle;
import it.polimi.ingsw.network.messages.actions.*;
import it.polimi.ingsw.network.messages.actions.requests.ChooseCloud;
import it.polimi.ingsw.network.messages.actions.requests.ChooseIsland;
import it.polimi.ingsw.network.messages.actions.requests.MoveMotherNature;
import it.polimi.ingsw.network.messages.enums.MoveLocation;
import it.polimi.ingsw.network.messages.views.*;
import it.polimi.ingsw.server.model.enums.AssistantCard;
import it.polimi.ingsw.server.model.enums.GamePhase;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.util.Function;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.*;

/**
 * This class is the controller of the game screen.
 */
public class GameController implements GUIController, MuteToggle {
    @FXML
    private AnchorPane anchorPaneCharacterCard0;
    @FXML
    private AnchorPane anchorPaneCharacterCard1;
    @FXML
    private AnchorPane anchorPaneCharacterCard2;
    @FXML
    private VBox vBoxReserve;
    @FXML
    private ImageView imgViewReserveCoin;
    @FXML
    private Label lblReserveCoins;
    @FXML
    private HBox hBoxTitle;
    @FXML
    private ImageView imgViewTitle;
    @FXML
    private Label lblTurn;
    @FXML
    private Label lblGamePhase;
    @FXML
    private Label lblAction;
    @FXML
    private AnchorPane anchorPanePlayerInfo;
    @FXML
    private AnchorPane anchorPaneBoard;
    @FXML
    private AnchorPane anchorPaneIslands;
    @FXML
    private AnchorPane anchorPaneCloud0;
    @FXML
    private AnchorPane anchorPaneCloud1;
    @FXML
    private AnchorPane anchorPaneCloud2;
    @FXML
    private AnchorPane anchorPaneCloud3;
    @FXML
    private AnchorPane anchorPanePlayer0;
    @FXML
    private AnchorPane anchorPanePlayer1;
    @FXML
    private AnchorPane anchorPanePlayer2;
    @FXML
    private GridPane gridRoot;
    @FXML
    private Button btnMute;
    @FXML
    private ImageView imgViewMute;
    @FXML
    private ImageView imgViewBackground;
    @FXML
    private AnchorPane suggestionBox;
    @FXML
    private Rectangle suggestionBackground;
    @FXML
    private Rectangle playerInfoBackground;
    @FXML
    private AnchorPane titleBackAnchor;
    @FXML
    private Rectangle titleBackground;

    private Pane root;

    /**
     * The GUI associated to the controller
     */
    private GUI gui;

    /**
     * List of the Cloud panes
     */
    private final List<AnchorPane> cloudPanes = new ArrayList<>(4);
    /**
     * List of the Cloud controllers
     */
    private final List<CloudController> cloudControllers = new ArrayList<>(2);
    /**
     * List of the CharacterCard panes
     */
    private final List<AnchorPane> characterCardPanes = new ArrayList<>(3);
    /**
     * List of the CharacterCard controllers
     */
    private final List<CharacterCardController> characterCardControllers = new ArrayList<>();
    /**
     * Controller of the Islands
     */
    private IslandsController islandsController;
    /**
     * List of the panes associated with the remaining Players
     */
    private final List<AnchorPane> remainingPlayerPanes = new ArrayList<>(3);
    /**
     * Map of the PlayerControllers, by nickname
     */
    private final Map<String, PlayerController> playerControllersByNickname = new HashMap<>();

    /**
     * Map of the GamePhase messages, by GamePhase
     */
    private final EnumMap<GamePhase, String> gamePhaseMessageMap = new EnumMap<>(Map.of(
            GamePhase.PLANNING, "Planning",
            GamePhase.MOVE_STUDENTS, "Move Students",
            GamePhase.MOVE_MOTHER_NATURE, "Move Mother Nature",
            GamePhase.CHOOSE_CLOUD, "Choose a Cloud"
    ));

    /**
     * The current GameView
     */
    private GameView gameView;

    /**
     * The controller of the waiting view.
     */
    private WaitingViewController waitingViewController;

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
        cloudPanes.add(anchorPaneCloud0);
        cloudPanes.add(anchorPaneCloud1);
        cloudPanes.add(anchorPaneCloud2);
        cloudPanes.add(anchorPaneCloud3);

        characterCardPanes.add(anchorPaneCharacterCard0);
        characterCardPanes.add(anchorPaneCharacterCard1);
        characterCardPanes.add(anchorPaneCharacterCard2);

        remainingPlayerPanes.add(anchorPanePlayer0);
        remainingPlayerPanes.add(anchorPanePlayer1);
        remainingPlayerPanes.add(anchorPanePlayer2);

        vBoxReserve.setVisible(false);
        GUIUtils.bindSize(hBoxTitle, imgViewTitle);
        GUIUtils.bindSize(root, imgViewBackground);
        GUIUtils.bindSize(suggestionBox, suggestionBackground);
        GUIUtils.bindSize(anchorPanePlayerInfo, playerInfoBackground);
        GUIUtils.bindSize(titleBackAnchor, titleBackground);
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
        stage.setResizable(true);
        stage.setMinHeight(0.0);
        stage.setMinWidth(0.0);
        // needed to trigger change
        stage.setMaximized(false);
        stage.setMaximized(true);
        stage.setResizable(false);
    }

    /**
     * This method is used to show a new stage that disables the current one until it is closed.
     *
     * @param stage the stage to show.
     */
    @Override
    public void showNewDisablingStage(Stage stage) {
        gridRoot.setDisable(true);
        stage.setOnHiding(event -> gridRoot.setDisable(false));
        stage.show();
    }

    /**
     * This method is used to update the game with the new game view.
     *
     * @param gameView the new game view.
     * @param nickname of the client who will receive the game view.
     */
    public void updateGameView(GameView gameView, String nickname) {
        closeWaitingStage();
        this.gameView = gameView;
        lblGamePhase.setText("Game Phase: " + gamePhaseMessageMap.get(gameView.getPhase()));
        if (gameView.getCurrentPlayer().equals(gui.getNickname()))
            lblTurn.setText("Your turn");
        else
            lblTurn.setText("Waiting for " + gameView.getCurrentPlayer());
        lblAction.setText("Waiting for your opponent to make their move...");
        updateReserve(gameView.getReserve());
        updateCharacterCardControllers(gameView.getCharacterCardView(), nickname);
        updateCloudControllers(gameView.getCloudViews());
        updateIslandsController(gameView.getIslandsView(), gameView.getMotherNatureIndex());
        updatePlayerControllers(gameView.getPlayersView(), gameView.getPlayerCoins());
    }

    /**
     * This method is used to update the reserve.
     *
     * @param reserve the reserve to update.
     */
    private void updateReserve(Integer reserve) {
        if (reserve == null)
            return;
        vBoxReserve.setVisible(true);
        lblReserveCoins.setText(reserve.toString());
    }

    /**
     * This method is used to load the character cards.
     *
     * @param characterCardViews the character cards to load.
     * @param nickname of the player that will receive the updated CharacterCard scene.
     */
    private void updateCharacterCardControllers(List<CharacterCardView> characterCardViews, String nickname) {
        if (characterCardViews == null)
            return;

        for (int i = 0; i < characterCardViews.size(); i++) {
            if (characterCardControllers.size() <= i) {
                CharacterCardController characterCardController = ResourceLoader.loadFXML(FXMLPath.CHARACTER_CARD, gui);
                characterCardController.init();
                GUIUtils.addToAnchorPane(characterCardPanes.get(i), characterCardController.getRootPane());
                characterCardControllers.add(characterCardController);
                characterCardController.setIndex(i);
            }
            characterCardControllers.get(i).setCharacterView(characterCardViews.get(i));
            if (characterCardViews.get(i).canBeUsed() && gameView.getCurrentPlayer().equals(nickname)) {
                CharacterCardController characterCardController = characterCardControllers.get(i);
                GUIUtils.setButton(characterCardController.characterBtn, e -> {
                    for (CharacterCardController c : characterCardControllers) {
                        GUIUtils.resetButton(c.characterBtn);
                    }
                    gui.notifyViewListener(new ActivatedCharacterCard(characterCardController.getIndex()));
                });
            }
        }
    }

    /**
     * This method is used to load the clouds.
     *
     * @param cloudViews the clouds to load.
     */
    private void updateCloudControllers(List<CloudView> cloudViews) {
        for (int i = 0; i < cloudViews.size(); i++) {
            if (cloudControllers.size() <= i) {
                CloudController cloudController = ResourceLoader.loadFXML(FXMLPath.CLOUD, gui);
                cloudController.init();
                GUIUtils.addToAnchorPane(cloudPanes.get(i), cloudController.getRootPane());
                cloudControllers.add(cloudController);
            }
            cloudControllers.get(i).setCloud(cloudViews.get(i), i);
        }
    }

    /**
     * This method is used to update the islands.
     *
     * @param islandGroupViews  the islands to update.
     * @param motherNatureIndex the index of the mother nature.
     */
    private void updateIslandsController(List<IslandGroupView> islandGroupViews, int motherNatureIndex) {
        if (islandsController == null) {
            islandsController = ResourceLoader.loadFXML(FXMLPath.ISLANDS, gui);
            islandsController.init();
            GUIUtils.addToAnchorPane(anchorPaneIslands, islandsController.getRootPane());
        }
        islandsController.setIslandsView(islandGroupViews, motherNatureIndex);
    }

    /**
     * This method is used to update the player controllers.
     *
     * @param playerViews the player views to update.
     * @param playerCoins the coins of the players.
     */
    private void updatePlayerControllers(List<PlayerView> playerViews, Map<String, Integer> playerCoins) {
        for (PlayerView playerView : playerViews) {
            if (!playerControllersByNickname.containsKey(playerView.getNickname())) {
                PlayerController playerController = ResourceLoader.loadFXML(FXMLPath.PLAYER, gui);
                playerController.init();
                addPlayerController(playerController, playerView.getNickname());
            }
            Integer coins = playerCoins != null ? playerCoins.get(playerView.getNickname()) : null;
            playerControllersByNickname.get(playerView.getNickname()).updatePlayerView(playerView, coins);
        }

    }

    /**
     * This method is used to create the player view and add it to the grid.
     *
     * @param playerController the player controller to add.
     * @param nickname         the nickname of the player.
     */
    private void addPlayerController(PlayerController playerController, String nickname) {
        playerControllersByNickname.put(nickname, playerController);
        if (nickname.equals(gui.getNickname())) {
            playerController.enableHand(this);
            playerController.moveSchoolBoard(anchorPaneBoard);
            playerController.movePlayerInfo(anchorPanePlayerInfo);
        } else {
            GUIUtils.addToAnchorPane(remainingPlayerPanes.get(0), playerController.getRootPane());
            remainingPlayerPanes.remove(0);
        }
    }

    /**
     * This method set the action on the cloud button.
     *
     * @param message {@link ChosenCloud} message that contains a list of available clouds.
     */
    public void processChooseCloud(ChooseCloud message) {
        lblAction.setText("Select a highlighted cloud");
        List<Button> cloudButtons = new LinkedList<>();
        for (int i : message.getAvailableCloudIndexes()) {
            Button cloudBtn = cloudControllers.get(i).cloudBtn;
            cloudButtons.add(cloudBtn);
            GUIUtils.setButton(cloudBtn, actionEvent -> {
                for (Button btn : cloudButtons)
                    GUIUtils.resetButton(btn);
                gui.notifyViewListener(new ChosenCloud(i));
            });
        }
    }

    /**
     * This method sets the action on island buttons when the player could select an island.
     *
     * @param message the {@link ChooseIsland} message that contains a set of available islands .
     */
    public void processChooseIsland(ChooseIsland message) {
        lblAction.setText("Select a highlighted island");
        for (Integer i : message.getAvailableIslandIndexes()) {
            if (i < islandsController.islandControllers.size()) {
                Button islandBtn = islandsController.islandControllers.get(i).islandButton;
                GUIUtils.setButton(islandBtn, actionEvent -> {
                    for (IslandController ic : islandsController.islandControllers)
                        GUIUtils.resetButton(ic.islandButton);
                    gui.notifyViewListener(new ChosenIsland(i));
                });
            }
        }

    }

    /**
     * This method sets the islands the player could choose during the moving mother nature phase.
     *
     * @param message the {@link MoveMotherNature} message that contains the max steps mother nature could take.
     */
    public void processMoveMotherNature(MoveMotherNature message) {
        lblAction.setText("Select an island to move mother nature to");
        Map<Integer, Integer> availableIslandIndexes = new HashMap<>();
        for (int i = 1; i <= message.getMaxNumMoves(); i++) {
            Integer index = (gameView.getMotherNatureIndex() + i) % islandsController.islandControllers.size();
            availableIslandIndexes.put(index, i);
        }
        for (Integer i : availableIslandIndexes.keySet()) {
            if (i < islandsController.islandControllers.size()) {
                Button islandBtn = islandsController.islandControllers.get(i).islandButton;
                GUIUtils.setButton(islandBtn, actionEvent -> {
                    for (IslandController ic : islandsController.islandControllers)
                        GUIUtils.resetButton(ic.islandButton);
                    gui.notifyViewListener(new MovedMotherNature(availableIslandIndexes.get(i)));
                });
            }
        }
    }

    /**
     * This method resets all the clickable buttons.
     *
     * @param nickname the nickname of the player associated with the view.
     */
    public void clearAllButtons(String nickname) {
        for (IslandController ic : islandsController.islandControllers)
            GUIUtils.resetButton(ic.islandButton);
        for (CloudController cc : cloudControllers)
            GUIUtils.resetButton(cc.cloudBtn);
        playerControllersByNickname.get(nickname).getSchoolBoardController().clearAllButtons();
    }

    /**
     * This method handle the move to island requests. It highlights the islands the player could move to.
     * When the player chooses an island it sends a {@link MovedStudent} message to the server.
     *
     * @param fromIndexes a set of indexes that represents the available student colors on the character card.
     * @param toIndexes   a set of indexes that represents the available island indexes.
     */
    public void processMoveCardIsland(Set<Integer> fromIndexes, Set<Integer> toIndexes) {
        lblAction.setText("Select a student from the character card");
        Integer cardIndex = findActivatedCard();
        if (cardIndex == null)
            return;
        for (Integer fromIndex : fromIndexes) {
            GUIUtils.setButton(characterCardControllers.get(cardIndex).buttons.get(StudentColor.retrieveStudentColorByOrdinal(fromIndex)), e -> {
                lblAction.setText("Select a highlighted island");
                for (Integer resInd : fromIndexes) {
                    GUIUtils.resetButton(characterCardControllers.get(cardIndex).buttons.get(StudentColor.retrieveStudentColorByOrdinal(resInd)));
                }
                for (Integer toIndex : toIndexes) {
                    GUIUtils.setButton(islandsController.islandControllers.get(toIndex).islandButton, action -> {
                        for (Integer resToInd : fromIndexes) {
                            GUIUtils.resetButton(islandsController.islandControllers.get(resToInd).islandButton);
                        }
                        gui.notifyViewListener(new MovedStudent(MoveLocation.CARD, fromIndex, MoveLocation.ISLAND, toIndex));
                    });
                }
            });
        }
    }

    /**
     * This method handles the move from a card to the hall. Highlights the colors of the students that could be moved.
     * When the player chooses a student it sends a {@link MovedStudent} message to the server.
     *
     * @param fromIndexes a set of indexes that represents the available student colors on the character card.
     */
    public void processMoveCardHall(Set<Integer> fromIndexes) {
        lblAction.setText("Select a student from the character card to move to the hall");
        Integer cardIndex = findActivatedCard();
        if (cardIndex == null)
            return;

        for (Integer fromIndex : fromIndexes) {
            GUIUtils.setButton(characterCardControllers.get(cardIndex).buttons.get(StudentColor.retrieveStudentColorByOrdinal(fromIndex)), e -> {
                for (Integer resInd : fromIndexes) {
                    GUIUtils.resetButton(characterCardControllers.get(cardIndex).buttons.get(StudentColor.retrieveStudentColorByOrdinal(resInd)));
                }
                gui.notifyViewListener(new MovedStudent(MoveLocation.CARD, fromIndex, MoveLocation.HALL, null));
            });
        }
    }

    /**
     * This method handles the swap of students between card and entrance. Highlights the colors of the students that could be swapped and the students in the entrance.
     * When the player chooses a student it sends a {@link SwappedStudents} message to the server.
     *
     * @param cardIndexes a set of indexes that represents the available student colors on the character card.
     * @param toIndexes   a set of indexes that represents the available students on the entrance.
     */
    public void processSwapCardEntrance(Set<Integer> cardIndexes, Set<Integer> toIndexes) {
        lblAction.setText("Select a student from the character card");
        Integer cardIndex = findActivatedCard();

        if (cardIndex == null)
            return;

        characterCardControllers.get(cardIndex).activateEndButton();

        SchoolBoardController schoolBoardController = playerControllersByNickname.get(gui.getNickname()).getSchoolBoardController();
        for (Integer fromIndex : cardIndexes) {
            GUIUtils.setButton(characterCardControllers.get(cardIndex).buttons.get(StudentColor.retrieveStudentColorByOrdinal(fromIndex)), e -> {
                lblAction.setText("Select a highlighted student in the entrance");
                characterCardControllers.get(cardIndex).hideEndButton();
                for (Integer resInd : cardIndexes) {
                    GUIUtils.resetButton(characterCardControllers.get(cardIndex).buttons.get(StudentColor.retrieveStudentColorByOrdinal(resInd)));
                }
                for (Integer toIndex : toIndexes) {
                    GUIUtils.setButton(schoolBoardController.entranceButtons.get(toIndex), action -> {
                        for (Integer resToInd : cardIndexes) {
                            GUIUtils.resetButton(schoolBoardController.entranceButtons.get(resToInd));
                        }
                        gui.notifyViewListener(new SwappedStudents(MoveLocation.CARD, fromIndex, MoveLocation.ENTRANCE, toIndex));
                    });
                }
            });
        }
    }

    /**
     * This method handles the swap of students between entrance and hall. Highlights the students that could be swapped.
     * When the player chooses a student it sends a {@link SwappedStudents} message to the server.
     *
     * @param fromIndexes a set of indexes that represents the available students on the entrance.
     * @param toIndexes   a set of indexes that represents the available student colors on the hall.
     */
    public void processSwapEntranceHall(Set<Integer> fromIndexes, Set<Integer> toIndexes) {
        lblAction.setText("Select a highlighted student in the entrance");
        Integer cardIndex = findActivatedCard();
        if (cardIndex == null)
            return;

        if (toIndexes.isEmpty() || fromIndexes.isEmpty())
            return;

        characterCardControllers.get(cardIndex).activateEndButton();

        SchoolBoardController schoolBoardController = playerControllersByNickname.get(gui.getNickname()).getSchoolBoardController();
        for (Integer fromIndex : fromIndexes) {
            GUIUtils.setButton(schoolBoardController.entranceButtons.get(fromIndex), e -> {
                lblAction.setText("Select a highlighted student color in the hall");
                for (Integer resInd : fromIndexes) {
                    GUIUtils.resetButton(schoolBoardController.entranceButtons.get(resInd));
                }
                characterCardControllers.get(cardIndex).hideEndButton();
                for (Integer toIndex : toIndexes) {
                    schoolBoardController.hallAnchorPaneByColor.get(StudentColor.retrieveStudentColorByOrdinal(toIndex)).toFront();
                    GUIUtils.setButton(schoolBoardController.hallButtonsByColor.get(StudentColor.retrieveStudentColorByOrdinal(toIndex)), action -> {
                        for (Integer resToInd : toIndexes) {
                            GUIUtils.resetButton(schoolBoardController.hallButtonsByColor.get(StudentColor.retrieveStudentColorByOrdinal(resToInd)));
                            schoolBoardController.hallAnchorPaneByColor.get(StudentColor.retrieveStudentColorByOrdinal(toIndex)).toBack();
                        }
                        gui.notifyViewListener(new SwappedStudents(MoveLocation.ENTRANCE, fromIndex, MoveLocation.HALL, toIndex));
                    });
                }
            });
        }
    }

    /**
     * This method handles a multiple possible moves. It highlights the clickable buttons and , after the choice of the player, sends the move to the server.
     *
     * @param entranceIndexes       a set of indexes that represents the available students on the entrance that can be moved to an island.
     * @param islandIndexes         a set of indexes that represents the available island.
     * @param entranceToHallIndexes a set of indexes that represents the available students on the entrance that can be moved to the hall.
     */
    public void processMultiplePossibleMoves(Set<Integer> entranceIndexes, Set<Integer> islandIndexes, Set<Integer> entranceToHallIndexes) {
        lblAction.setText("Select a highlighted student in the entrance");
        PlayerController me = playerControllersByNickname.get(gui.getNickname());
        List<Button> entranceButtons = me.getSchoolBoardController().entranceButtons;
        Function clearHallAndIslandButtons = () -> {
            // clear island buttons
            for (Button b : islandsController.islandControllers.stream().map(IslandController::getIslandButton).toList()) {
                GUIUtils.resetButton(b);
            }
            // clear entrance to hall
            GUIUtils.resetButton(me.getSchoolBoardController().hallButton);
        };
        for (Integer i : entranceIndexes) {
            GUIUtils.setButton(entranceButtons.get(i), event -> {
                lblAction.setText("Select a highlighted island or hall to move the student to");
                clearCharacterButtons();
                // activate islands
                for (Integer j : islandIndexes) {
                    GUIUtils.setButton(islandsController.islandControllers.get(j).islandButton, e -> {
                        gui.notifyViewListener(new MovedStudent(MoveLocation.ENTRANCE, i, MoveLocation.ISLAND, j));
                        clearHallAndIslandButtons.apply();
                    });
                }
                // activate hall
                if (entranceToHallIndexes.contains(i)) {
                    GUIUtils.setButton(me.getSchoolBoardController().hallButton, e -> {
                        gui.notifyViewListener(new MovedStudent(MoveLocation.ENTRANCE, i, MoveLocation.HALL, null));
                        clearHallAndIslandButtons.apply();
                    });
                }
                // clear entrance buttons
                for (Button b : entranceButtons) {
                    GUIUtils.resetButton(b);
                }
            });
        }
    }

    /**
     * Helper method that returns the activated character card.
     *
     * @return the index of the active character card.
     */
    private Integer findActivatedCard() {
        if (characterCardControllers.isEmpty())
            return null;

        Integer cardIndex = 0;
        for (CharacterCardView characterCardView : gameView.getCharacterCardView()) {
            if (characterCardView.isActivating())
                break;
            cardIndex++;
        }

        return cardIndex;
    }

    /**
     * This method handles the request of playing a card. It shows the available cards.
     *
     * @param availableCards an enum set of available cards.
     */
    public void processPlayAssistantCard(EnumSet<AssistantCard> availableCards) {
        PlayerController me = playerControllersByNickname.get(gui.getNickname());
        me.playAssistantCard(availableCards);
    }

    /**
     * This method close the Assistant Card View.
     */
    public void closeAssistantCardView() {
        PlayerController me = playerControllersByNickname.get(gui.getNickname());
        me.closeAssistantCardView();
    }

    /**
     * Disables all the character card buttons.
     */
    private void clearCharacterButtons() {
        for (CharacterCardController characterCardController : characterCardControllers) {
            characterCardController.resetCharacterButton();
        }
    }

    /**
     * This method is called when changing scene or closing the stage.
     */
    @Override
    public void unload() {
        for (PlayerController playerController : playerControllersByNickname.values()) {
            playerController.unload();
        }
        closeWaitingStage();
    }

    /**
     * This method closes the waiting stage if it is open.
     */
    private void closeWaitingStage() {
        if (waitingViewController != null) {
            waitingViewController.unload();
            Platform.runLater(() -> ((Stage) waitingViewController.getRootPane().getScene().getWindow()).close());

        }
    }

    /**
     * handles the mute toggle.
     */
    @FXML
    @Override
    public void handleMuteButton() {
        toggleMute(imgViewMute);
    }

    /**
     * This method requests to show the waiting screen.
     */
    public void showWaiting() {
        showWaiting(60);
    }

    /**
     * Shows the waiting screen. The waiting screen has a countdown.
     *
     * @param timer the initial value of the countdown timer.
     */
    private void showWaiting(int timer) {
        waitingViewController = ResourceLoader.loadFXML(FXMLPath.WAITING_SCREEN, gui);
        Platform.runLater(() -> {
            closeAssistantCardView();
            waitingViewController.init();
            Stage stage = new Stage();
            waitingViewController.loadScene(stage);
            showNewDisablingStage(stage);
            waitingViewController.startTimer(timer);
            stage.setOnHidden(event -> {
                int t = waitingViewController.getTimer();
                waitingViewController.unload();
                waitingViewController = null;
                if (t > 0)
                    showWaiting(t);
            });
        });
    }
}
