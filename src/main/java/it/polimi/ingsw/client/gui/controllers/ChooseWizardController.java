package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.network.messages.client.ChosenWizard;
import it.polimi.ingsw.network.messages.views.WizardsView;
import it.polimi.ingsw.server.model.enums.Wizard;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ChooseWizardController implements GUIController {

    @FXML
    public Button merlinBtn;

    @FXML
    public Button witchBtn;

    @FXML
    public Button kingBtn;

    @FXML
    public Button senseiBtn;

    private GUI gui;
    private Parent root;

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
     * This method is used to load the scene of the controller on the stage.
     *
     * @param stage the stage to load the scene on.
     */
    @Override
    public void loadScene(Stage stage) {
        stage.setScene(new Scene(getParent()));
        stage.setMinHeight(150.0);
        stage.setMinWidth(300.0);
        stage.setResizable(false);
    }

    /**
     * This method is used to handle the event of clicking the Merlin button.
     */
    @FXML
    public void selectedMerlin() {
        gui.notifyViewListener(new ChosenWizard(Wizard.MERLIN));
        closeScene(merlinBtn);
    }

    /**
     * This method is used to handle the event of clicking the Witch button.
     */
    @FXML
    public void selectedWitch() {
        gui.notifyViewListener(new ChosenWizard(Wizard.WITCH));
        closeScene(witchBtn);
    }

    /**
     * This method is used to handle the event of clicking the King button.
     */
    @FXML
    public void selectedKing() {
        gui.notifyViewListener(new ChosenWizard(Wizard.KING));
        closeScene(kingBtn);
    }

    /**
     * This method is used to handle the event of clicking the Sensei button.
     */
    @FXML
    public void selectedSensei() {
        gui.notifyViewListener(new ChosenWizard(Wizard.SENSEI));
        closeScene(senseiBtn);
    }


    /**
     * Set which button could be clicked.
     *
     * @param wizardsView for set the available buttons
     */
    public void updateWizards(WizardsView wizardsView) {
        Button btn;
        for (Wizard w : Wizard.values()) {
            btn = getButtonFromWizard(w);
            assert btn != null;
            btn.setDisable(true);
            btn.setVisible(false);
        }
        for (Wizard w : wizardsView.getAvailableWizards()) {
            btn = getButtonFromWizard(w);
            assert btn != null;
            btn.setDisable(false);
            btn.setVisible(true);
        }
    }

    /**
     * Close the Wizard scene
     *
     * @param button of the scene that has to be closed
     */
    private void closeScene(Button button) {
        gui.getStage().getScene().getRoot().setDisable(false);
        ((Stage) button.getScene().getWindow()).close();
    }

    /**
     * Return the button related to a specified wizard
     */
    private Button getButtonFromWizard(Wizard wizard) {
        switch (wizard) {
            case SENSEI -> {
                return senseiBtn;
            }
            case WITCH -> {
                return witchBtn;
            }
            case MERLIN -> {
                return merlinBtn;
            }
            case KING -> {
                return kingBtn;
            }
        }
        return null;
    }
}
