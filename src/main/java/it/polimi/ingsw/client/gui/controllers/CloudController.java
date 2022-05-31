package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.GUIUtils;
import it.polimi.ingsw.network.messages.actions.ChosenCloud;
import it.polimi.ingsw.network.messages.views.CloudView;
import it.polimi.ingsw.server.model.enums.StudentColor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class CloudController implements GUIController {

    @FXML
    public Pane root;
    @FXML
    public Button cloudBtn;

    @FXML
    public ImageView pawn0Img;

    @FXML
    public ImageView pawn1Img;

    @FXML
    public ImageView pawn2Img;

    @FXML
    public ImageView pawn3Img;
    @FXML
    public ImageView imgViewCloud;
    @FXML
    public AnchorPane anchorPanePawn;
    @FXML
    public AnchorPane anchorPaneCloud;

    private ArrayList<ImageView> imageViews;

    private GUI gui;

    private int cloudIndex;

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
     * Set the cloud view.
     *
     * @param cloudView for rendering the view.
     * @param number    the index of the island.
     */
    public void setCloud(CloudView cloudView, int number) {
        cloudIndex = number;

        for (ImageView iv : imageViews)
            iv.setImage(null);

        int i = 0;

        for (StudentColor c : cloudView.getStudents()) {
            if (c != null) {
                imageViews.get(i).setImage(GUIUtils.getStudentImage(c));
                i++;
            }
        }

    }

    /**
     * Notifies the listener with a {@link ChosenCloud} message
     */
    public void sendChosenCloud() {
        gui.notifyViewListener(new ChosenCloud(cloudIndex));
    }

    /**
     * Enable the picking of the cloud.
     *
     * @param enable set as true if the cloud could be picked.
     */
    public void enableCloud(boolean enable) {
        cloudBtn.setDisable(!enable);
    }

    /**
     * This method is used to initialize the controller and stage.
     */
    @Override
    public void init() {
        imageViews = new ArrayList<>(4);
        imageViews.add(pawn0Img);
        imageViews.add(pawn1Img);
        imageViews.add(pawn2Img);
        imageViews.add(pawn3Img);

        root.getChildren().clear();

        GUIUtils.bindSize(anchorPaneCloud, cloudBtn);
        GUIUtils.bindSize(anchorPaneCloud, imgViewCloud);
        for (ImageView iv : imageViews)
            GUIUtils.bindSize(anchorPanePawn, iv);

        GUIUtils.addToPaneCenterKeepRatio(root, anchorPaneCloud, 1.0);

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
