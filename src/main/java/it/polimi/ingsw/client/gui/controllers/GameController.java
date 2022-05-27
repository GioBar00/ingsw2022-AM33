package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.enums.FXMLPath;
import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.ResourceLoader;
import it.polimi.ingsw.network.messages.views.CharacterCardView;
import it.polimi.ingsw.network.messages.views.GameView;
import it.polimi.ingsw.network.messages.views.PlayerView;
import it.polimi.ingsw.server.model.enums.AssistantCard;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

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

    private GUI gui;

    private Parent root;

    private String nickname;

    private GameView gameView;

    private final List<AnchorPane> cloudPanes = new ArrayList<>(3);
    private final List<AnchorPane> characterCardPanes = new ArrayList<>();
    private final List<AnchorPane> playerPanes = new ArrayList<>(2);

    private final List<CharacterCardController> characterCardControllers = new ArrayList<>();
    //private final List<CloudController> cloudControllers = new ArrayList<>();
    //private IslandController islandController;
    //private final List<PlayerController> playerControllers = new ArrayList<>();
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
        playerPanes.add(anchorPaneBoard0);
        playerPanes.add(anchorPaneBoard1);
        playerPanes.add(anchorPaneBoard2);
        playerPanes.add(anchorPaneBoard3);
    }

    /**
     * This method is used to set the parent of the controller.
     *
     * @param parent the parent of the controller.
     */
    @Override
    public void setParent(Parent parent) {
        this.root = parent;
    }

    /**
     * This method returns the node of the controller.
     *
     * @return the node of the controller.
     */
    @Override
    public Parent getParent() {
        return root;
    }

    /**
     * This method is used to load the scene of the controller on the stage.
     *
     * @param stage the stage to load the scene on.
     */
    @Override
    public void loadScene(Stage stage) {
        stage.setScene(new Scene(getParent()));
        stage.setResizable(true);
        stage.setMinHeight(0.0);
        stage.setMinWidth(0.0);
        stage.setMaximized(true);
        stage.setResizable(false);
    }

    /**
     * Setter for the nickname.
     *
     * @param nickname of the player.
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @FXML
    public void handleMuteButton() {
        System.out.println("Mute");
    }

    /**
     * Adds node to anchor pane and sets the anchors.
     *
     * @param anchorPane to add the node to
     * @param node       to add
     */
    private void addToAnchorPane(AnchorPane anchorPane, Node node) {
        anchorPane.getChildren().add(node);
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
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
    public void playAssistantCard() {
        if (assistantCardController != null) {
            ((Stage) assistantCardController.getParent().getScene().getWindow()).close();
        }
        EnumSet<AssistantCard> cards = getPlayerAssistantCards(nickname);
        if (cards != null) {
            showAssistantCards("Choose an assistant card to play", cards, true);
            if (!assistantCardController.hasChosenAnAssistant())
                playAssistantCard();
            else
                assistantCardController = null;
        }

    }

    /**
     * This method shows the assistant cards of the player.
     */
    private void showAssistantCards() {
        EnumSet<AssistantCard> cards = getPlayerAssistantCards(nickname);
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
        if (playable)
            controller.setPlayable(cards);
        else
            controller.showAssistantCards(cards);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.getIcons().add(ResourceLoader.loadImage(ImagePath.ICON));
        controller.loadScene(stage);
        stage.setAlwaysOnTop(true);
        stage.showAndWait();
        gridRoot.setDisable(false);
    }

    /**
     * This method is used to load the character cards.
     *
     * @param characterCardViews the character cards to load.
     */
    private void updateCharacterCardControllers(List<CharacterCardView> characterCardViews) {
        for (int i = 0; i < characterCardViews.size(); i++) {
            if (characterCardControllers.size() <= i) {
                CharacterCardController characterCardController = (CharacterCardController) ResourceLoader.loadFXML(FXMLPath.CHARACTER_CARD, gui);
                characterCardController.init();
                addToAnchorPane(characterCardPanes.get(i), characterCardController.getParent());
                characterCardControllers.add(characterCardController);
            }
            characterCardControllers.get(i).setCharacterView(characterCardViews.get(i));
        }
    }

    public void updateGameView(GameView gameView) {

    }
}
