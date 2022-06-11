package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.GUIUtils;
import it.polimi.ingsw.client.gui.ResourceLoader;
import it.polimi.ingsw.network.messages.views.IslandGroupView;
import it.polimi.ingsw.network.messages.views.IslandView;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.server.model.enums.Tower;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.*;

/**
 * This class is the controller of the island view.
 */
public class IslandController implements GUIController {

    /**
     * {@link GUI} instance
     */
    private GUI gui;
    @FXML
    private AnchorPane anchorPaneStudent;
    @FXML
    private AnchorPane anchorPaneMotherNature;
    @FXML
    private AnchorPane anchorPaneTower;
    @FXML
    private AnchorPane anchorPaneBlock;
    @FXML
    private ImageView imgStudent0;
    @FXML
    private ImageView imgStudent1;
    @FXML
    private ImageView imgStudent2;
    @FXML
    private ImageView imgStudent3;
    @FXML
    private ImageView imgStudent4;
    @FXML
    private AnchorPane anchorPaneIsland;

    Map<StudentColor, Label> studentNumbersMap = new HashMap<>();

    @FXML
    private ImageView blockImage;

    @FXML
    public Button islandButton;

    @FXML
    private GridPane islandGrid;

    @FXML
    private ImageView islandImage;

    @FXML
    private GridPane margins;

    @FXML
    private ImageView motherNatureImage;

    @FXML
    private Label numBlue;

    @FXML
    private Label numGreen;

    @FXML
    private Label numMagenta;

    @FXML
    private Label numRed;

    @FXML
    private Label numYellow;

    @FXML
    private Label numberLabel;

    @FXML
    private Pane root;

    @FXML
    private ImageView towerImage;

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
        studentNumbersMap.put(StudentColor.GREEN, numGreen);
        studentNumbersMap.put(StudentColor.RED, numRed);
        studentNumbersMap.put(StudentColor.YELLOW, numYellow);
        studentNumbersMap.put(StudentColor.MAGENTA, numMagenta);
        studentNumbersMap.put(StudentColor.BLUE, numBlue);

        root.getChildren().clear();

        GUIUtils.bindSize(anchorPaneIsland, islandImage);
        GUIUtils.bindSize(anchorPaneMotherNature, motherNatureImage);
        GUIUtils.bindSize(anchorPaneBlock, blockImage);
        GUIUtils.bindSize(anchorPaneTower, towerImage);
        GUIUtils.bindSize(anchorPaneStudent, imgStudent0);
        GUIUtils.bindSize(anchorPaneStudent, imgStudent1);
        GUIUtils.bindSize(anchorPaneStudent, imgStudent2);
        GUIUtils.bindSize(anchorPaneStudent, imgStudent3);
        GUIUtils.bindSize(anchorPaneStudent, imgStudent4);

        GUIUtils.addToPaneCenterKeepRatio(root, anchorPaneIsland, 1.0);

        islandGrid.setGridLinesVisible(false);
        margins.setGridLinesVisible(false);

        setImageVisibility(motherNatureImage, false);
        setImageVisibility(blockImage, false);

        setIslandImage();
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

    /**
     * This method is used to setting the pawns on the island.
     *
     * @param islandGroupView the view of the island.
     * @param isMotherNature  true if the island has mother nature on it, false otherwise.
     */
    public void setIsland(IslandGroupView islandGroupView, boolean isMotherNature) {
        setNumOfStudents(getTotalStudents(islandGroupView.getIslands()));
        setImageVisibility(motherNatureImage, isMotherNature);
        setImageVisibility(blockImage, islandGroupView.isBlocked());
        setTowerImage(islandGroupView.getIslands().get(0).getTower());
        setNumberLabel(islandGroupView.getIslands().size());
    }

    /**
     * This method is used to assign a random image to the island.
     */
    public void setIslandImage() {
        Random rand = new Random();
        int value = rand.nextInt(100);
        value = value % 3;
        switch (value) {
            case 0 -> islandImage.setImage(ResourceLoader.loadImage(ImagePath.ISLAND1));
            case 1 -> islandImage.setImage(ResourceLoader.loadImage(ImagePath.ISLAND2));
            case 2 -> islandImage.setImage(ResourceLoader.loadImage(ImagePath.ISLAND3));
        }
    }

    /**
     * This method sets the number of students divided by the color on the island.
     *
     * @param students a map of the number of students divided by the color.
     */
    public void setNumOfStudents(EnumMap<StudentColor, Integer> students) {
        for (StudentColor sc : students.keySet()) {
            studentNumbersMap.get(sc).setText(students.get(sc).toString());
        }
    }

    /**
     * This method is used to know the total number of students on the island.
     *
     * @param islands the islands on the islands group.
     * @return the total number of students on the island divided by the color.
     */
    public EnumMap<StudentColor, Integer> getTotalStudents(List<IslandView> islands) {
        EnumMap<StudentColor, Integer> totalStudents = new EnumMap<>(StudentColor.class);

        for (StudentColor sc : StudentColor.values()) {
            totalStudents.put(sc, 0);
        }

        for (IslandView iv : islands) {
            for (StudentColor sc : iv.getStudents().keySet()) {
                int oldValue = totalStudents.get(sc);
                totalStudents.put(sc, oldValue + iv.getStudents().get(sc));
            }
        }
        return totalStudents;
    }

    /**
     * This method is used for setting the visibility of a specific image.
     *
     * @param image     the image to set the visibility.
     * @param isVisible true if the image is visible, false otherwise.
     */
    public void setImageVisibility(ImageView image, boolean isVisible) {
        image.setVisible(isVisible);
        image.setDisable(!isVisible);
    }

    /**
     * This method is used to set the tower image.
     *
     * @param tower the type of tower.
     */
    public void setTowerImage(Tower tower) {
        if (tower != null)
            towerImage.setImage(GUIUtils.getTowerImage(tower));
        else
            towerImage.imageProperty().set(null);
    }

    /**
     * This method is used to set the number label.
     *
     * @param number the number of islands.
     */
    public void setNumberLabel(Integer number) {
        numberLabel.setText(number.toString());
    }

    /**
     * This method is used for getting the button of the island
     *
     * @return the button of the island.
     */
    public Button getIslandButton() {
        return islandButton;
    }
}
