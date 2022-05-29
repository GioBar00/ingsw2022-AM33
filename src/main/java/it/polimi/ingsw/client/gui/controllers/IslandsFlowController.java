package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.enums.FXMLPath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.GUIUtils;
import it.polimi.ingsw.client.gui.ResourceLoader;
import it.polimi.ingsw.network.messages.actions.ChosenIsland;
import it.polimi.ingsw.network.messages.actions.MovedMotherNature;
import it.polimi.ingsw.network.messages.views.IslandGroupView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.util.*;

public class IslandsFlowController implements GUIController {

    GUI gui;

    List<AnchorPane> anchors = new LinkedList<>();

    List<IslandController> islandsControllers = new LinkedList<>();

    @FXML
    private FlowPane flowPane;

    @FXML
    private AnchorPane root;

    @Override
    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    @Override
    public void init() {
        for (int i = 0; i < 12; i++) {
            anchors.add((AnchorPane) flowPane.getChildren().get(i));
        }

        for (int i = 0; i < 12; i++) {
            GUIController islandGui;
            islandGui = ResourceLoader.loadFXML(FXMLPath.ISLAND, gui);
            islandsControllers.add((IslandController) islandGui);
            GUIUtils.addToAnchorPane(anchors.get(i), islandGui.getRootPane());
            islandGui.init();
        }
    }

    public void setIslandsView(List<IslandGroupView> islandsView, int motherNatureIndex) {
        for (int i = 0; i < islandsView.size(); i++) {
            boolean motherNatureIsThere = motherNatureIndex == i;
            islandsControllers.get(i).setIsland(islandsView.get(i), motherNatureIsThere);
        }
        if (islandsView.size() < 12) {
            for (int j = 0; j < 12 - islandsView.size(); j++) {
                anchors.get(j).getChildren().removeAll();
            }
        }
    }

    @Override
    public void setRootPane(Pane root) {
        // already set
    }

    @Override
    public Pane getRootPane() {
        return root;
    }


    /**
     * This method sets the action on island buttons when the player could select an island.
     *
     * @param availableIslandIndexes the {@link Set} of clickable islands.
     */
    void chooseIsland(Set<Integer> availableIslandIndexes) {
        for (Integer i : availableIslandIndexes) {
            if (i < islandsControllers.size()) {
                Button islandBtn = islandsControllers.get(i).islandButton;
                GUIUtils.setButton(islandBtn, actionEvent -> gui.notifyViewListener(new ChosenIsland(i)));
            }
        }
    }


    /**
     * This method sets the islands the player could choose during the moving mother nature phase.
     *
     * @param maxNumMoves       the max steps mother nature could take.
     * @param motherNatureIndex the current index of mother nature.
     */
    void moveMotherNature(Integer maxNumMoves, Integer motherNatureIndex) {
        Map<Integer, Integer> availableIslandIndexes = new HashMap<>();

        for (int i = 1; i <= maxNumMoves; i++) {
            Integer index = (motherNatureIndex + i) % islandsControllers.size();
            availableIslandIndexes.put(index, i);
        }

        for (Integer i : availableIslandIndexes.keySet()) {
            if (i < islandsControllers.size()) {
                Button islandBtn = islandsControllers.get(i).islandButton;
                GUIUtils.setButton(islandBtn, actionEvent -> gui.notifyViewListener(new MovedMotherNature(availableIslandIndexes.get(i))));
            }
        }
    }

}
