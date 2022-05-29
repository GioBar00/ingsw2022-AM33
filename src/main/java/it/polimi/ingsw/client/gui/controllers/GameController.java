package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.enums.FXMLPath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.GUIUtils;
import it.polimi.ingsw.client.gui.ResourceLoader;
import it.polimi.ingsw.network.messages.actions.*;
import it.polimi.ingsw.network.messages.actions.requests.ChooseCloud;
import it.polimi.ingsw.network.messages.actions.requests.ChooseIsland;
import it.polimi.ingsw.network.messages.actions.requests.MoveMotherNature;
import it.polimi.ingsw.network.messages.enums.MoveLocation;
import it.polimi.ingsw.network.messages.views.*;
import it.polimi.ingsw.server.model.enums.StudentColor;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;

public class GameController implements GUIController {
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

    private Pane root;

    private GUI gui;

    private final List<AnchorPane> cloudPanes = new ArrayList<>(4);
    private final List<CloudController> cloudControllers = new ArrayList<>(2);
    private final List<AnchorPane> characterCardPanes = new ArrayList<>(3);
    private final List<CharacterCardController> characterCardControllers = new ArrayList<>();
    private IslandsController islandsController;
    private final List<AnchorPane> remainingPlayerPanes = new ArrayList<>(3);
    private final Map<String, PlayerController> playerControllersByNickname = new HashMap<>();

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
        stage.setScene(new Scene(getRootPane()));
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

    @FXML
    public void handleMuteButton() {
        System.out.println("Mute");
    }

    /**
     * This method is used to update the game with the new game view.
     *
     * @param gameView the new game view.
     */
    public void updateGameView(GameView gameView) {
        this.gameView = gameView;
        updateReserve(gameView.getReserve());
        updateCharacterCardControllers(gameView.getCharacterCardView());
        updateCloudControllers(gameView.getCloudViews());
        updateIslandsController(gameView.getIslandsView(), gameView.getMotherNatureIndex());
        Integer coins = gameView.getPlayerCoins() != null ? gameView.getPlayerCoins().get(gui.getNickname()) : null;
        updatePlayerControllers(gameView.getPlayersView(), coins);
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
    private void updateCharacterCardControllers(List<CharacterCardView> characterCardViews) {
        if (characterCardViews == null)
            return;
        for (int i = 0; i < characterCardViews.size(); i++) {
            if (characterCardControllers.size() <= i) {
                CharacterCardController characterCardController = (CharacterCardController) ResourceLoader.loadFXML(FXMLPath.CHARACTER_CARD, gui);
                characterCardController.init();
                GUIUtils.addToAnchorPane(characterCardPanes.get(i), characterCardController.getRootPane());
                characterCardControllers.add(characterCardController);
                characterCardController.setIndex(i);
            }
            characterCardControllers.get(i).setCharacterView(characterCardViews.get(i));
            if (characterCardViews.get(i).canBeUsed()) {
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
                CloudController cloudController = (CloudController) ResourceLoader.loadFXML(FXMLPath.CLOUD, gui);
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
            islandsController = (IslandsController) ResourceLoader.loadFXML(FXMLPath.ISLANDS, gui);
            islandsController.init();
            GUIUtils.addToAnchorPane(anchorPaneIslands, islandsController.getRootPane());
        }
        islandsController.setIslandsView(islandGroupViews, motherNatureIndex);
    }

    /**
     * This method is used to update the player controllers.
     *
     * @param playerViews the player views to update.
     * @param coins       the coins of the player.
     */
    private void updatePlayerControllers(List<PlayerView> playerViews, Integer coins) {
        for (PlayerView playerView : playerViews) {
            if (!playerControllersByNickname.containsKey(playerView.getNickname())) {
                PlayerController playerController = (PlayerController) ResourceLoader.loadFXML(FXMLPath.PLAYER, gui);
                playerController.init();
                addPlayerController(playerController, playerView.getNickname());
            }
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
     * This methods set the action on the cloud button.
     *
     * @param message {@link ChosenCloud} message that contains a list of available clouds.
     */
    public void processChooseCloud(ChooseCloud message) {
        for (int i : message.getAvailableCloudIndexes()) {
            Button cloudBtn = cloudControllers.get(i).cloudBtn;
            GUIUtils.setButton(cloudBtn, actionEvent -> {
                gui.notifyViewListener(new ChosenCloud(i));
                cloudBtn.setDisable(true);
            });
        }
    }


    /**
     * This method sets the action on island buttons when the player could select an island.
     *
     * @param message the {@link ChooseIsland} message that contains a set of available islands .
     */
    public void processChooseIsland(ChooseIsland message) {

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

    public void processMoveCardIsland(Set<Integer> fromIndexes, Set<Integer> toIndexes) {
        int i = 0;
        for (CharacterCardView characterCardView : gameView.getCharacterCardView()) {
            if (characterCardView.isActivating())
                break;
            i++;
        }
        for (Integer fromIndex : fromIndexes) {
            GUIUtils.setButton(characterCardControllers.get(i).buttons.get(StudentColor.retrieveStudentColorByOrdinal(fromIndex)).button, e -> {
                for (Integer resInd : fromIndexes) {
                    GUIUtils.resetButton(characterCardControllers.get(resInd).buttons.get(StudentColor.retrieveStudentColorByOrdinal(fromIndex)).button);
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
}
