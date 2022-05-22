package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.network.messages.actions.PlayedAssistantCard;
import it.polimi.ingsw.server.model.enums.AssistantCard;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.*;

public class AssistantCardController implements GUIController {
    private GUI gui;

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

    private List<AssistantCard> played;


    /**
     * This method is used to set the GUI of the controller.
     *
     * @param gui the GUI of the controller.
     */
    @Override
    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    public void setPlayable(EnumSet<AssistantCard> playableAssistantCards) {

        ArrayList<AssistantCard> notPlayable = new ArrayList<>(Arrays.asList(AssistantCard.values()));

        for(AssistantCard assistantCard : playableAssistantCards){
            notPlayable.remove(assistantCard);
            enableCard(assistantCard);
        }
        for(AssistantCard assistantCard : played){
            notPlayable.remove(assistantCard);
            playedCard(assistantCard);
        }

        for(AssistantCard assistantCard : notPlayable){
            deactivateCard(assistantCard);
            deactivateCard(assistantCard);
        }

    }

    public void setPlayedCard(AssistantCard assistantCard) {
        if(played.contains(assistantCard))
            return;
        played.add(assistantCard);
    }

    private void enableCard(AssistantCard assistantCard) {
        assert getImagePath(assistantCard) != null;
        assistantViews.get(assistantCard).assistantImage.setImage(new Image(getImagePath(assistantCard).getPath()));
        assistantViews.get(assistantCard).assistantButton.setDisable(false);
        assistantViews.get(assistantCard).assistantButton.setVisible(true);
    }

    private void playedCard(AssistantCard assistantCard) {
        assistantViews.get(assistantCard).assistantImage.setImage(new Image(ImagePath.BACK_CARD.getPath()));
        assistantViews.get(assistantCard).assistantButton.setDisable(true);
        assistantViews.get(assistantCard).assistantButton.setVisible(false);

    }

    private void deactivateCard(AssistantCard assistantCard) {
        assistantViews.get(assistantCard).assistantImage.setImage(new Image(getImagePath(assistantCard).getPath()));
        assistantViews.get(assistantCard).assistantButton.setDisable(true);
        assistantViews.get(assistantCard).assistantButton.setVisible(false);
    }
    @FXML
    public void playAssistantCard(AssistantCard card) {
        gui.notifyViewListener(new PlayedAssistantCard(card));
    }

    /**
     * This method is used to initialize the controller and stage.
     */
    @Override
    public void init() {
        cheetahBtn.setOnAction(e -> playAssistantCard(AssistantCard.CHEETAH));
        ostrichBtn.setOnAction(e -> playAssistantCard(AssistantCard.OSTRICH));
        catBtn.setOnAction(e -> playAssistantCard(AssistantCard.CAT));
        eagleBtn.setOnAction(e -> playAssistantCard(AssistantCard.EAGLE));
        foxBtn.setOnAction(e -> playAssistantCard(AssistantCard.FOX));
        snakeBtn.setOnAction(e -> playAssistantCard(AssistantCard.SNAKE));
        octopusBtn.setOnAction(e -> playAssistantCard(AssistantCard.OCTOPUS));
        dogBtn.setOnAction(e -> playAssistantCard(AssistantCard.DOG));
        elephantBtn.setOnAction(e -> playAssistantCard(AssistantCard.ELEPHANT));
        turtleBtn.setOnAction(e -> playAssistantCard(AssistantCard.TURTLE));

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

    }

    private ImagePath getImagePath(AssistantCard card) {
        switch (card) {
            case CHEETAH -> {
                return ImagePath.CHEETAH;
            }
            case OSTRICH -> {
                return ImagePath.OSTRICH;
            }
            case CAT -> {
                return ImagePath.CAT;
            }
            case EAGLE -> {
                return ImagePath.EAGLE;
            }
            case FOX -> {
                return ImagePath.FOX;
            }
            case SNAKE -> {
                return ImagePath.SNAKE;
            }
            case OCTOPUS -> {
                return ImagePath.OCTOPUS;
            }
            case DOG -> {
                return ImagePath.DOG;
            }
            case ELEPHANT -> {
                return ImagePath.ELEPHANT;
            }
            case TURTLE -> {
                return ImagePath.TURTLE;
            }

        }
        return null;
    }

    private static class AssistantView {
        Button assistantButton;
        ImageView assistantImage;

        AssistantView(Button button, ImageView imageView) {
            assistantButton = button;
            assistantImage = imageView;
        }
    }
}
