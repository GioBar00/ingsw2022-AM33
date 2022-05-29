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

    @FXML
    public ImageView imgViewGreen;
    @FXML
    public ImageView imgViewRed;
    @FXML
    public ImageView imgViewMagenta;
    @FXML
    public ImageView imgViewBlue;
    @FXML
    public ImageView imgViewYellow;
    @FXML
    public ImageView imgViewCoin;
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
            imgViewRed.setImage(ResourceLoader.loadImage(ImagePath.PROHIBITION));
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

        for(LabelButton lb : buttons.values()){
            setNotUsable(lb);
        }
        GUIUtils.bindSize(anchorPaneCharacter, characterBtn);
        GUIUtils.bindSize(anchorPaneCharacter, characterImg);

        GUIUtils.bindSize(anchorPanePawn, blueBtn);
        GUIUtils.bindSize(anchorPanePawn, blueLbl);
        GUIUtils.bindSize(anchorPanePawn, imgViewBlue);
        GUIUtils.bindSize(anchorPanePawn, redBtn);
        GUIUtils.bindSize(anchorPanePawn, redLbl);
        GUIUtils.bindSize(anchorPanePawn, imgViewRed);
        GUIUtils.bindSize(anchorPanePawn, greenBtn);
        GUIUtils.bindSize(anchorPanePawn, greenLbl);
        GUIUtils.bindSize(anchorPanePawn, imgViewGreen);
        GUIUtils.bindSize(anchorPanePawn, pinkBtn);
        GUIUtils.bindSize(anchorPanePawn, pinkLbl);
        GUIUtils.bindSize(anchorPanePawn, imgViewMagenta);
        GUIUtils.bindSize(anchorPanePawn, yellowBtn);
        GUIUtils.bindSize(anchorPanePawn, yellowLbl);
        GUIUtils.bindSize(anchorPanePawn, imgViewYellow);

        GUIUtils.bindSize(anchorPaneCoin, imgViewCoin);

        root.getChildren().clear();
        GUIUtils.addToPaneCenterKeepRatio(root, anchorPaneCharacter, 685.0 /1039);


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
