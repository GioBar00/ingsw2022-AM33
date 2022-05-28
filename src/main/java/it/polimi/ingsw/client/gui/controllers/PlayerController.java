package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.enums.FXMLPath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.GUIUtils;
import it.polimi.ingsw.client.gui.ResourceLoader;
import it.polimi.ingsw.network.messages.views.PlayerView;
import it.polimi.ingsw.util.Function;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class PlayerController implements GUIController {

    @FXML
    public AnchorPane schoolBoardAnc;

    @FXML
    public Label nameLbl;

    @FXML
    public ImageView towerImg;

    @FXML
    public Label coinLbl;

    @FXML
    public ImageView playedImg;

    @FXML
    public Button handBtn;

    @FXML
    public ImageView coinImg;

    @FXML
    public AnchorPane anchorPaneHand;

    @FXML
    public AnchorPane anchorPaneCoin;

    private GUI gui;

    private Pane root;

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
        coinLbl.setVisible(false);
        coinImg.setVisible(false);
        playedImg.setVisible(true);
        handBtn.setVisible(false);
        handBtn.setDisable(true);

        GUIUtils.bindSize(anchorPaneCoin, coinImg);
        GUIUtils.bindSize(anchorPaneCoin, towerImg);
        GUIUtils.bindSize(anchorPaneHand, playedImg);
        GUIUtils.bindSize(anchorPaneHand, handBtn);
        anchorPaneCoin.heightProperty().addListener((observable, oldValue, newValue) -> anchorPaneCoin.setPrefWidth(newValue.doubleValue()));
        root.heightProperty().addListener((observable, oldValue, newValue) -> anchorPaneCoin.setPrefHeight(newValue.doubleValue()));

        anchorPaneHand.heightProperty().addListener((observable, oldValue, newValue) -> anchorPaneHand.setPrefWidth(newValue.doubleValue()));
        root.heightProperty().addListener((observable, oldValue, newValue) -> anchorPaneHand.setPrefHeight(newValue.doubleValue()));


    }

    public void updatePlayerView(PlayerView playerView, Integer coin) {

        nameLbl.setText(playerView.getNickname());
        towerImg.setImage(GUIUtils.getTowerImage(playerView.getSchoolBoardView().getTower()));

        if (coin != null) {
            coinLbl.setVisible(true);
            coinLbl.setVisible(true);
            coinLbl.setText(String.valueOf(coin));
            coinImg.setVisible(true);
        }
        if (playerView.getPlayedCard() != null) {
            playedImg.setImage(GUIUtils.getAssistantCard(playerView.getPlayedCard()));
            playedImg.setVisible(true);
        } else playedImg.setVisible(false);

        if (playerView.getNickname().equals(gui.getNickname())) {
            handBtn.setVisible(true);
            handBtn.setDisable(false);
        }

        SchoolBoardController schoolBoardController = (SchoolBoardController) ResourceLoader.loadFXML(FXMLPath.SCHOOL_BOARD, gui);
        schoolBoardController.init();
        GUIUtils.addToAnchorPane(schoolBoardAnc, schoolBoardController.getRootPane());

        schoolBoardController.setSchoolBoardView(playerView.getSchoolBoardView());
    }

    public void setShowHand(Function function) {
        handBtn.setOnAction(event -> function.apply());
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
