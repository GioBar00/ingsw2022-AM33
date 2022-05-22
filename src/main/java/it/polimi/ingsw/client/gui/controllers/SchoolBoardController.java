package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.network.messages.views.SchoolBoardView;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.server.model.enums.Tower;
import it.polimi.ingsw.util.Function;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class SchoolBoardController implements GUIController {

    private GUI schoolBoardGUI;

    private final Map<Integer, AnchorPane> entranceMap = new HashMap<>();

    private final Map<Integer, AnchorPane> towersMap = new HashMap<>();

    private final EnumMap<StudentColor, Map<Integer, AnchorPane>> hallMap = new EnumMap<>(StudentColor.class);

    private final EnumMap<StudentColor, AnchorPane> profsMap = new EnumMap<>(StudentColor.class);

    private final Map<Integer, Button> entranceButtonsMap = new HashMap<>();

    private final Map<Button, Function> buttonAction = new HashMap<>();

    @FXML
    private Button blueHallButton;

    @FXML
    private AnchorPane blueProf;

    @FXML
    private GridPane ent_hall_prof_grid;

    @FXML
    private Button greenHallButton;

    @FXML
    private AnchorPane greenProf;

    @FXML
    private Button hallButton;

    @FXML
    private Button magentaHallButton;

    @FXML
    private AnchorPane magentaProf;

    @FXML
    private Button redHallButton;

    @FXML
    private AnchorPane redProf;

    @FXML
    private AnchorPane root;

    @FXML
    private GridPane towers_grid;

    @FXML
    private Button yellowHallButton;

    @FXML
    private AnchorPane yellowProf;

    @FXML
    private GridPane buttonEntranceGrid;


    /**
     * method to set the GUI
     *
     * @param gui the GUI of the controller.
     */
    @Override
    public void setGUI(GUI gui) {
        this.schoolBoardGUI = gui;
    }


    /**
     * method to initialize the controller
     */
    @Override
    public void init() {
        // initialize components & their listeners
        towers_grid.setGridLinesVisible(false);
        towers_grid.setDisable(true);

        ent_hall_prof_grid.setGridLinesVisible(false);
        ent_hall_prof_grid.setDisable(true);

        buttonEntranceGrid.setGridLinesVisible(false);
        buttonEntranceGrid.setDisable(true);

        greenHallButton.setDisable(true);

        redHallButton.setDisable(true);

        yellowHallButton.setDisable(true);

        magentaHallButton.setDisable(true);

        blueHallButton.setDisable(true);

        hallButton.setDisable(true);

        // set all the maps
        // entranceMap
        int row = 0;
        int column = 1;
        for (int i = 0; i < 9; i++) {
            if (getAnchorFromGrid(ent_hall_prof_grid, row, column) != null)
                entranceMap.put(i, getAnchorFromGrid(ent_hall_prof_grid, row, column));
            if (column == 1)
                row++;
            column = (column + 1) % 2;
        }

        // entrance buttons map
        row = 0;
        column = 1;
        for (int i = 0; i < 9; i++) {
            if (getAnchorFromGrid(buttonEntranceGrid, row, column) != null) {
                Button button = (Button) getAnchorFromGrid(buttonEntranceGrid, row, column).getChildren();
                entranceButtonsMap.put(i, button);
                entranceButtonsMap.get(i).setDisable(true);
                // entranceButtonsMap.get(i).addEventHandler(MouseEvent.MOUSE_CLICKED, ... non abbiamo un event per il click);
            }
            if (column == 1)
                row++;
            column = (column + 1) % 2;
        }

        // button action map
        for (int i = 0; i < 9; i++) {
            buttonAction.put(entranceButtonsMap.get(i), () -> {
            });
        }

        //towerMap
        row = 0;
        column = 0;
        for (int i = 0; i < 8; i++) {
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
            for (int i = 0; i < 10; i++) {
                table.put(i, getAnchorFromGrid(ent_hall_prof_grid, row, i + 3));
            }
            hallMap.put(sc, table);
            row++;
        }
    }

    public void setSchoolBoardGUIData(SchoolBoardView schoolBoardView) {
        // setEntrance
        for (int i = 0; i < 9; i++) {
            setEntranceSlot(schoolBoardView.getEntrance().get(i), i, i < schoolBoardView.getEntrance().size());
        }

        // setHall
        for (StudentColor studentColor : StudentColor.values()) {
            for (int i = 0; i < 10; i++) {
                setHallSlot(studentColor, i, i < schoolBoardView.getStudentsHall().get(studentColor));
            }
        }

        // setProfs
        for (StudentColor prof : StudentColor.values()) {
            setProfessor(prof, schoolBoardView.getProfessors().contains(prof));
        }

        // setTowers
        for (int i = 0; i < 8; i++) {
            setTower(schoolBoardView.getTower(), i, i < schoolBoardView.getNumTowers());
        }

    }


    /**
     * for each cell in the grid, the method returns the anchor pane inside it
     *
     * @param gridPane that contains all cells
     * @param row      of the selected cell
     * @param column   of the selected cell
     * @return the anchorPane inside the selected cell
     */
    public AnchorPane getAnchorFromGrid(GridPane gridPane, int row, int column) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == column && GridPane.getRowIndex(node) == row)
                return (AnchorPane) node;
        }
        return null;
    }

    /**
     * add the image of a student to the grid
     *
     * @param sc        color of the student
     * @param imageView inside the GridAnchor, where the image of the student pawn will be set
     * @return true if the method was executed correctly
     */
    public boolean addStudentToGridAnchor(StudentColor sc, ImageView imageView) {
        if (sc == null || imageView == null)
            return false;

        switch (sc) {
            case GREEN -> imageView.setImage(GUI.imagesByPath.get(ImagePath.GREEN_STUDENT));
            case RED -> imageView.setImage(GUI.imagesByPath.get(ImagePath.RED_STUDENT));
            case YELLOW -> imageView.setImage(GUI.imagesByPath.get(ImagePath.YELLOW_STUDENT));
            case MAGENTA -> imageView.setImage(GUI.imagesByPath.get(ImagePath.MAGENTA_STUDENT));
            case BLUE -> imageView.setImage(GUI.imagesByPath.get(ImagePath.BLUE_STUDENTS));
        }

        return true;
    }

    /**
     * method to set the image of the student pawn inside a cell of the entrance
     *
     * @param sc    color of the student
     * @param index of the entrance slot
     * @param isSet is true if the student needs to be set, false if it has to be removed
     * @return true if the method was executed correctly
     */
    public boolean setEntranceSlot(StudentColor sc, int index, boolean isSet) {
        AnchorPane anchorPane = entranceMap.get(index);
        ImageView imageView = (ImageView) anchorPane.getChildren();
        if (isSet) {
            // you want to set the student
            if (imageView.getImage() != null)
                return false;
            addStudentToGridAnchor(sc, imageView);
        } else {
            // you want to remove the student
            if (imageView.getImage() == null)
                return true;
            imageView.setImage(null);
        }
        return true;
    }

    /**
     * method to add a student to the hall tables
     *
     * @param sc    of the table
     * @param index position of the student in the table
     * @param isSet id true if the student needs to be added, false otherwise
     * @return true if the method was executed correctly
     */
    public boolean setHallSlot(StudentColor sc, int index, boolean isSet) {
        AnchorPane anchorPane = hallMap.get(sc).get(index);
        ImageView imageView = (ImageView) anchorPane.getChildren();
        if (isSet) {
            // you want to set the student
            AnchorPane anchorPanePrevious = hallMap.get(sc).get(index - 1);
            ImageView previous = (ImageView) anchorPanePrevious.getChildren();
            if (imageView.getImage() != null || previous.getImage() == null)
                return false;
            addStudentToGridAnchor(sc, imageView);
        } else {
            // you want to remove the student
            AnchorPane anchorPaneNext = hallMap.get(sc).get(index + 1);
            ImageView next = (ImageView) anchorPaneNext.getChildren();
            if (next.getImage() == null)
                return false;
            imageView.setImage(null);
        }
        return true;
    }


    /**
     * method to set the professor's pawn in the schoolboard on the CLI
     *
     * @param sc    of the professor
     * @param isSet is true if you want the prof to be set, false if you want to remove it
     * @return true if the method was executed correctly
     */
    public boolean setProfessor(StudentColor sc, boolean isSet) {
        AnchorPane anchorPane = profsMap.get(sc);
        ImageView imageView = (ImageView) anchorPane.getChildren();
        if (isSet) {
            // you want to set the professor
            if (imageView.getImage() != null)
                return false;
            switch (sc) {
                case GREEN -> imageView.setImage(GUI.imagesByPath.get(ImagePath.GREEN_PROF));
                case RED -> imageView.setImage(GUI.imagesByPath.get(ImagePath.RED_PROF));
                case YELLOW -> imageView.setImage(GUI.imagesByPath.get(ImagePath.YELLOW_PROF));
                case MAGENTA -> imageView.setImage(GUI.imagesByPath.get(ImagePath.MAGENTA_PROF));
                case BLUE -> imageView.setImage(GUI.imagesByPath.get(ImagePath.BLUE_PROF));
            }
        } else {
            // you want to remove the professor
            if (imageView.getImage() == null)
                return true;
            imageView.setImage(null);
        }
        return true;
    }

    /**
     * method used to set a tower in the tower grid
     *
     * @param tower color of the tower
     * @param index of the tower in the towersMap
     * @param isSet true if you want to add a tower, false if you want to remove it
     * @return true if the method was executed correctly
     */
    public boolean setTower(Tower tower, int index, Boolean isSet) {
        AnchorPane anchorPane = towersMap.get(index);
        ImageView imageView = (ImageView) anchorPane.getChildren();
        if (isSet) {
            if (imageView.getImage() != null)
                return false;
            switch (tower) {
                case WHITE -> imageView.setImage(GUI.imagesByPath.get(ImagePath.WHITE_TOWER));
                case GREY -> imageView.setImage(GUI.imagesByPath.get(ImagePath.GRAY_TOWER));
                case BLACK -> imageView.setImage(GUI.imagesByPath.get(ImagePath.BLACK_TOWER));
            }
        } else {
            if (imageView.getImage() == null)
                return true;
            imageView.setImage(null);
        }
        return true;
    }

    /**
     * method to enable or disable the buttons of the entrance and the hall
     */
    public void setEnableEntranceToHall(boolean isSet) {
        for (int i = 0; i < 9; i++)
            entranceButtonsMap.get(i).setDisable(!isSet);
        hallButton.setDisable(!isSet);
    }

    /**
     * @return the schoolBoardGUI
     */
    public GUI getSchoolBoardGUI() {
        return schoolBoardGUI;
    }
}
