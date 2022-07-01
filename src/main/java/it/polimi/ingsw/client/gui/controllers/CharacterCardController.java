package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.GUIUtils;
import it.polimi.ingsw.client.gui.ResourceLoader;
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

/**
 * This class is the controller of the character card view.
 */
public class CharacterCardController implements GUIController {

    /**
     * {@link GUI} instance.
     */
    private GUI gui;

    /**
     * The root of the scene.
     */
    private Pane root;

    /**
     * A map of the student color buttons on the card.
     */
    EnumMap<StudentColor, Button> buttons;

    /**
     * The index of the character card.
     */
    private Integer index;

    /**
     * Boolean set to true if the effect of the character card could be concluded.
     */
    private boolean canEndEffect;
    @FXML
    private ImageView imgViewGreen;
    @FXML
    private ImageView imgViewRed;
    @FXML
    private ImageView imgViewMagenta;
    @FXML
    private ImageView imgViewBlue;
    @FXML
    private ImageView imgViewYellow;
    @FXML
    private ImageView imgViewCoin;
    @FXML
    private Button endEffectBtn;
    @FXML
    private ImageView endEffectImg;

    @FXML
    private AnchorPane anchorPaneCharacter;
    @FXML
    private AnchorPane anchorPanePawnGreen;
    @FXML
    private AnchorPane anchorPanePawnRed;
    @FXML
    private AnchorPane anchorPanePawnMagenta;
    @FXML
    private AnchorPane anchorPanePawnBlue;
    @FXML
    private AnchorPane anchorPanePawnYellow;
    @FXML
    private ImageView characterImg;
    /**
     * Button for clicking on the card in the game-screen
     */
    @FXML
    public Button characterBtn;
    @FXML
    private Label coinLbl;

    @FXML
    private Button greenBtn;

    @FXML
    private Button redBtn;

    @FXML
    private Button magentaBtn;

    @FXML
    private Button blueBtn;

    @FXML
    private Button yellowBtn;

    @FXML
    private AnchorPane anchorPaneCoin;
    @FXML
    private AnchorPane anchorPaneEndEffect;


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
        buttons = new EnumMap<>(StudentColor.class);
        buttons.put(StudentColor.GREEN, greenBtn);
        buttons.put(StudentColor.RED, redBtn);
        buttons.put(StudentColor.BLUE, blueBtn);
        buttons.put(StudentColor.MAGENTA, magentaBtn);
        buttons.put(StudentColor.YELLOW, yellowBtn);

        GUIUtils.bindSize(anchorPaneCharacter, characterBtn);
        GUIUtils.bindSize(anchorPaneCharacter, characterImg);

        GUIUtils.addToPaneCenterKeepRatio(anchorPanePawnBlue, blueBtn, 1.0);
        GUIUtils.bindSize(blueBtn, imgViewBlue);
        GUIUtils.addToPaneCenterKeepRatio(anchorPanePawnGreen, greenBtn, 1.0);
        GUIUtils.bindSize(greenBtn, imgViewGreen);
        GUIUtils.addToPaneCenterKeepRatio(anchorPanePawnRed, redBtn, 1.0);
        GUIUtils.bindSize(redBtn, imgViewRed);
        GUIUtils.addToPaneCenterKeepRatio(anchorPanePawnYellow, yellowBtn, 1.0);
        GUIUtils.bindSize(yellowBtn, imgViewYellow);
        GUIUtils.addToPaneCenterKeepRatio(anchorPanePawnMagenta, magentaBtn, 1.0);
        GUIUtils.bindSize(magentaBtn, imgViewMagenta);

        GUIUtils.bindSize(anchorPaneCoin, imgViewCoin);
        GUIUtils.bindSize(anchorPaneEndEffect, endEffectBtn);
        GUIUtils.bindSize(anchorPaneEndEffect, endEffectImg);

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
            redBtn.setText(String.valueOf(view.getNumBlocks()));
            redBtn.setVisible(true);
            return;
        }

        if (view.getStudent() != null) {
            Integer num;
            for (StudentColor s : StudentColor.values()) {
                num = view.getStudent().get(s);
                if (num == null || num == 0) {
                    buttons.get(s).setVisible(false);
                    buttons.get(s).setOpacity(1);
                } else setUsable(buttons.get(s), num);
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
     * Private method that sets if a button is usable.
     *
     * @param btn    {@link Button} the button we want to enable.
     * @param num   the quantity related to the button.
     */
    private void setUsable(Button btn, int num) {
        btn.setVisible(true);
        btn.setDisable(false);
        btn.setText(String.valueOf(num));
    }

    /**
     * This method activates the "conclude effect" button which sends a {@link ConcludeCharacterCardEffect}.
     */
    void activateEndButton() {
        if (canEndEffect) {
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

}
