package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.network.messages.client.ChosenWizard;
import it.polimi.ingsw.network.messages.views.WizardsView;
import it.polimi.ingsw.server.model.enums.Wizard;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class ChooseWizardController implements GUIController {

    @FXML
    public Button merlinBtn;

    @FXML
    public Button witchBtn;

    @FXML
    public Button kingBtn;

    @FXML
    public Button senseiBtn;

    private final Map<Button, Wizard> wizardByButton = new HashMap<>(4);

    private GUI gui;
    private Pane root;

    private boolean choseWizard = false;

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
        wizardByButton.put(merlinBtn, Wizard.MERLIN);
        wizardByButton.put(witchBtn, Wizard.WITCH);
        wizardByButton.put(kingBtn, Wizard.KING);
        wizardByButton.put(senseiBtn, Wizard.SENSEI);
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
     * This method is used to load the scene of the controller on the stage.
     *
     * @param stage the stage to load the scene on.
     */
    @Override
    public void loadScene(Stage stage) {
        stage.setScene(new Scene(getRootPane()));
        stage.setMinHeight(150.0);
        stage.setMinWidth(300.0);
        stage.setResizable(false);
    }

    /**
     * @return if the player has chosen a wizard
     */
    public boolean hasChosenWizard() {
        return choseWizard;
    }

    /**
     * This method is used to handle the event of clicking the Merlin button.
     */
    @FXML
    public void handleButtonAction(ActionEvent actionEvent) {
        choseWizard = true;
        Button btn = (Button) actionEvent.getSource();
        gui.notifyViewListener(new ChosenWizard(wizardByButton.get(btn)));
        closeScene();
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
            btn.setDisable(true);
            btn.setVisible(false);
        }
        for (Wizard w : wizardsView.getAvailableWizards()) {
            btn = getButtonFromWizard(w);
            btn.setDisable(false);
            btn.setVisible(true);
        }
    }

    /**
     * Close the Wizard scene
     */
    private void closeScene() {
        ((Stage) root.getScene().getWindow()).close();
    }

    /**
     * Return the button related to a specified wizard
     */
    private Button getButtonFromWizard(Wizard wizard) {
        return switch (wizard) {
            case MERLIN -> merlinBtn;
            case WITCH -> witchBtn;
            case KING -> kingBtn;
            case SENSEI -> senseiBtn;
        };
    }
}
