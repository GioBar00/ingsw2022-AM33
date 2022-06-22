package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.GUIUtils;
import it.polimi.ingsw.network.messages.actions.PlayedAssistantCard;
import it.polimi.ingsw.server.model.enums.AssistantCard;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * The controller related to choose-assistant view
 */
public class AssistantCardController implements GUIController {

    /**
     * Private record that contains the anchorPaneButton and imageView related to an Assistant card
     */
    private record AssistantView(AnchorPane anchorPaneButton, AnchorPane anchorPaneImageView) {
    }

    /**
     * {@link GUI} instance.
     */
    private GUI gui;

    /**
     * The root of the scene.
     */
    private Pane root;

    /**
     * Boolean set to true if the player has to choose an assistant card.
     */
    private boolean choseAnAssistant = false;

    private EnumMap<AssistantCard, GridPane> assistantGridMap;

    private Map<GridPane, AssistantView> assistantViewMaps;

    private EnumSet<AssistantCard> availableCards = EnumSet.noneOf(AssistantCard.class);
    @FXML
    private Text lblTitle;

    @FXML
    private AnchorPane cheetahBtn;
    @FXML
    private AnchorPane ostrichBtn;
    @FXML
    private AnchorPane catBtn;
    @FXML
    private AnchorPane eagleBtn;
    @FXML
    private AnchorPane foxBtn;
    @FXML
    private AnchorPane snakeBtn;
    @FXML
    private AnchorPane octopusBtn;
    @FXML
    private AnchorPane dogBtn;
    @FXML
    private AnchorPane elephantBtn;
    @FXML
    private AnchorPane turtleBtn;

    @FXML
    private AnchorPane cheetahImg;
    @FXML
    private AnchorPane ostrichImg;
    @FXML
    private AnchorPane catImg;
    @FXML
    private AnchorPane eagleImg;
    @FXML
    private AnchorPane foxImg;
    @FXML
    private AnchorPane snakeImg;
    @FXML
    private AnchorPane octopusImg;
    @FXML
    private AnchorPane dogImg;
    @FXML
    private AnchorPane elephantImg;
    @FXML
    private AnchorPane turtleImg;

    @FXML
    private GridPane cheetah;
    @FXML
    private GridPane ostrich;
    @FXML
    private GridPane cat;
    @FXML
    private GridPane eagle;
    @FXML
    private GridPane fox;
    @FXML
    private GridPane snake;
    @FXML
    private GridPane octopus;
    @FXML
    private GridPane dog;
    @FXML
    private GridPane elephant;
    @FXML
    private GridPane turtle;

    @FXML
    private FlowPane flowpane;

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
     * This method is used to load the scene of the controller on the stage.
     *
     * @param stage the stage to load the scene on.
     */
    @Override
    public void loadScene(Stage stage) {
        stage.setScene(new Scene(getRootPane()));
        stage.setMinHeight(540.0);
        stage.setMinWidth(870.0);
        stage.setResizable(false);
    }

    /**
     * This method is used to set the assistant cards available to the player.
     *
     * @param availableCards the available cards
     */
    public void setAvailableCards(EnumSet<AssistantCard> availableCards) {
        this.availableCards = availableCards;
    }

    /**
     * @return if the player has chosen an assistant
     */
    public boolean hasChosenAnAssistant() {
        return choseAnAssistant;
    }

    /**
     * This methods set the values in the assistant card view
     *
     * @param playableAssistantCards an EnumSet of playable card
     */
    public void setPlayable(EnumSet<AssistantCard> playableAssistantCards) {
        lblTitle.setText("Choose an assistant card");
        setCards(playableAssistantCards, true);
    }

    /**
     * This method shows the image of the cards
     */
    public void showAssistantCards() {
        lblTitle.setText("Your assistant cards");
        setCards(availableCards, false);
    }

    /**
     * Send a {@link PlayedAssistantCard} request to the server.
     *
     * @param card the card the player wants to play.
     */
    @FXML
    public void playAssistantCard(AssistantCard card) {
        gui.notifyViewListener(new PlayedAssistantCard(card));
        forceClose();
    }

    /**
     * This method is used to initialize the controller and stage.
     */
    @Override
    public void init() {
        assistantGridMap = new EnumMap<>(AssistantCard.class);
        assistantGridMap.put(AssistantCard.CHEETAH, cheetah);
        assistantGridMap.put(AssistantCard.OSTRICH, ostrich);
        assistantGridMap.put(AssistantCard.CAT, cat);
        assistantGridMap.put(AssistantCard.EAGLE, eagle);
        assistantGridMap.put(AssistantCard.FOX, fox);
        assistantGridMap.put(AssistantCard.SNAKE, snake);
        assistantGridMap.put(AssistantCard.OCTOPUS, octopus);
        assistantGridMap.put(AssistantCard.DOG, dog);
        assistantGridMap.put(AssistantCard.ELEPHANT, elephant);
        assistantGridMap.put(AssistantCard.TURTLE, turtle);

        assistantViewMaps = new HashMap<>();
        assistantViewMaps.put(cheetah, new AssistantView(cheetahBtn, cheetahImg));
        assistantViewMaps.put(ostrich, new AssistantView(ostrichBtn, ostrichImg));
        assistantViewMaps.put(cat, new AssistantView(catBtn, catImg));
        assistantViewMaps.put(eagle, new AssistantView(eagleBtn, eagleImg));
        assistantViewMaps.put(fox, new AssistantView(foxBtn, foxImg));
        assistantViewMaps.put(snake, new AssistantView(snakeBtn, snakeImg));
        assistantViewMaps.put(octopus, new AssistantView(octopusBtn, octopusImg));
        assistantViewMaps.put(dog, new AssistantView(dogBtn, dogImg));
        assistantViewMaps.put(elephant, new AssistantView(elephantBtn, elephantImg));
        assistantViewMaps.put(turtle, new AssistantView(turtleBtn, turtleImg));

        for (AssistantCard card : AssistantCard.values()) {
            flowpane.heightProperty().addListener((observable, oldValue, newValue) -> {
                double value = (newValue.doubleValue() - 20) / 2;
                assistantGridMap.get(card).setPrefHeight(value);
                assistantGridMap.get(card).setPrefWidth(value * 6 / 10);
            });
        }

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
     * this method is used to set the grids inside the FlowPane
     *
     * @param availableCards the card that will be displayed
     * @param setButtons     true if the buttons need to be shown
     */
    public void setCards(EnumSet<AssistantCard> availableCards, boolean setButtons) {
        clearAllGrids();
        for (AssistantCard ac : AssistantCard.values()) {
            if (availableCards.contains(ac)) {
                setAssistantImage(assistantViewMaps.get(assistantGridMap.get(ac)).anchorPaneImageView(), ac);
                if (setButtons)
                    setAssistantButton(assistantViewMaps.get(assistantGridMap.get(ac)).anchorPaneButton(), ac);
            } else {
                flowpane.getChildren().remove(assistantGridMap.get(ac));
            }
        }
    }

    /**
     * this method sets the anchorPaneButton of a specific gridPane
     *
     * @param anchorPane inside the cell of the grid, where the anchorPaneButton will be placed
     * @param ac         that will be shown in the grid
     */
    public void setAssistantButton(AnchorPane anchorPane, AssistantCard ac) {
        Button button = new Button();
        button.setText(ac.name());
        button.setTextAlignment(TextAlignment.CENTER);
        button.setAlignment(Pos.CENTER);
        button.setOnAction(e -> playAssistantCard(ac));
        GUIUtils.addToAnchorPane(anchorPane, button);
    }

    /**
     * this method sets the image of an Assistant of a specific gridPane
     *
     * @param anchorPane inside the cell of the grid, where the image will be placed
     * @param ac         assistant that will be shown in the image
     */
    public void setAssistantImage(AnchorPane anchorPane, AssistantCard ac) {
        ImageView imageView = new ImageView();
        imageView.setImage(GUIUtils.getAssistantCard(ac));
        anchorPane.getChildren().add(imageView);
        GUIUtils.bindSize(anchorPane, imageView);
    }

    /**
     * This method clears all the grids. Il will be called before setting new content in the cells
     * of the grid so that there are no duplicates
     */
    public void clearAllGrids() {
        for (AssistantCard ac : AssistantCard.values()) {
            assistantViewMaps.get(assistantGridMap.get(ac)).anchorPaneImageView().getChildren().removeAll();
            assistantViewMaps.get(assistantGridMap.get(ac)).anchorPaneButton().getChildren().removeAll();
        }
    }

    /**
     * This method closes the window and prevents its reopening.
     */
    public void forceClose() {
        choseAnAssistant = true;
        Platform.runLater(() -> ((Stage) root.getScene().getWindow()).close());
    }
}
