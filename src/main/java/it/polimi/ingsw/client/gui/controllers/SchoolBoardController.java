package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.GUIUtils;
import it.polimi.ingsw.network.messages.views.SchoolBoardView;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.server.model.enums.Tower;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.*;

public class SchoolBoardController implements GUIController {

    @FXML
    public ImageView imgBoard;
    @FXML
    public AnchorPane anchorPaneBoard;
    public AnchorPane anchorPaneBlue;
    public AnchorPane anchorPaneYellow;
    public AnchorPane anchorPaneGreen;
    public AnchorPane anchorPaneRed;
    public AnchorPane anchorPaneMagenta;
    public AnchorPane anchorPaneHall;
    private GUI gui;

    public final List<Button> entranceButtons = new ArrayList<>(9);

    private final Map<Button, ImageView> entranceImageViewByButton = new HashMap<>();

    private final List<ImageView> towerImageViews = new ArrayList<>(8);

    private final EnumMap<StudentColor, ImageView> professorsImageViewByColor = new EnumMap<>(StudentColor.class);

    private final EnumMap<StudentColor, List<ImageView>> hallImageViewsByColor = new EnumMap<>(StudentColor.class);

    public final EnumMap<StudentColor, Button> hallButtonsByColor = new EnumMap<>(StudentColor.class);

    public final EnumMap<StudentColor, AnchorPane> hallAnchorPaneByColor = new EnumMap<>(StudentColor.class);
    

    private Pane root;

    @FXML
    private Button blueHallButton;

    @FXML
    private GridPane entranceGrid;

    @FXML
    private Button greenHallButton;

    @FXML
    public Button hallButton;

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
        initEntrance();
        initTowers();
        initProfessors();
        initHall();

        GUIUtils.bindSize(anchorPaneHall, hallButton);
        GUIUtils.bindSize(anchorPaneBlue, blueHallButton);
        GUIUtils.bindSize(anchorPaneGreen, greenHallButton);
        GUIUtils.bindSize(anchorPaneMagenta, magentaHallButton);
        GUIUtils.bindSize(anchorPaneRed, redHallButton);
        GUIUtils.bindSize(anchorPaneYellow, yellowHallButton);

        root.getChildren().clear();

        GUIUtils.bindSize(anchorPaneBoard, imgBoard);
        GUIUtils.addToPaneCenterKeepRatio(root, anchorPaneBoard, 1125.0 / 488);
    }

    private void initEntrance() {
        for (int i = 0; i < 9; i++) {
            Button btn = new Button();
            btn.setMinHeight(0.0);
            btn.setMinWidth(0.0);
            btn.setBackground(null);
            btn.setText("");
            entranceButtons.add(btn);
            ImageView imageView = new ImageView();
            imageView.setPreserveRatio(true);
            GUIUtils.bindSize(btn, imageView);
            entranceImageViewByButton.put(btn, imageView);
            btn.setGraphic(imageView);
            entranceGrid.add(btn, ((i + 1) % 2) * 2 + 1, ((i + 1) / 2) * 2 + 1);
            GUIUtils.resetButton(btn);
        }
    }

    private void initTowers() {
        for (int i = 0; i < 8; i++) {
            AnchorPane anchorPane = new AnchorPane();
            anchorPane.setMinHeight(0.0);
            anchorPane.setMinWidth(0.0);
            ImageView imageView = new ImageView();
            imageView.setPreserveRatio(true);
            GUIUtils.bindSize(anchorPane, imageView);
            towerImageViews.add(imageView);
            anchorPane.getChildren().add(imageView);
            towersGrid.add(anchorPane, (i % 2) * 2 + 1, (i / 2) * 2 + 1);
        }
        hallButtonsByColor.put(StudentColor.BLUE, blueHallButton);
        hallButtonsByColor.put(StudentColor.GREEN, greenHallButton);
        hallButtonsByColor.put(StudentColor.MAGENTA, magentaHallButton);
        hallButtonsByColor.put(StudentColor.RED, redHallButton);
        hallButtonsByColor.put(StudentColor.YELLOW, yellowHallButton);
    }

    private void initProfessors() {
        for (StudentColor sc : StudentColor.values()) {
            AnchorPane anchorPane = new AnchorPane();
            anchorPane.setMinHeight(0.0);
            anchorPane.setMinWidth(0.0);
            ImageView imageView = new ImageView();
            imageView.setPreserveRatio(true);
            imageView.setRotate(90.0);
            GUIUtils.bindSize(anchorPane, imageView);
            professorsImageViewByColor.put(sc, imageView);
            anchorPane.getChildren().add(imageView);
            profsGrid.add(anchorPane, 1, (sc.ordinal() * 2) + 1);
        }
    }
    
    private void initHall() {
        GUIUtils.resetButton(hallButton);
        hallAnchorPaneByColor.put(StudentColor.BLUE, anchorPaneBlue);
        hallAnchorPaneByColor.put(StudentColor.GREEN, anchorPaneGreen);
        hallAnchorPaneByColor.put(StudentColor.MAGENTA, anchorPaneMagenta);
        hallAnchorPaneByColor.put(StudentColor.RED, anchorPaneRed);
        hallAnchorPaneByColor.put(StudentColor.YELLOW, anchorPaneYellow);
        for (StudentColor sc : StudentColor.values()) {
            GUIUtils.resetButton(hallButtonsByColor.get(sc));
            hallImageViewsByColor.put(sc, new ArrayList<>(10));
            for (int i = 0; i < 10; i++) {
                AnchorPane anchorPane = new AnchorPane();
                anchorPane.setMinHeight(0.0);
                anchorPane.setMinWidth(0.0);
                ImageView imageView = new ImageView();
                imageView.setPreserveRatio(true);
                hallImageViewsByColor.get(sc).add(imageView);
                GUIUtils.bindSize(anchorPane, imageView);
                anchorPane.getChildren().add(imageView);

                AnchorPane father = new AnchorPane();
                father.setMinHeight(0.0);
                father.setMinWidth(0.0);
                GUIUtils.addToPaneCenterKeepRatio(father, anchorPane, 1.0);
                father.setMouseTransparent(true);

                hallGrid.add(father, i + 1, sc.ordinal() * 2 + 1);
            }
        }
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



    public void setSchoolBoardView(SchoolBoardView schoolBoardView) {
        setEntrance(schoolBoardView.getEntrance());
        setHall(schoolBoardView.getStudentsHall());
        setProfs(schoolBoardView.getProfessors());
        setTowers(schoolBoardView.getNumTowers(), schoolBoardView.getTower());
    }

    public void setEntrance(List<StudentColor> entrance) {
        for (int i = 0; i < entrance.size(); i++) {
            if (entrance.get(i) != null) {
                Image studentImage = GUIUtils.getStudentImage(entrance.get(i));
                entranceImageViewByButton.get(entranceButtons.get(i)).setImage(studentImage);
            } else
                entranceImageViewByButton.get(entranceButtons.get(i)).setImage(null);
        }
    }

    public void setHall(EnumMap<StudentColor, Integer> hall) {
        for (StudentColor sc : hallImageViewsByColor.keySet()) {
            int num = 0;
            for (; num < hall.get(sc); num++)
                hallImageViewsByColor.get(sc).get(num).setImage(GUIUtils.getStudentImage(sc));
            for (; num < hallImageViewsByColor.get(sc).size(); num++)
                hallImageViewsByColor.get(sc).get(num).setImage(null);
        }
    }

    public void setProfs(EnumSet<StudentColor> professors) {
        for (StudentColor sc : StudentColor.values()) {
            if (professors.contains(sc)) {
                professorsImageViewByColor.get(sc).setImage(GUIUtils.getProfImage(sc));
            } else
                professorsImageViewByColor.get(sc).setImage(null);
        }
    }

    public void setTowers(int numTowers, Tower tower) {
        Image towerImage = GUIUtils.getTowerImage(tower);
        int num = 0;
        for (; num < numTowers; num++)
            towerImageViews.get(num).setImage(towerImage);
        for (; num < towerImageViews.size(); num++)
            towerImageViews.get(num).setImage(null);
    }

    /**
     * This method is used for clear all the buttons in the school board.
     */
    public void clearAllButtons() {
        for (Button button : entranceButtons)
            GUIUtils.resetButton(button);
        for(Button button : hallButtonsByColor.values())
            GUIUtils.resetButton(button);
        GUIUtils.resetButton(hallButton);
    }
}
