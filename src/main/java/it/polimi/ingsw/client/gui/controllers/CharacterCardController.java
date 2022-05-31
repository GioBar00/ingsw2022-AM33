package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.GUIUtils;
import it.polimi.ingsw.client.gui.ResourceLoader;
import it.polimi.ingsw.network.messages.actions.ActivatedCharacterCard;
import it.polimi.ingsw.network.messages.actions.ConcludeCharacterCardEffect;
import it.polimi.ingsw.network.messages.views.CharacterCardView;
import it.polimi.ingsw.server.model.enums.StudentColor;
import javafx.fxml.FXML;
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
    @FXML
    public Button endEffectBtn;
    @FXML
    public ImageView endEffectImg;

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

    private GUI gui;
    private Pane root;
    EnumMap<StudentColor, Button> buttons;
    EnumMap<StudentColor, Label> labels;

    private Integer index;

    private boolean canEndEffect;

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


        GUIUtils.resetButton(characterBtn);

        canEndEffect = view.canEndEffect();

        characterImg.setImage(GUIUtils.getCharacterImage(view.getType()));

        endEffectBtn.setVisible(false);
        endEffectImg.setVisible(false);

        int cost = view.getAdditionalCost() + view.getOriginalCost();
        coinLbl.setText(String.valueOf(cost));
        coinLbl.setVisible(true);


        if (view.getNumBlocks() > 0) {
            imgViewRed.setImage(ResourceLoader.loadImage(ImagePath.BLOCK));
            redLbl.setText(String.valueOf(view.getNumBlocks()));
            redBtn.setVisible(true);
            redLbl.setVisible(true);
            return;
        }

        if (view.getStudent() != null) {
            Integer num;
            for (StudentColor s : StudentColor.values()) {
                num = view.getStudent().get(s);
                if (num == null || num == 0) {
                    setNotUsable(buttons.get(s), labels.get(s));
                    buttons.get(s).setOpacity(1);
                } else setUsable(buttons.get(s), labels.get(s), num);
            }
        }

    }

    /**
     * This method is used to set the character card as not usable.
     */
    public void resetCharacterButton() {
        GUIUtils.resetButton(characterBtn);
    }

    /**
     * Private method that sets if a button is not usable.
     *
     * @param in    {@link Button} the button we want to enable.
     * @param inLbl {@link Label} the label we want to enable.
     */
    private void setNotUsable(Button in, Label inLbl) {
        in.setVisible(false);
        inLbl.setVisible(false);
    }

    /**
     * Private method that sets if a button is usable.
     *
     * @param in    {@link Button} the button we want to enable.
     * @param inLbl {@link Label} the label we want to enable.
     * @param num   the quantity related to the button.
     */
    private void setUsable(Button in, Label inLbl, int num) {
        in.setVisible(true);
        in.setDisable(false);
        inLbl.setVisible(true);
        inLbl.setText(String.valueOf(num));
    }

    /**
     * This method activates the "conclude effect" button which sends a {@link ConcludeCharacterCardEffect}.
     */
    void activateEndButton() {
        if(canEndEffect) {
            endEffectImg.setVisible(true);
            endEffectBtn.setVisible(true);
            GUIUtils.setButton(endEffectBtn, e -> {
                gui.notifyViewListener(new ConcludeCharacterCardEffect());
                for (Button button : buttons.values()) {
                    GUIUtils.resetButton(button);
                }
            });
        }
    }

    /**
     * This method disables the "conclude effect" button which sends a {@link ConcludeCharacterCardEffect}.
     */
    void hideEndButton() {
        endEffectImg.setVisible(false);
        endEffectBtn.setVisible(false);
        GUIUtils.resetButton(endEffectBtn);
    }

    /**
     * This method is used to initialize the controller and stage.
     */
    @Override
    public void init() {
        buttons = new EnumMap<>(StudentColor.class);
        labels = new EnumMap<>(StudentColor.class);
        buttons.put(StudentColor.GREEN, greenBtn);
        labels.put(StudentColor.GREEN, greenLbl);
        buttons.put(StudentColor.RED, redBtn);
        labels.put(StudentColor.RED, redLbl);
        buttons.put(StudentColor.BLUE, blueBtn);
        labels.put(StudentColor.BLUE, blueLbl);
        buttons.put(StudentColor.MAGENTA, pinkBtn);
        labels.put(StudentColor.MAGENTA, pinkLbl);
        buttons.put(StudentColor.YELLOW, yellowBtn);
        labels.put(StudentColor.YELLOW, yellowLbl);


        for (StudentColor s : StudentColor.values()) {
            setNotUsable(buttons.get(s), labels.get(s));
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
        GUIUtils.bindSize(anchorPaneCoin, endEffectBtn);
        GUIUtils.bindSize(anchorPaneCoin, endEffectImg);

        root.getChildren().clear();
        GUIUtils.addToPaneCenterKeepRatio(root, anchorPaneCharacter, 685.0 / 1039);


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
     * This method sets the index of the card.
     *
     * @param index the index.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * This method returns the index of the card.
     *
     * @return the index of the card.
     */
    public Integer getIndex() {
        return index;
    }

}
