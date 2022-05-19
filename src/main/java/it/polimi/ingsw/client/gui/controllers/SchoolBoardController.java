package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.server.model.enums.StudentColor;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class SchoolBoardController implements GUIController{

    private GUI schoolBoardGUI;

    private final Map<Integer, AnchorPane> entranceMap = new HashMap<>();

    private final Map<Integer, AnchorPane> towersMap = new HashMap<>();

    private final EnumMap<StudentColor, Map<Integer, AnchorPane>> hallMap = new EnumMap<>(StudentColor.class);

    private final EnumMap<StudentColor, AnchorPane> profsMap = new EnumMap<>(StudentColor.class);

    @FXML
    private GridPane towers_grid;

    @FXML
    private GridPane ent_hall_prof_grid;

    @FXML
    private AnchorPane blueProf;

    @FXML
    private AnchorPane greenProf;

    @FXML
    private AnchorPane magentaProf;

    @FXML
    private AnchorPane redProf;

    @FXML
    private AnchorPane yellowProf;

    @FXML
    private AnchorPane root;

    @Override
    public void setGUI(GUI gui) {
        this.schoolBoardGUI = gui;
    }

    @Override
    public void init() {
        // initialize components & their listeners
        towers_grid.setGridLinesVisible(false);
        towers_grid.setDisable(true);

        ent_hall_prof_grid.setGridLinesVisible(false);
        ent_hall_prof_grid.setDisable(true);

        // set all the maps
        // entranceMap
        int row = 0;
        int column = 1;
        for(int i = 0; i < 9; i++){
            if (getAnchorFromGrid(ent_hall_prof_grid, row, column) != null)
                entranceMap.put(i, getAnchorFromGrid(ent_hall_prof_grid, row, column));
            if (column == 1)
                row++;
            column = (column + 1) % 2;
        }

        //towerMap
        row = 0;
        column = 0;
        for(int i = 0; i < 8; i++){
            if (getAnchorFromGrid(towers_grid, row, column) != null)
                towersMap.put(i, getAnchorFromGrid(towers_grid, row, column));
            if (column == 1)
                row++;
            column = (column + 1) % 2;
        }

        //profsMap
        profsMap.put(StudentColor.GREEN, greenProf);
        profsMap.put(StudentColor.RED, redProf);
        profsMap.put(StudentColor.YELLOW, yellowProf);
        profsMap.put(StudentColor.MAGENTA, magentaProf);
        profsMap.put(StudentColor.BLUE, blueProf);

        //hallMap
        row = 0;
        for (StudentColor sc : StudentColor.values()) {
            Map<Integer, AnchorPane> table = new HashMap<>();
            for (int i = 0; i < 10; i++){
                table.put(i, getAnchorFromGrid(ent_hall_prof_grid, row, i+3));
            }
            hallMap.put(sc, table);
            row++;
        }
    }

    public AnchorPane getAnchorFromGrid (GridPane gridPane, int row, int column){
        for(Node node : gridPane.getChildren()){
            if (GridPane.getColumnIndex(node) == column && GridPane.getRowIndex(node) == row)
                return (AnchorPane) node;
        }
        return null;
    }
}
