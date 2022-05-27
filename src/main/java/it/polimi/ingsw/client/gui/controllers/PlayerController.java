package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.enums.FXMLPath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.GUIUtils;
import it.polimi.ingsw.client.gui.ResourceLoader;
import it.polimi.ingsw.network.messages.views.PlayerView;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

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
        coinLbl.setVisible(false);
        coinImg.setVisible(false);
        playedImg.setVisible(true);
        handBtn.setVisible(false);
        handBtn.setDisable(true);
    }

    public void updatePlayerView(PlayerView playerView, Integer coin) {

        nameLbl.setText(playerView.getNickname());
        towerImg.setImage(GUIUtils.getTowerImage(playerView.getSchoolBoardView().getTower()));

        if(coin != null){
            coinLbl.setVisible(true);
            coinLbl.setVisible(true);
            coinLbl.setText(String.valueOf(coin));
        }
        if(playerView.getAssistantCards()!= null) {
            playedImg.setImage(GUIUtils.getAssistantCard(playerView.getPlayedCard()));
            playedImg.setVisible(true);
        }
        else  playedImg.setVisible(false);

        if(playerView.getNickname().equals(gui.getNickname())) {
            handBtn.setVisible(true);
            handBtn.setDisable(false);
        }

        SchoolBoardController schoolBoardController = (SchoolBoardController) ResourceLoader.loadFXML(FXMLPath.SCHOOL_BOARD, gui);
        schoolBoardController.init();
        GUIUtils.addToAnchorPane(schoolBoardAnc, schoolBoardController.getParent());

        schoolBoardController.setSchoolBoardView(playerView.getSchoolBoardView());
    }

    public void showHand() {

    }

    /**
     * This method is used to set the parent of the controller.
     *
     * @param parent the parent of the controller.
     */
    @Override
    public void setParent(Parent parent) {
        this.root = parent;
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
}
