package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.ResourceLoader;
import it.polimi.ingsw.network.messages.actions.ChosenStudentColor;
import it.polimi.ingsw.server.model.enums.StudentColor;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.*;

/**
 * This class is the controller of the "choose color" view.
 */
public class ChooseColorController implements GUIController {

    /**
     * {@link GUI} instance
     */
    private GUI gui;

    /**
     * The root of the scene.
     */
    private Pane root;
    @FXML
    private Button blueBtn;

    @FXML
    private Button greenBtn;

    @FXML
    private Button magentaBtn;

    @FXML
    private Button redBtn;

    @FXML
    private Button yellowBtn;

    /**
     * This map keeps track of which button corresponds to each StudentColor
     */
    private Map<StudentColor, Button> buttonsMap;

    /**
     * This boolean indicates if the player has choose a color
     */
    private boolean hasChoose;

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
        hasChoose = false;
        buttonsMap = new HashMap<>();
        blueBtn.setOnAction(buildAction(StudentColor.BLUE));
        redBtn.setOnAction(buildAction(StudentColor.RED));
        yellowBtn.setOnAction(buildAction(StudentColor.YELLOW));
        magentaBtn.setOnAction(buildAction(StudentColor.MAGENTA));
        greenBtn.setOnAction(buildAction(StudentColor.GREEN));

        buttonsMap.put(StudentColor.BLUE, blueBtn);
        buttonsMap.put(StudentColor.RED, redBtn);
        buttonsMap.put(StudentColor.YELLOW, yellowBtn);
        buttonsMap.put(StudentColor.MAGENTA, magentaBtn);
        buttonsMap.put(StudentColor.GREEN, greenBtn);


    }

    /**
     * This method build the on action event.
     * The event notifies the GUI with a personalized {@link ChosenStudentColor} message.
     *
     * @param s the student color.
     * @return an EventHandler
     */
    private EventHandler<ActionEvent> buildAction(StudentColor s) {
        return actionEvent -> {
            hasChoose = true;
            gui.notifyViewListener(new ChosenStudentColor(s));
            ((Stage) root.getScene().getWindow()).close();

        };
    }

    /**
     * This method set the available buttons.
     *
     * @param availableColors a list of available colors.
     */
    public void setAvailableButtons(EnumSet<StudentColor> availableColors) {
        Button btn;
        ArrayList<StudentColor> stdNotAvailable = new ArrayList<>();
        Collections.addAll(stdNotAvailable, StudentColor.values());

        for (StudentColor s : availableColors) {
            btn = buttonsMap.get(s);
            btn.setVisible(true);
            btn.setDisable(false);
            btn.setOpacity(1);
            stdNotAvailable.remove(s);
        }

        for (StudentColor s : stdNotAvailable) {
            btn = buttonsMap.get(s);
            btn.setVisible(false);
            btn.setDisable(true);
            btn.setOpacity(0.25);
        }
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
        stage.setScene(new Scene(root));
        stage.setTitle("Choose a color");
        stage.getIcons().add(ResourceLoader.loadImage(ImagePath.ICON));
        stage.setHeight(250);
        stage.setWidth(500);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
    }

    /**
     * Returns if the player has chosen a color
     *
     * @return true if the player has chosen a color, false otherwise.
     */
    public boolean isHasChoose() {
        return hasChoose;
    }

    /**
     * This method closes the window.
     */
    public void close() {
        Platform.runLater(() -> ((Stage) root.getScene().getWindow()).close());
    }
}
