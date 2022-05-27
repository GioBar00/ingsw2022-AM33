package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.Coordinates;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.GUIUtils;
import it.polimi.ingsw.network.messages.views.SchoolBoardView;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.server.model.enums.Tower;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.util.*;

public class SchoolBoardController implements GUIController {

    private GUI gui;

    private final Map<Integer, Coordinates> entranceMap = new HashMap<>();

    private final Map<Integer, Coordinates> towersMap = new HashMap<>();

    private final EnumMap<StudentColor, Map<Integer, Coordinates>> hallMap = new EnumMap<>(StudentColor.class);

    private final EnumMap<StudentColor, Coordinates> profsMap = new EnumMap<>(StudentColor.class);

    private Parent root;

    @FXML
    private Button blueHallButton;

    @FXML
    private Button entrance0;

    @FXML
    private Button entrance1;

    @FXML
    private Button entrance2;

    @FXML
    private Button entrance3;

    @FXML
    private Button entrance4;

    @FXML
    private Button entrance5;

    @FXML
    private Button entrance6;

    @FXML
    private Button entrance7;

    @FXML
    private Button entrance8;

    @FXML
    private GridPane entranceGrid;

    @FXML
    private Button greenHallButton;

    @FXML
    private Button hallButton;

    @FXML
    private GridPane hallGrid;

    @FXML
    private Button magentaHallButton;

    @FXML
    private GridPane profsGrid;

    @FXML
    private Button redHallButton;

    @FXML
    private GridPane towersGrid;

    @FXML
    private Button yellowHallButton;

    /**
     * method to set the GUI
     *
     * @param gui the GUI of the controller.
     */
    @Override
    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    @Override
    public void init() {
        // initialize components & their listeners
        towersGrid.setGridLinesVisible(false);
        towersGrid.setDisable(true);

        entranceGrid.setGridLinesVisible(false);
        entranceGrid.setDisable(true);

        hallGrid.setGridLinesVisible(false);
        hallGrid.setDisable(true);

        profsGrid.setGridLinesVisible(false);
        profsGrid.setDisable(true);

        greenHallButton.setDisable(true);

        redHallButton.setDisable(true);

        yellowHallButton.setDisable(true);

        magentaHallButton.setDisable(true);

        blueHallButton.setDisable(true);

        hallButton.setDisable(true);

        // set all the maps
        // entranceMap
        int row = 0;
        int column = 3;
        for (int i = 0; i < 9; i++) {
            entranceMap.put(i, new Coordinates(row, column));
            if (column == 3) {
                row++;
                column = 1;
            } else
                column = 3;
        }

        //towerMap
        row = 0;
        column = 0;
        for (int i = 0; i < 8; i++) {
            towersMap.put(i, new Coordinates(row, column));
            if (column == 1)
                row++;
            column = (column + 1) % 2;
        }

        //profsMap
        profsMap.put(StudentColor.GREEN, new Coordinates(0, 1));
        profsMap.put(StudentColor.RED, new Coordinates(2, 1));
        profsMap.put(StudentColor.YELLOW, new Coordinates(4, 1));
        profsMap.put(StudentColor.MAGENTA, new Coordinates(6, 1));
        profsMap.put(StudentColor.BLUE, new Coordinates(8, 1));

        //hallMap
        row = 0;
        for (StudentColor sc : StudentColor.values()) {
            Map<Integer, Coordinates> table = new HashMap<>();
            for (int i = 0; i < 10; i++) {
                table.put(i, new Coordinates(row, i));
            }
            hallMap.put(sc, table);
            row++;
        }
    }

    /**
     * This method is used to set the parent of the controller.
     *
     * @param parent the parent of the controller.
     */
    @Override
    public void setParent(Parent parent) {
        root = parent;
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



    public void setSchoolBoardView(SchoolBoardView schoolBoardView) {
        setEntrance(schoolBoardView.getEntrance());
        setHall(schoolBoardView.getStudentsHall());
        setProfs(schoolBoardView.getProfessors());
        setTowers(schoolBoardView.getNumTowers(), schoolBoardView.getTower());
    }

    public void setEntrance(ArrayList<StudentColor> entrance) {
        for (int i = 0; i < 9; i++) {
            if (entrance.get(i) != null) {
                Image studentImage = GUIUtils.getStudentImage(entrance.get(i));
                ImageView imageView = new ImageView(studentImage);
                imageView.setFitWidth(57);
                imageView.setFitHeight(57);
                entranceGrid.add(imageView, entranceMap.get(i).getColumn(), entranceMap.get(i).getRow());
                GridPane.setHalignment(imageView, HPos.CENTER);
                GridPane.setValignment(imageView, VPos.CENTER);
            } else
                removeImagesFromCell(entranceGrid, entranceMap.get(i).getRow(), entranceMap.get(i).getColumn());
        }
    }

    public void setHall(EnumMap<StudentColor, Integer> hall) {
        for (StudentColor sc : StudentColor.values()) {
            for (int i = 0; i < 10; i++) {
                if (i < hall.get(sc)) {
                    Image studentImage = GUIUtils.getStudentImage(sc);
                    ImageView imageView = new ImageView(studentImage);
                    hallGrid.add(imageView, hallMap.get(sc).get(i).getColumn(), hallMap.get(sc).get(i).getRow());
                    GridPane.setHalignment(imageView, HPos.CENTER);
                    GridPane.setValignment(imageView, VPos.CENTER);
                } else
                    removeImagesFromCell(hallGrid, hallMap.get(sc).get(i).getRow(), hallMap.get(sc).get(i).getColumn());
            }
        }
    }

    public void setProfs(EnumSet<StudentColor> professors) {
        for (StudentColor sc : StudentColor.values()) {
            if (professors.contains(sc)) {
                Image profImage = GUIUtils.getProfImage(sc);
                ImageView imageView = new ImageView(profImage);
                profsGrid.add(imageView, profsMap.get(sc).getColumn(), profsMap.get(sc).getRow());
                GridPane.setHalignment(imageView, HPos.CENTER);
                GridPane.setValignment(imageView, VPos.CENTER);
            } else
                removeImagesFromCell(profsGrid, profsMap.get(sc).getRow(), profsMap.get(sc).getColumn());
        }
    }

    public void setTowers(int numTowers, Tower tower) {
        for (int i = 0; i < 8; i++) {
            if (i < numTowers) {
                Image towerImage = GUIUtils.getTowerImage(tower);
                ImageView imageView = new ImageView(towerImage);
                towersGrid.add(imageView, towersMap.get(i).getColumn(), towersMap.get(i).getRow());
                GridPane.setHalignment(imageView, HPos.CENTER);
                GridPane.setValignment(imageView, VPos.CENTER);
            } else
                removeImagesFromCell(towersGrid, towersMap.get(i).getRow(), towersMap.get(i).getColumn());
        }
    }

    public List<Node> getNodesFromGrid(GridPane gridPane, int row, int column) {
        List<Node> nodes = new ArrayList<>();
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == column && GridPane.getRowIndex(node) == row)
                nodes.add(node);
        }
        return nodes;
    }

    public void removeImagesFromCell(GridPane gridPane, int row, int column) {
        int num = getNodesFromGrid(gridPane, row, column).size();
        if (num > 0) {
            List<Node> nodes = getNodesFromGrid(gridPane, row, column);
            for (int j = 0; j < num; j++) {
                Node toBeRemoved = nodes.get(j);
                if (toBeRemoved instanceof ImageView)
                    entranceGrid.getChildren().remove(toBeRemoved);
            }
        }
    }

}
