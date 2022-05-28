package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.enums.FXMLPath;
import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.GUIUtils;
import it.polimi.ingsw.client.gui.ResourceLoader;
import it.polimi.ingsw.network.messages.views.*;
import it.polimi.ingsw.server.model.enums.AssistantCard;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.*;

public class GameController implements GUIController {

    @FXML
    public AnchorPane anchorPaneBoard0;
    @FXML
    public AnchorPane anchorPaneBoard1;
    @FXML
    public AnchorPane anchorPaneBoard2;
    @FXML
    public AnchorPane anchorPaneBoard3;
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
    public AnchorPane anchorPaneCharacterCard0;
    @FXML
    public AnchorPane anchorPaneCharacterCard1;
    @FXML
    public AnchorPane anchorPaneCharacterCard2;
    @FXML
    public GridPane gridRoot;
    @FXML
    public AnchorPane anchorPaneReserve;
    @FXML
    public ImageView imgReserve;
    @FXML
    public Label lblCoins;

    private GUI gui;

    private Pane root;

    private GameView gameView;

    private final List<AnchorPane> cloudPanes = new ArrayList<>(3);
    private final List<AnchorPane> characterCardPanes = new ArrayList<>();
    private final List<AnchorPane> remainingPlayerPanes = new ArrayList<>(2);

    private final List<CharacterCardController> characterCardControllers = new ArrayList<>();
    private final List<CloudController> cloudControllers = new ArrayList<>();
    private IslandsGridController islandController;
    private final Map<String, PlayerController> playerControllersByNickname = new HashMap<>();
    private final Map<String, AnchorPane> playerPanesByNickname = new HashMap<>();
    private AssistantCardController assistantCardController;

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

        remainingPlayerPanes.add(anchorPaneBoard1);
        remainingPlayerPanes.add(anchorPaneBoard2);
        remainingPlayerPanes.add(anchorPaneBoard3);

        anchorPaneReserve.setVisible(false);
        anchorPaneReserve.heightProperty().addListener((observable, oldValue, newValue) -> imgReserve.setFitHeight(newValue.doubleValue()));
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
     * This method is used to show a new stage.
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
     * This method returns the player view of the player with a given nickname.
     *
     * @param nickname of the player.
     * @return the player view of the player with a given nickname.
     */
    private PlayerView getPlayerView(String nickname) {
        for (PlayerView playerView : gameView.getPlayersView()) {
            if (playerView.getNickname().equals(nickname)) {
                return playerView;
            }
        }
        return null;
    }

    /**
     * @param nickname of the player.
     * @return the playable assistant card of the player with a given nickname.
     */
    private EnumSet<AssistantCard> getPlayerAssistantCards(String nickname) {
        PlayerView playerView = getPlayerView(nickname);
        if (playerView != null) {
            EnumSet<AssistantCard> cards = EnumSet.noneOf(AssistantCard.class);
            cards.addAll(playerView.getAssistantCards());
            return cards;
        }
        return null;
    }

    /**
     * This method creates a new stage to let the player choose an assistant card.
     */
    public void playAssistantCard(EnumSet<AssistantCard> playableCards) {
        if (assistantCardController != null) {
            ((Stage) assistantCardController.getRootPane().getScene().getWindow()).close();
        }

        if (playableCards != null) {
            showAssistantCards("Choose an assistant card to play", playableCards, true);
            if (!assistantCardController.hasChosenAnAssistant())
                playAssistantCard(playableCards);
            else
                assistantCardController = null;
        }

    }

    /**
     * This method shows the assistant cards of the player.
     */
    private void showAssistantCards() {
        EnumSet<AssistantCard> cards = getPlayerAssistantCards(gui.getNickname());
        if (cards != null) {
            showAssistantCards("Your assistant cards", cards, false);
            assistantCardController = null;
        }
    }

    /**
     * This method shows the assistant cards of the player.
     *
     * @param title of the window
     */
    private void showAssistantCards(String title, EnumSet<AssistantCard> cards, boolean playable) {
        gridRoot.setDisable(true);
        AssistantCardController controller = (AssistantCardController) ResourceLoader.loadFXML(FXMLPath.CHOOSE_ASSISTANT, gui);
        assistantCardController = controller;
        controller.init();
        controller.setAvailableCards(cards);
        controller.showAssistantCards();
        if (playable)
            controller.setPlayable(cards);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.getIcons().add(ResourceLoader.loadImage(ImagePath.ICON));
        controller.loadScene(stage);
        stage.setAlwaysOnTop(true);
        stage.setOnHiding(event -> gridRoot.setDisable(false));
        stage.show();
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
            }
            characterCardControllers.get(i).setCharacterView(characterCardViews.get(i));
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
     * This method is used to update the reserve.
     *
     * @param reserve the reserve to update.
     */
    private void updateReserve(Integer reserve) {
        if (reserve == null)
            return;
        else
            anchorPaneReserve.setVisible(true);
        lblCoins.setText(reserve.toString());
    }

    /**
     * This method is used to update the islands.
     *
     * @param islandGroupViews the islands to update.
     * @param motherNatureIndex the index of the mother nature.
     */
    private void updateIslandsController(List<IslandGroupView> islandGroupViews, int motherNatureIndex) {
        if (islandController == null) {
            islandController = (IslandsGridController) ResourceLoader.loadFXML(FXMLPath.ISLANDS, gui);
            islandController.init();
            GUIUtils.addToAnchorPane(anchorPaneIslands, islandController.getRootPane());
        }
        islandController.setIslandsView(islandGroupViews, motherNatureIndex);
    }

    /**
     * This method is used to create the player view and add it to the grid.
     *
     * @param playerController the player controller to add.
     * @param nickname the nickname of the player.
     */
    private void addPlayerController(PlayerController playerController, String nickname) {
        playerControllersByNickname.put(nickname, playerController);
        if (nickname.equals(gui.getNickname())) {
            playerController.setShowHand(this::showAssistantCards);
            GUIUtils.addToAnchorPane(anchorPaneBoard0, playerController.getRootPane());
            playerPanesByNickname.put(nickname, anchorPaneBoard0);
            remainingPlayerPanes.remove(anchorPaneBoard0);
        } else {
            double rotate = 90 * (4 - remainingPlayerPanes.size());
            playerController.getRootPane().setRotate(rotate);
            GUIUtils.addToAnchorPane(remainingPlayerPanes.get(0), playerController.getRootPane());
            playerPanesByNickname.put(nickname, remainingPlayerPanes.get(0));
            remainingPlayerPanes.remove(0);
        }
    }

    /**
     * This method is used to update the player controllers.
     *
     * @param playerViews the player views to update.
     * @param coins the coins of the player.
     */
    private void updatePlayerControllers(List<PlayerView> playerViews, Integer coins) {
        /*
        for (PlayerView playerView : playerViews) {
            if (!playerControllersByNickname.containsKey(playerView.getNickname())) {
                PlayerController playerController = (PlayerController) ResourceLoader.loadFXML(FXMLPath.PLAYER, gui);
                playerController.init();
                addPlayerController(playerController, playerView.getNickname());
            }
            playerControllersByNickname.get(playerView.getNickname()).updatePlayerView(playerView, coins);
        }

         */
        PlayerView playerView = playerViews.stream().filter(p -> p.getNickname().equals(gui.getNickname())).findFirst().orElse(null);
        if (playerView != null) {
            PlayerController playerController = (PlayerController) ResourceLoader.loadFXML(FXMLPath.PLAYER, gui);
            playerController.init();
            addPlayerController(playerController, playerView.getNickname());
            playerControllersByNickname.get(playerView.getNickname()).updatePlayerView(playerView, coins);
        }

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
}
