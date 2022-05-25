package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.UI;
import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.enums.FXMLPath;
import it.polimi.ingsw.client.enums.ViewState;
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
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.MissingResourceException;

public class GUI extends Application implements UI {
    private Stage stage;

    private Client client;

    private ViewListener listener;
    private Message lastRequest;

    private String nickname;

    private ChooseWizardController chooseWizardController;

    private TeamLobbyController teamLobbyController;

    private ViewState viewState = ViewState.SETUP;

    @Override
    public void init() {
        try {
            ResourceLoader.checkResources();
        } catch (MissingResourceException e) {
            System.err.println(e.getMessage());
            stop();
        }
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Eriantys");
        stage.setMinHeight(800.0);
        stage.setMinWidth(1200.0);
        stage.getIcons().add(ResourceLoader.loadImage(ImagePath.ICON));
        stage.setOnHiding(event -> stop());

        client = new Client(this);
        setViewListener(client);
        client.startClient();
    }

    @Override
    public void stop() {
       System.exit(0);
    }

    public Client getClient() {
        return client;
    }

    public Stage getStage() {
        return stage;
    }

    /**
     * This method checks if the wizard controller is already loaded.
     */
    private void checkChooseWizardController() {
        if (chooseWizardController == null) {
            chooseWizardController = (ChooseWizardController) ResourceLoader.loadFXML(FXMLPath.CHOOSE_WIZARD, this);
            Platform.runLater(chooseWizardController::init);
        }
    }

    /**
     * This method checks if the lobby controller is already loaded.
     */
    private void checkTeamLobbyController() {
        if (teamLobbyController == null) {
            teamLobbyController = (TeamLobbyController) ResourceLoader.loadFXML(FXMLPath.TEAM_LOBBY, this);
            Platform.runLater(teamLobbyController::init);
        }
    }

    /**
     * @param wizardsView
     */
    @Override
    public void setWizardView(WizardsView wizardsView) {
        checkChooseWizardController();
        Platform.runLater(() -> chooseWizardController.updateWizards(wizardsView));
    }

    /**
     * @param teamsView
     */
    @Override
    public void setTeamsView(TeamsView teamsView) {
        boolean show = viewState == ViewState.CHOOSE_TEAM && teamLobbyController == null;
        checkTeamLobbyController();
        Platform.runLater(() -> teamLobbyController.updateTeams(teamsView));
        if (show)
            showLobbyScreen();
    }

    /**
     * @param gameView
     */
    @Override
    public void setGameView(GameView gameView) {
        /*
        for (PlayerView pv : gameView.getPlayersView()) {
            if (pv.getNickname().equals(nickname)){
                if(pv.getPlayedCard() != null){
                    ((AssistantCardController)sceneByPath.get(FXMLPath.CHOOSE_ASSISTANT).getUserData()).setPlayedCard(pv.getPlayedCard());
                    break;
                }
            }
        }

         */
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
            chooseGameStage.setScene(new Scene(ResourceLoader.loadFXML(FXMLPath.CHOOSE_GAME, this).getParent()));
            chooseGameStage.setMinHeight(400.0);
            chooseGameStage.setMinWidth(600.0);
            chooseGameStage.getIcons().add(ResourceLoader.loadImage(ImagePath.ICON));
            chooseGameStage.setResizable(false);
            chooseGameStage.show();
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
                GUIController controller = ResourceLoader.loadFXML(FXMLPath.START_SCREEN, this);
                controller.init();
                stage.setScene(new Scene(controller.getParent()));
                if (!stage.isShowing())
                    stage.show();
            });
    }

    /**
     *
     */
    @Override
    public void showWizardMenu() {
        System.out.println("Showing wizard menu");
        checkChooseWizardController();
        Platform.runLater(() -> {
            stage.getScene().getRoot().setDisable(true);
            Stage chooseWizardStage = new Stage();
            chooseWizardStage.setTitle("Choose a Wizard");
            chooseWizardStage.setScene(new Scene(chooseWizardController.getParent()));
            chooseWizardStage.setMinHeight(150.0);
            chooseWizardStage.setMinWidth(300.0);
            chooseWizardStage.getIcons().add(ResourceLoader.loadImage(ImagePath.ICON));
            chooseWizardStage.setResizable(false);
            chooseWizardStage.onHidingProperty().set(event -> {
                chooseWizardController = null;
                viewState = ViewState.CHOOSE_TEAM;
                if (teamLobbyController != null)
                    showLobbyScreen();
            });
            chooseWizardStage.show();
        });
    }

    /**
     *
     */
    @Override
    public void showLobbyScreen() {
        checkTeamLobbyController();
        Platform.runLater(() -> {
            stage.setScene(new Scene(teamLobbyController.getParent()));
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
        if(teamLobbyController != null) {
            Platform.runLater(() -> {
                teamLobbyController.setCanStart();
                // FIXME: ??????
                /*
                stage.setScene(sceneByPath.get(FXMLPath.TEAM_LOBBY));
                stage.setResizable(false);
                stage.setTitle("Eriantys");
                stage.getIcons().add(imagesByPath.get(ImagePath.ICON));
                stage.setOnHiding(event -> stop());
                stage.show();
                 */
            });
        }
    }

    /**
     *
     */
    @Override
    public void hostCantStart() {
        if(teamLobbyController != null){
            Platform.runLater(() -> {
                teamLobbyController.setCantStart();
                // FIXME: ??????
                /*
                stage.setScene(sceneByPath.get(FXMLPath.TEAM_LOBBY));
                stage.setResizable(false);
                stage.getIcons().add(imagesByPath.get(ImagePath.ICON));
                stage.setOnHiding(event -> stop());
                stage.show();
                 */
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
        /*
        switch (MessageType.retrieveByMessage(message)){
            case PLAY_ASSISTANT_CARD -> {
                ((AssistantCardController)sceneByPath.get(FXMLPath.CHOOSE_ASSISTANT).getUserData()).setPlayable(((PlayAssistantCard)message).getPlayableAssistantCards());
            }
        }

         */
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
