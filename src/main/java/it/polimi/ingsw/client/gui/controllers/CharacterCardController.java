package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;


public class CharacterCardController implements GUIController {

    private GUI gui;

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

    }
}
