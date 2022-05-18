package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.UI;
import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.enums.SceneFXMLPath;
import it.polimi.ingsw.client.gui.controllers.GUIController;
import it.polimi.ingsw.network.listeners.ViewListener;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.network.messages.views.GameView;
import it.polimi.ingsw.network.messages.views.TeamsView;
import it.polimi.ingsw.network.messages.views.WizardsView;
import it.polimi.ingsw.server.model.enums.GameMode;
import it.polimi.ingsw.server.model.enums.GamePreset;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GUI extends Application implements UI {

    private static GUI instance;
    private static final CountDownLatch instantiationLatch = new CountDownLatch(1);
    private final EnumMap<SceneFXMLPath, Scene> sceneByPath = new EnumMap<>(SceneFXMLPath.class);
    public final static EnumMap<ImagePath, Image> imagesByPath = new EnumMap<>(ImagePath.class);

    private Stage stage;

    private Client client;

    private ViewListener listener;

    public GUI() {
        instance = this;
        instantiationLatch.countDown();
    }

    public synchronized static GUI getInstance() {
        try {
            if (!instantiationLatch.await(5, TimeUnit.SECONDS))
                return null;
        } catch (InterruptedException e) {
            return null;
        }
        return instance;
    }

    private Scene loadFXML(String path) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(path)));
            Scene scene = new Scene(loader.load());
            GUIController controller = loader.getController();
            scene.setUserData(controller);
            controller.setGUI(this);
            controller.init();
            return scene;
        } catch (IOException | NullPointerException e) {
            System.err.println("Error loading fxml: " + path);
            stop();
        }
        return null;
    }

    @Override
    public void init() {
        // load all fxml files
        for (SceneFXMLPath path : SceneFXMLPath.values())
            sceneByPath.put(path, loadFXML(path.getPath()));
        // load all images
        for (ImagePath path : ImagePath.values()) {
            try {
                imagesByPath.put(path, new Image(Objects.requireNonNull(getClass().getResource(path.getPath())).toExternalForm()));
            } catch (NullPointerException e) {
                System.err.println("Error loading image: " + path);
                stop();
            }
        }
        //TODO: load all fonts
        //TODO: load all sounds

    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Scene scene = sceneByPath.get(SceneFXMLPath.START_SCREEN);
        stage.setScene(scene);
        stage.setTitle("Eriantys");
        stage.setMinHeight(800.0);
        stage.setMinWidth(1200.0);
        stage.getIcons().add(imagesByPath.get(ImagePath.ICON));
        stage.setOnHiding(event -> stop());
        stage.show();
    }

    @Override
    public void stop() {
       System.exit(0);
    }

    @Override
    public void setClient(Client client) {
        System.out.println("Setting client");
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public Stage getStage() {
        return stage;
    }

    /**
     * @param wizardsView
     */
    @Override
    public void setWizardView(WizardsView wizardsView) {
        System.out.println("Setting wizard view");
    }

    /**
     * @param teamsView
     */
    @Override
    public void setTeamsView(TeamsView teamsView) {
        System.out.println("Setting teams view");
    }

    /**
     * @param gameView
     */
    @Override
    public void setGameView(GameView gameView) {
        System.out.println("Setting game view");
    }

    /**
     *
     */
    @Override
    public void chooseGame() {
        Platform.runLater(() -> {
            stage.getScene().getRoot().setDisable(true);
            Stage chooseGameStage = new Stage();
            chooseGameStage.setTitle("Create a new game");
            chooseGameStage.setScene(sceneByPath.get(SceneFXMLPath.CHOOSE_GAME));
            chooseGameStage.setMinHeight(150.0);
            chooseGameStage.setMinWidth(300.0);
            chooseGameStage.setResizable(false);
            chooseGameStage.showAndWait();
        });
    }

    /**
     *
     */
    @Override
    public void showStartScreen() {
        System.out.println("Showing start screen");
    }

    /**
     *
     */
    @Override
    public void showWizardMenu() {
        System.out.println("Showing wizard menu");
    }

    /**
     *
     */
    @Override
    public void showLobbyScreen() {
        System.out.println("Showing lobby screen");
    }

    /**
     *
     */
    @Override
    public void hostCanStart() {
        System.out.println("Host can start");
    }

    /**
     *
     */
    @Override
    public void hostCantStart() {
        System.out.println("Host cant start");
    }

    /**
     *
     */
    @Override
    public void showGameScreen() {
        System.out.println("Showing game screen");
    }

    /**
     * @param message
     */
    @Override
    public void setPossibleMoves(Message message) {
        System.out.println("Setting possible moves");
    }

    @Override
    public void serverUnavailable() {
        System.out.println("Server unavailable");
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
        System.out.println("Updating game view");
    }

    /**
     *
     */
    @Override
    public void updateLobbyView() {
        System.out.println("Updating lobby view");
    }

    /**
     * @param message
     */
    @Override
    public void showCommMessage(CommMessage message) {
        System.out.println("Showing comm message");
        System.out.println(MessageBuilder.toJson(message));
    }

    /**
     * Sets the view listener.
     *
     * @param listener the listener to set
     */
    @Override
    public void setViewListener(ViewListener listener) {
        this.listener = listener;
    }

    /**
     * Notifies the listener that a request has occurred.
     *
     * @param message the request to notify
     */
    @Override
    public void notifyViewListener(Message message) {
        listener.onMessage(message);
    }
}
