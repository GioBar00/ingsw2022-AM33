package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.ResourceLoader;
import it.polimi.ingsw.network.messages.actions.PlayedAssistantCard;
import it.polimi.ingsw.server.model.enums.AssistantCard;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;

/**
 * The controller related to choose-assistant view
 */
public class AssistantCardController implements GUIController {

    /**
     * Private record that contains the button and imageView related to an Assistant card
     */
    private record AssistantView(Button button, ImageView imageView) {
    }

    @FXML
    public Text lblTitle;

    private GUI gui;
    private Parent root;
    private boolean choseAnAssistant = false;

    @FXML
    public Button cheetahBtn;
    @FXML
    public Button ostrichBtn;
    @FXML
    public Button catBtn;
    @FXML
    public Button eagleBtn;
    @FXML
    public Button foxBtn;
    @FXML
    public Button snakeBtn;
    @FXML
    public Button octopusBtn;
    @FXML
    public Button dogBtn;
    @FXML
    public Button elephantBtn;
    @FXML
    public Button turtleBtn;

    @FXML
    public ImageView cheetahImg;
    @FXML
    public ImageView ostrichImg;
    @FXML
    public ImageView catImg;
    @FXML
    public ImageView eagleImg;
    @FXML
    public ImageView foxImg;
    @FXML
    public ImageView snakeImg;
    @FXML
    public ImageView octopusImg;
    @FXML
    public ImageView dogImg;
    @FXML
    public ImageView elephantImg;
    @FXML
    public ImageView turtleImg;

    private EnumMap<AssistantCard, AssistantView> assistantViews;

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
        stage.setScene(new Scene(getParent()));
        stage.setMinHeight(540.0);
        stage.setMinWidth(870.0);
        stage.setResizable(false);
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
        for (AssistantCard card : playableAssistantCards) {
            assistantViews.get(card).button().setVisible(true);
            assistantViews.get(card).button().setDisable(false);
            showAssistantCard(card);
        }
    }

    /**
     * This method shows the image of the cards
     *
     * @param assistantCards the cards to show
     */
    public void showAssistantCards(EnumSet<AssistantCard> assistantCards) {
        lblTitle.setText("Your assistant cards");
        for (AssistantCard card : assistantCards) {
            showAssistantCard(card);
        }
    }

    /**
     * This method shows the image of the card
     *
     * @param card the card to show
     */
    private void showAssistantCard(AssistantCard card) {
        assistantViews.get(card).imageView.setVisible(true);
    }

    /**
     * Send a {@link PlayedAssistantCard} reqest to the server.
     *
     * @param card the card the player wants to play.@
     */
    @FXML
    public void playAssistantCard(AssistantCard card) {
        choseAnAssistant = true;
        gui.notifyViewListener(new PlayedAssistantCard(card));
    }

    /**
     * This method is used to initialize the controller and stage.
     */
    @Override
    public void init() {
        assistantViews = new EnumMap<>(AssistantCard.class);
        assistantViews.put(AssistantCard.CHEETAH, new AssistantView(cheetahBtn, cheetahImg));
        assistantViews.put(AssistantCard.OSTRICH, new AssistantView(ostrichBtn, ostrichImg));
        assistantViews.put(AssistantCard.CAT, new AssistantView(catBtn, catImg));
        assistantViews.put(AssistantCard.EAGLE, new AssistantView(eagleBtn, eagleImg));
        assistantViews.put(AssistantCard.FOX, new AssistantView(foxBtn, foxImg));
        assistantViews.put(AssistantCard.SNAKE, new AssistantView(snakeBtn, snakeImg));
        assistantViews.put(AssistantCard.OCTOPUS, new AssistantView(octopusBtn, octopusImg));
        assistantViews.put(AssistantCard.DOG, new AssistantView(dogBtn, dogImg));
        assistantViews.put(AssistantCard.ELEPHANT, new AssistantView(elephantBtn, elephantImg));
        assistantViews.put(AssistantCard.TURTLE, new AssistantView(turtleBtn, turtleImg));

        for (AssistantCard assistantCard : AssistantCard.values()) {
            Button btn = assistantViews.get(assistantCard).button();
            btn.setOnAction(e -> playAssistantCard(assistantCard));
            btn.setVisible(false);
            btn.setDisable(true);
            ImageView imageView = assistantViews.get(assistantCard).imageView();
            imageView.setVisible(false);
            imageView.setImage(getImage(assistantCard));
        }

    }

    /**
     * This method is used to set the parent of the controller.
     *
     * @param parent the parent of the controller.
     */
    @Override
    public void setParent(Parent parent) {
        root = parent;
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
     * This method returns the path of the image related to a specified Assistant CArd
     *
     * @param card the card
     * @return the ImagePath of the card
     */
    private Image getImage(AssistantCard card) {
        return switch (card) {
            case CAT -> ResourceLoader.loadImage(ImagePath.CAT);
            case CHEETAH -> ResourceLoader.loadImage(ImagePath.CHEETAH);
            case DOG -> ResourceLoader.loadImage(ImagePath.DOG);
            case ELEPHANT -> ResourceLoader.loadImage(ImagePath.ELEPHANT);
            case FOX -> ResourceLoader.loadImage(ImagePath.FOX);
            case OCTOPUS -> ResourceLoader.loadImage(ImagePath.OCTOPUS);
            case SNAKE -> ResourceLoader.loadImage(ImagePath.SNAKE);
            case TURTLE -> ResourceLoader.loadImage(ImagePath.TURTLE);
            case EAGLE -> ResourceLoader.loadImage(ImagePath.EAGLE);
            case OSTRICH -> ResourceLoader.loadImage(ImagePath.OSTRICH);
        };
    }
}
