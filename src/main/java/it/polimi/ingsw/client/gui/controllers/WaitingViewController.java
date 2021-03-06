package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GUI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is the controller of the waiting screen.
 */
public class WaitingViewController implements GUIController {
    @FXML
    private Label timerLbl;

    /**
     * The root of the scene.
     */
    private Pane root;

    /**
     * boolean set to true if the timer has been activated;
     */
    private boolean activated = false;

    /**
     * The timer used for the countdown.
     */
    private Timer timer;

    private int currentTime = 0;

    /**
     * This method is used to set the GUI of the controller.
     *
     * @param gui the GUI of the controller.
     */
    @Override
    public void setGUI(GUI gui) {

    }

    /**
     * This method is used to initialize the controller and stage.
     */
    @Override
    public void init() {
        timer = new Timer();
    }

    /**
     * This method is used for setting and updating the timer inside the waiting screen.
     */
    public void startTimer(int startTime) {
        activated = true;
        timerLbl.setAlignment(javafx.geometry.Pos.CENTER);
        currentTime = startTime;
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                synchronized (this) {
                    if (currentTime > 0) {
                        Platform.runLater(() -> timerLbl.setText(String.valueOf(currentTime)));
                        currentTime--;
                    } else
                        timer.cancel();
                }
            }
        }, 1000, 1000);
    }

    /**
     * @return the current time of the timer.
     */
    public synchronized int getTimer() {
        return currentTime;
    }

    /**
     * This method is used to load the scene of the controller on the stage.
     *
     * @param stage the stage to load the scene on.
     */
    public void loadScene(Stage stage) {
        stage.setMinHeight(500.0);
        stage.setMinWidth(800.0);
        stage.setHeight(500.0);
        stage.setWidth(800.0);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);

        stage.setScene(new Scene(getRootPane()));
    }

    /**
     * This method is called when changing scene or closing the stage.
     */
    @Override
    public void unload() {
        currentTime = 0;
        if (activated) {
            timer.cancel();
            timer.purge();
            activated = false;
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
}
