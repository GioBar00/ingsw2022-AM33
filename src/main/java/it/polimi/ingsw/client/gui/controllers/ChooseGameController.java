package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.network.messages.client.ChosenGame;
import it.polimi.ingsw.server.model.enums.GameMode;
import it.polimi.ingsw.server.model.enums.GamePreset;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;

public class ChooseGameController implements GUIController {
    @FXML
    public RadioButton radioBtn2;
    @FXML
    public RadioButton radioBtn3;
    @FXML
    public RadioButton radioBtn4;
    @FXML
    public CheckBox checkBoxExpert;
    @FXML
    public Button btnCreateGame;

    private GUI gui;


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
        radioBtn2.setSelected(true);
        radioBtn3.setSelected(false);
        radioBtn4.setSelected(false);
        checkBoxExpert.setSelected(false);
    }

    /**
     * This method is used to handle the event of clicking the button to create a new game.
     */
    @FXML
    public void handleCreateGame(ActionEvent actionEvent) {
        GamePreset preset = GamePreset.TWO;
        if (radioBtn3.isSelected()) {
            preset = GamePreset.THREE;
        } else if (radioBtn4.isSelected()) {
            preset = GamePreset.FOUR;
        }
        GameMode mode = checkBoxExpert.isSelected() ? GameMode.EXPERT : GameMode.EASY;
        gui.notifyViewListener(new ChosenGame(preset, mode));
        gui.getStage().getScene().getRoot().setDisable(false);
        ((Stage) btnCreateGame.getScene().getWindow()).close();
    }
}
