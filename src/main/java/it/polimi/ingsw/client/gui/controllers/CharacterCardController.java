package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.GUIUtils;
import it.polimi.ingsw.client.gui.ResourceLoader;
import it.polimi.ingsw.network.messages.views.CharacterCardView;
import it.polimi.ingsw.server.model.enums.StudentColor;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.util.EnumMap;


public class CharacterCardController implements GUIController {

    private GUI gui;
    private Pane root;
    private EnumMap<StudentColor, LabelButton> buttons;

    @FXML
    public AnchorPane anchorPaneCharacter;
    @FXML
    public AnchorPane anchorPanePawn;
    @FXML
    public ImageView characterImg;
    @FXML
    public Button characterBtn;
    @FXML
    public Label coinLbl;

    @FXML
    public Button greenBtn;
    @FXML
    public Label greenLbl;

    @FXML
    public Button redBtn;
    @FXML
    public ImageView redImg;
    @FXML
    public Label redLbl;

    @FXML
    public Button pinkBtn;
    @FXML
    public Label pinkLbl;

    @FXML
    public Button blueBtn;
    @FXML
    public Label blueLbl;

    @FXML
    public Label yellowLbl;

    @FXML
    public Button yellowBtn;

    @FXML
    public AnchorPane anchorPaneCoin;
    @FXML
    public ImageView coinImg;

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
     * Sets and shows the details on a character card.
     *
     * @param view {@link CharacterCardView} of the card.
     */
    public void setCharacterView(CharacterCardView view) {

        characterImg.setImage(GUIUtils.getCharacterImage(view.getType()));

        int cost = view.getAdditionalCost() + view.getOriginalCost();
        coinLbl.setText(String.valueOf(cost));

        if (view.canBeUsed())
            characterBtn.setDisable(false);

        if (view.getNumBlocks() > 0) {
            redImg.setImage(ResourceLoader.loadImage(ImagePath.PROHIBITION));
            redLbl.setText(String.valueOf(view.getNumBlocks()));
            return;
        }

        if (view.getStudent() != null) {
            Integer num;
            for (StudentColor s : StudentColor.values()) {
                num = view.getStudent().get(s);
                if (num == null)
                    setNotUsable(buttons.get(s));
                else setUsable(buttons.get(s), num);
            }
        }

    }

    /**
     * Private method that sets if a button is not usable.
     *
     * @param in {@link LabelButton}.
     */
    private void setNotUsable(LabelButton in) {
        in.button.setVisible(false);
        in.button.setDisable(true);
        in.label.setVisible(false);

    }

    /**
     * Private method that sets if a button is usable.
     *
     * @param in  {@link LabelButton}.
     * @param num the quantity related to the button.
     */
    private void setUsable(LabelButton in, int num) {
        in.button.setVisible(true);
        in.button.setDisable(true);
        in.label.setVisible(true);
        in.label.setText(String.valueOf(num));
    }

    /**
     * This method is used to initialize the controller and stage.
     */
    @Override
    public void init() {
        buttons = new EnumMap<>(StudentColor.class);
        buttons.put(StudentColor.GREEN, new LabelButton(greenLbl, greenBtn));
        buttons.put(StudentColor.RED, new LabelButton(redLbl, redBtn));
        buttons.put(StudentColor.BLUE, new LabelButton(blueLbl, blueBtn));
        buttons.put(StudentColor.MAGENTA, new LabelButton(pinkLbl, pinkBtn));
        buttons.put(StudentColor.YELLOW, new LabelButton(yellowLbl, yellowBtn));



        GUIUtils.bindSize(root, characterImg);
        GUIUtils.bindSize(root, characterBtn);

        GUIUtils.bindSize(anchorPaneCharacter,characterBtn);
        GUIUtils.bindSize(anchorPaneCharacter,characterImg);



        GUIUtils.bindSize(anchorPanePawn,blueBtn);
        GUIUtils.bindSize(blueBtn,blueLbl);
        GUIUtils.bindSize(anchorPanePawn,redBtn);
        GUIUtils.bindSize(redBtn,redLbl);
        GUIUtils.bindSize(anchorPanePawn,greenBtn);
        GUIUtils.bindSize(greenBtn,greenLbl);
        GUIUtils.bindSize(anchorPanePawn,pinkBtn);
        GUIUtils.bindSize(pinkBtn,pinkLbl);
        GUIUtils.bindSize(anchorPanePawn,yellowBtn);
        GUIUtils.bindSize(yellowBtn,yellowLbl);

        GUIUtils.bindSize(anchorPanePawn,anchorPaneCoin);
        GUIUtils.bindSize(anchorPanePawn,coinImg);

        anchorPaneCharacter.heightProperty().addListener((observable, oldValue, newValue) -> anchorPaneCharacter.setPrefWidth(newValue.doubleValue() / 1039 * 685));
        root.heightProperty().addListener((observable, oldValue, newValue) -> anchorPaneCharacter.setPrefHeight(newValue.doubleValue()));


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
     * Private class for group label and button related to the same color
     */
    private class LabelButton {
        Label label;
        Button button;

        LabelButton(Label label, Button button) {
            this.label = label;
            this.button = button;
        }
    }
}
