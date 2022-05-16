package it.polimi.ingsw.client.GUI;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.UI;
import it.polimi.ingsw.network.listeners.ViewListener;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.network.messages.views.GameView;
import it.polimi.ingsw.network.messages.views.TeamsView;
import it.polimi.ingsw.network.messages.views.WizardsView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class GUI extends Application implements UI {

    private static GUI instance;
    private static final CountDownLatch instantiationLatch = new CountDownLatch(1);

    public GUI() {
        instance = this;
        instantiationLatch.countDown();
    }

    public synchronized static GUI getInstance() {
        try {
            instantiationLatch.await();
        } catch (InterruptedException e) {
            return null;
        }
        return instance;
    }

    @Override
    public void start(Stage stage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/start-screen.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            System.out.println("Error loading start screen");
            System.exit(1);
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Eriantys");
        stage.show();
    }

    @Override
    public void stop() {
       System.exit(0);
    }

    @Override
    public void setClient(Client client) {

    }

    /**
     * @param wizardsView
     */
    @Override
    public void setWizardView(WizardsView wizardsView) {

    }

    /**
     * @param teamsView
     */
    @Override
    public void setTeamsView(TeamsView teamsView) {

    }

    /**
     * @param gameView
     */
    @Override
    public void setGameView(GameView gameView) {

    }

    /**
     *
     */
    @Override
    public void chooseGame() {

    }

    /**
     *
     */
    @Override
    public void showStartScreen() {

    }

    /**
     *
     */
    @Override
    public void showWizardMenu() {

    }

    /**
     *
     */
    @Override
    public void showLobbyScreen() {

    }

    /**
     *
     */
    @Override
    public void hostCanStart() {

    }

    /**
     *
     */
    @Override
    public void hostCantStart() {

    }

    /**
     *
     */
    @Override
    public void showGameScreen() {

    }

    /**
     * @param message
     */
    @Override
    public void setPossibleMoves(Message message) {

    }

    @Override
    public void serverUnavailable() {

    }

    /**
     *
     */
    @Override
    public void close() {
        stop();
    }

    /**
     *
     */
    @Override
    public void updateGameView() {

    }

    /**
     *
     */
    @Override
    public void updateLobbyView() {

    }

    /**
     * @param message
     */
    @Override
    public void showCommMessage(CommMessage message) {

    }

    /**
     * Sets the view listener.
     *
     * @param listener the listener to set
     */
    @Override
    public void setViewListener(ViewListener listener) {

    }

    /**
     * Notifies the listener that a request has occurred.
     *
     * @param event the request to notify
     */
    @Override
    public void notifyListener(Message event) {

    }
}
