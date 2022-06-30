package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.enums.FXMLPath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.GUIUtils;
import it.polimi.ingsw.client.gui.ResourceLoader;
import it.polimi.ingsw.network.messages.views.IslandGroupView;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is the controller of the islands view.
 */
public class IslandsController implements GUIController {

    /**
     * {@link GUI} instance.
     */
    private GUI gui;

    /**
     * List used to keep track of the anchor panes on which the islands will be set
     */
    private final List<AnchorPane> anchors = new LinkedList<>();

    /**
     * List used to keep track of all the controllers of the islands.
     */
    public final List<IslandController> islandControllers = new LinkedList<>();

    @FXML
    private FlowPane flowPane;

    @FXML
    private AnchorPane root;


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
        for (int i = 0; i < 12; i++) {
            AnchorPane anchorPane = (AnchorPane) flowPane.getChildren().get(i);
            anchors.add(anchorPane);
        }

        for (AnchorPane anchor : anchors) {
            flowPane.heightProperty().addListener((observable, oldValue, newValue) -> {
                anchor.setPrefHeight(newValue.doubleValue() / 3);
                anchor.setPrefWidth(newValue.doubleValue() / 2.5);
            });
            IslandController islandGui = ResourceLoader.loadFXML(FXMLPath.ISLAND, gui);
            islandControllers.add(islandGui);
            GUIUtils.addToAnchorPane(anchor, islandGui.getRootPane());
            islandGui.init();
        }
    }

    /**
     * This method is used to set the islandsView.
     *
     * @param islandsView       the islandsView.
     * @param motherNatureIndex the index of the mother nature.
     */
    public void setIslandsView(List<IslandGroupView> islandsView, int motherNatureIndex) {
        for (int i = 0; i < islandsView.size(); i++) {
            boolean motherNatureIsThere = motherNatureIndex == i;
            islandControllers.get(i).setIsland(islandsView.get(i), motherNatureIsThere);
        }

        for (int i = islandsView.size(); i < anchors.size(); i++)
            flowPane.getChildren().remove(anchors.get(i));

        while (islandControllers.size() > islandsView.size())
            islandControllers.remove(islandControllers.size() - 1);
    }

    /**
     * This method is used to set the parent of the controller.
     *
     * @param root the parent of the controller.
     */
    @Override
    public void setRootPane(Pane root) {
        // already set
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
