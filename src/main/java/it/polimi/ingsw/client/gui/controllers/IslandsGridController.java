package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.enums.FXMLPath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.GUIUtils;
import it.polimi.ingsw.client.gui.ResourceLoader;
import it.polimi.ingsw.network.messages.views.IslandGroupView;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class IslandsGridController implements GUIController {

    GUI gui;

    List<AnchorPane> anchors = new LinkedList<>();

    LinkedList<IslandController> islandsControllers = new LinkedList();

    @FXML
    AnchorPane root;

    @FXML
    GridPane islandsGrid;

    @Override
    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    @Override
    public void init() {
        for (int i = 0; i < 12; i++){
            anchors.add((AnchorPane) islandsGrid.getChildren().get(i));
        }

        for (int i = 0; i < 12; i++){
            GUIController islandGui;
            islandGui = ResourceLoader.loadFXML(FXMLPath.ISLAND, gui);
            islandsControllers.add((IslandController) islandGui);
            GUIUtils.addToAnchorPane(anchors.get(i), islandsGrid.getParent());
            islandGui.init();
        }
    }

    public void setIslandsView(ArrayList<IslandGroupView> islandsView, int motherNatureIndex){
        for (int i = 0; i < islandsView.size(); i ++){
            boolean motherNatureIsThere = motherNatureIndex == i;
            islandsControllers.get(i).setIsland(islandsView.get(i), motherNatureIsThere);
        }
        if (islandsView.size() < 12){
            for (int j = 0; j < 12 - islandsView.size(); j++){
                anchors.get(j).getChildren().removeAll();
            }
        }
    }

    @Override
    public void setParent(Parent parent) {
        this.root = (AnchorPane) parent;
    }

    @Override
    public Parent getParent() {
        return root;
    }
}
