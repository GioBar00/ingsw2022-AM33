package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.enums.FXMLPath;
import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.GUIUtils;
import it.polimi.ingsw.client.gui.ResourceLoader;
import it.polimi.ingsw.network.messages.views.PlayerView;
import it.polimi.ingsw.server.model.enums.AssistantCard;
import it.polimi.ingsw.util.Function;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.util.EnumSet;

public class PlayerController implements GUIController {

    @FXML
    public AnchorPane anchorPaneSchoolBoard;

    @FXML
    public Label lblName;

    @FXML
    public ImageView imgViewTower;

    @FXML
    public Label lblCoins;

    @FXML
    public ImageView imgViewPlayedCard;

    @FXML
    public Button btnHand;

    @FXML
    public ImageView imgViewCoin;

    @FXML
    public AnchorPane anchorPaneHand;

    @FXML
    public GridPane gridPlayerInfo;
    @FXML
    public GridPane gridName;
    @FXML
    public HBox hBoxPlayedAssistantCard;
    @FXML
    public GridPane gridCards;
    @FXML
    public ImageView imgViewHand;
    @FXML
    public HBox hBoxTower;

    private GUI gui;

    private Pane root;

    private SchoolBoardController schoolBoardController;

    private PlayerView playerView;

    public AssistantCardController assistantCardController;

    private GUIController handStageHandler;

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
        lblCoins.setVisible(false);
        imgViewCoin.setVisible(false);
        imgViewPlayedCard.setVisible(true);

        GUIUtils.bindSize(hBoxTower, imgViewTower);
        GUIUtils.bindSize(anchorPaneHand, imgViewHand);
        GUIUtils.bindSize(anchorPaneHand, btnHand);
        GUIUtils.bindSize(hBoxPlayedAssistantCard, imgViewPlayedCard);
    }

    public void setShowHand(Function function) {
        btnHand.setOnAction(event -> function.apply());
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
     * This method updates the controller with the new player view.
     *
     * @param playerView the new player view.
     * @param coins      the coins of the player.
     */
    public void updatePlayerView(PlayerView playerView, Integer coins) {
        this.playerView = playerView;
        lblName.setText(playerView.getNickname());
        imgViewTower.setImage(GUIUtils.getTowerImage(playerView.getSchoolBoardView().getTower()));
        btnHand.setText(String.valueOf(playerView.getNumAssistantCards()));
        imgViewHand.setImage(GUIUtils.getWizardImage(playerView.getWizard()));

        if (coins != null) {
            lblCoins.setVisible(true);
            lblCoins.setText(String.valueOf(coins));
            imgViewCoin.setVisible(true);
        }
        if (playerView.getPlayedCard() != null) {
            imgViewPlayedCard.setImage(GUIUtils.getAssistantCard(playerView.getPlayedCard()));
            imgViewPlayedCard.setVisible(true);
        } else imgViewPlayedCard.setVisible(false);

        if (schoolBoardController == null) {
            instantiateSchoolBoardController();
            GUIUtils.bindSize(anchorPaneSchoolBoard, schoolBoardController.getRootPane());
            anchorPaneSchoolBoard.getChildren().add(schoolBoardController.getRootPane());
        }
        schoolBoardController.setSchoolBoardView(playerView.getSchoolBoardView());
    }

    /**
     * This method instantiates the school board controller.
     */
    private void instantiateSchoolBoardController() {
        schoolBoardController = (SchoolBoardController) ResourceLoader.loadFXML(FXMLPath.SCHOOL_BOARD, gui);
        schoolBoardController.init();
    }

    /**
     * This method moves the school board to a new container.
     *
     * @param newContainer the new container.
     */
    public void moveSchoolBoard(AnchorPane newContainer) {
        if (schoolBoardController == null)
            instantiateSchoolBoardController();
        anchorPaneSchoolBoard.getChildren().clear();
        GUIUtils.addToAnchorPane(newContainer, schoolBoardController.getRootPane());
    }

    /**
     * This method moves the player info to a new container.
     *
     * @param newContainer the new container.
     */
    public void movePlayerInfo(AnchorPane newContainer) {
        gridPlayerInfo.getChildren().clear();
        GridPane gridPane = new GridPane();
        ColumnConstraints col0 = new ColumnConstraints();
        col0.setPercentWidth(50);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        gridPane.getColumnConstraints().addAll(col0, col1);
        RowConstraints row0 = new RowConstraints();
        row0.setPercentHeight(100);
        gridPane.getRowConstraints().add(row0);
        gridPane.add(gridName, 0, 0);
        gridPane.add(gridCards, 1, 0);
        GUIUtils.addToAnchorPane(newContainer, gridPane);
    }

    /**
     * This method enables the player to view his hand.
     *
     * @param handStageHandler the handler of the stage.
     */
    public void enableHand(GUIController handStageHandler) {
        this.handStageHandler = handStageHandler;
        btnHand.setOnAction(event -> showAssistantCards());
    }

    /**
     * This method shows the assistant cards of the player.
     */
    private void showAssistantCards() {
        if (playerView == null)
            return;
        EnumSet<AssistantCard> cards = EnumSet.noneOf(AssistantCard.class);
        cards.addAll(playerView.getAssistantCards());
        AssistantCardController controller = (AssistantCardController) ResourceLoader.loadFXML(FXMLPath.CHOOSE_ASSISTANT, gui);
        assistantCardController = controller;
        controller.init();
        controller.setAvailableCards(cards);
        controller.showAssistantCards();
        Stage stage = new Stage();
        stage.setTitle("Your hand");
        stage.getIcons().add(ResourceLoader.loadImage(ImagePath.ICON));
        controller.loadScene(stage);
        stage.setAlwaysOnTop(true);
        handStageHandler.showNewDisablingStage(stage);
    }
}
