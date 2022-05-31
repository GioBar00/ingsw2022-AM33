package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.enums.AudioPath;
import it.polimi.ingsw.client.enums.FXMLPath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.GUIUtils;
import it.polimi.ingsw.client.gui.ResourceLoader;
import it.polimi.ingsw.client.gui.audio.AudioManager;
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
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.*;

public class GameController implements GUIController, MuteToggle {
    @FXML
    public AnchorPane anchorPaneCharacterCard0;
    @FXML
    public AnchorPane anchorPaneCharacterCard1;
    @FXML
    public AnchorPane anchorPaneCharacterCard2;
    @FXML
    public VBox vBoxReserve;
    @FXML
    public ImageView imgViewReserveCoin;
    @FXML
    public Label lblReserveCoins;
    @FXML
    public HBox hBoxTitle;
    @FXML
    public ImageView imgViewTitle;
    @FXML
    public Label lblTurn;
    @FXML
    public Label lblRound;
    @FXML
    public Label lblGamePhase;
    @FXML
    public Label lblAction;
    @FXML
    public AnchorPane anchorPanePlayerInfo;
    @FXML
    public AnchorPane anchorPaneBoard;
    @FXML
    public AnchorPane anchorPaneIslands;
    @FXML
    public AnchorPane anchorPaneCloud0;
    @FXML
    public AnchorPane anchorPaneCloud1;
    @FXML
    public AnchorPane anchorPaneCloud2;
    @FXML
    public AnchorPane anchorPaneCloud3;
    @FXML
    public AnchorPane anchorPanePlayer0;
    @FXML
    public AnchorPane anchorPanePlayer1;
    @FXML
    public AnchorPane anchorPanePlayer2;
    @FXML
    public GridPane gridRoot;
    @FXML
    public Button btnMute;
    @FXML
    public ImageView imgViewMute;
    @FXML
    public ImageView imgViewBackground;
    @FXML
    public AnchorPane suggestionBox;
    @FXML
    public Rectangle suggestionBackground;
    @FXML
    public Rectangle playerInfoBackground;
    @FXML
    public AnchorPane titleBackAnchor;
    @FXML
    public Rectangle titleBackground;

    private Pane root;

    private GUI gui;

    private final List<AnchorPane> cloudPanes = new ArrayList<>(4);
    private final List<CloudController> cloudControllers = new ArrayList<>(2);
    private final List<AnchorPane> characterCardPanes = new ArrayList<>(3);
    private final List<CharacterCardController> characterCardControllers = new ArrayList<>();
    private IslandsController islandsController;
    private final List<AnchorPane> remainingPlayerPanes = new ArrayList<>(3);
    private final Map<String, PlayerController> playerControllersByNickname = new HashMap<>();

    private final EnumMap<GamePhase, String> gamePhaseMessageMap = new EnumMap<>(Map.of(
            GamePhase.PLANNING, "Planning",
            GamePhase.MOVE_STUDENTS, "Move 3 Students",
            GamePhase.MOVE_MOTHER_NATURE, "Move Mother Nature",
            GamePhase.CHOOSE_CLOUD, "Choose a Cloud"
    ));

    private GameView gameView;

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
        stage.setMaximized(true);
        //stage.setResizable(false);
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
     */
    public void updateGameView(GameView gameView, String nickname) {
        this.gameView = gameView;
        lblRound.setText("Round: 1");
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

    public void processSwapEntranceHall(Set<Integer> fromIndexes, Set<Integer> toIndexes) {
        lblAction.setText("Select a highlighted student in the entrance");
        Integer cardIndex = findActivatedCard();
        if (cardIndex == null)
            return;

        //TODO hallbuttons don't work
        if(toIndexes.isEmpty() || fromIndexes.isEmpty())
            return;

        characterCardControllers.get(cardIndex).activateEndButton();

        SchoolBoardController schoolBoardController = playerControllersByNickname.get(gui.getNickname()).getSchoolBoardController();
        for (Integer fromIndex : fromIndexes) {
            GUIUtils.setButton(schoolBoardController.entranceButtons.get(fromIndex), e -> {
                lblAction.setText("Select a highlighted student color in the hall");
                lblAction.setText("Select an highlighted student color in the hall");
                for (Integer resInd : fromIndexes) {
                    GUIUtils.resetButton(schoolBoardController.entranceButtons.get(resInd));
                }
                characterCardControllers.get(cardIndex).hideEndButton();
                for (Integer toIndex : toIndexes) {
                    GUIUtils.setButton(schoolBoardController.hallButtonsByColor.get(StudentColor.retrieveStudentColorByOrdinal(toIndex)), action -> {
                        for (Integer resToInd : toIndexes) {
                            GUIUtils.resetButton(schoolBoardController.hallButtonsByColor.get(StudentColor.retrieveStudentColorByOrdinal(resToInd)));
                        }
                        gui.notifyViewListener(new SwappedStudents(MoveLocation.ENTRANCE, fromIndex, MoveLocation.HALL, toIndex));
                    });
                }
            });
        }
    }

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
                System.out.println("Entrance button pressed: " + i);
                // activate islands
                for (Integer j : islandIndexes) {
                    GUIUtils.setButton(islandsController.islandControllers.get(j).islandButton, e -> {
                        System.out.println("Island button pressed: " + j);
                        gui.notifyViewListener(new MovedStudent(MoveLocation.ENTRANCE, i, MoveLocation.ISLAND, j));
                        clearHallAndIslandButtons.apply();
                    });
                }
                // activate hall
                System.out.println(entranceToHallIndexes.toString());
                if (entranceToHallIndexes.contains(i)) {
                    GUIUtils.setButton(me.getSchoolBoardController().hallButton, e -> {
                        System.out.println("Hall button pressed");
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

    public void processPlayAssistantCard(EnumSet<AssistantCard> availableCards) {
        PlayerController me = playerControllersByNickname.get(gui.getNickname());
        me.playAssistantCard(availableCards);
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
     * handles the mute toggle.
     */
    @FXML
    @Override
    public void handleMuteButton() {
        toggleMute(imgViewMute);
    }
}
