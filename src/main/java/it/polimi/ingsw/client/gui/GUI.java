package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.UI;
import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.enums.SceneFXMLPath;
import it.polimi.ingsw.client.gui.controllers.AssistantCardController;
import it.polimi.ingsw.client.gui.controllers.ChooseWizardController;
import it.polimi.ingsw.client.gui.controllers.GUIController;
import it.polimi.ingsw.client.gui.controllers.TeamLobbyController;
import it.polimi.ingsw.network.listeners.ViewListener;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageBuilder;
import it.polimi.ingsw.network.messages.actions.requests.PlayAssistantCard;
import it.polimi.ingsw.network.messages.enums.MessageType;
import it.polimi.ingsw.network.messages.server.CommMessage;
import it.polimi.ingsw.network.messages.views.GameView;
import it.polimi.ingsw.network.messages.views.PlayerView;
import it.polimi.ingsw.network.messages.views.TeamsView;
import it.polimi.ingsw.network.messages.views.WizardsView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
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

    private TeamsView teamsView;
    private ViewListener listener;
    private Message lastRequest;

    private String nickname;

    public GUI() {
        instance = this;
        instantiationLatch.countDown();
    }

    public static GUI getInstance() {
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
        showStartScreen();
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
        Platform.runLater(() -> ((ChooseWizardController)sceneByPath.get(SceneFXMLPath.CHOOSE_WIZARD).getUserData()).setClickableButtons(wizardsView));
    }

    /**
     * @param teamsView
     */
    @Override
    public void setTeamsView(TeamsView teamsView) {
        this.teamsView = teamsView;
        showLobbyScreen();
    }

    /**
     * @param gameView
     */
    @Override
    public void setGameView(GameView gameView) {
        for (PlayerView pv : gameView.getPlayersView()) {
            if (pv.getNickname().equals(nickname)){
                if(pv.getPlayedCard() != null){
                    ((AssistantCardController)sceneByPath.get(SceneFXMLPath.CHOOSE_ASSISTANT).getUserData()).setPlayedCard(pv.getPlayedCard());
                    break;
                }
            }
        }
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
            chooseGameStage.setMinHeight(400.0);
            chooseGameStage.setMinWidth(600.0);
            chooseGameStage.getIcons().add(imagesByPath.get(ImagePath.ICON));
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
        if (stage != null)
            Platform.runLater(() -> {
                Scene scene = sceneByPath.get(SceneFXMLPath.START_SCREEN);
                stage.setScene(scene);
            });

    }

    /**
     *
     */
    @Override
    public void showWizardMenu() {
        System.out.println("Showing wizard menu");
        Platform.runLater(() -> {
            stage.getScene().getRoot().setDisable(true);
            Stage chooseWizardStage = new Stage();
            chooseWizardStage.setTitle("Choose a Wizard");
            chooseWizardStage.setScene(sceneByPath.get(SceneFXMLPath.CHOOSE_WIZARD));
            chooseWizardStage.setMinHeight(150.0);
            chooseWizardStage.setMinWidth(300.0);
            chooseWizardStage.getIcons().add(imagesByPath.get(ImagePath.ICON));
            chooseWizardStage.setResizable(false);
            chooseWizardStage.showAndWait();
        });
    }

    /**
     *
     */
    @Override
    public void showLobbyScreen() {
        Platform.runLater(() -> {
            ((TeamLobbyController)sceneByPath.get(SceneFXMLPath.TEAM_LOBBY).getUserData()).setLabels(teamsView);
            stage.setScene(sceneByPath.get(SceneFXMLPath.TEAM_LOBBY));
            stage.setMinHeight(500.0);
            stage.setMinWidth(680.0);
            stage.setResizable(false);
        });
    }

    /**
     *
     */
    @Override
    public void hostCanStart() {
        if(teamsView != null){
            Platform.runLater(() -> {
                ((TeamLobbyController)sceneByPath.get(SceneFXMLPath.TEAM_LOBBY).getUserData()).setCanStart();
                stage.setScene(sceneByPath.get(SceneFXMLPath.TEAM_LOBBY));
                stage.setResizable(false);
                stage.setTitle("Eriantys");
                stage.getIcons().add(imagesByPath.get(ImagePath.ICON));
                stage.setOnHiding(event -> stop());
                stage.show();
            });
        }
    }

    /**
     *
     */
    @Override
    public void hostCantStart() {
        if(teamsView != null){
            Platform.runLater(() -> {
                ((TeamLobbyController)sceneByPath.get(SceneFXMLPath.TEAM_LOBBY).getUserData()).setCantStart();
                stage.setScene(sceneByPath.get(SceneFXMLPath.TEAM_LOBBY));
                stage.setResizable(false);
                stage.getIcons().add(imagesByPath.get(ImagePath.ICON));
                stage.setOnHiding(event -> stop());
                stage.show();
            });
        }
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
        lastRequest = message;
        processLastRequest(lastRequest);
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    private void processLastRequest(Message message) {
        switch (MessageType.retrieveByMessage(message)){
            case PLAY_ASSISTANT_CARD -> {
                ((AssistantCardController)sceneByPath.get(SceneFXMLPath.CHOOSE_ASSISTANT).getUserData()).setPlayable(((PlayAssistantCard)message).getPlayableAssistantCards());
            }
        }
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
