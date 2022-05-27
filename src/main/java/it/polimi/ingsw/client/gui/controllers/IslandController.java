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

public class IslandController implements GUIController{

    @FXML
    public AnchorPane anchorPaneStudent;
    @FXML
    public AnchorPane anchorPaneMotherNature;
    @FXML
    public AnchorPane anchorPaneTower;
    @FXML
    public AnchorPane anchorPaneBlock;
    @FXML
    public ImageView imgStudent0;
    @FXML
    public ImageView imgStudent1;
    @FXML
    public ImageView imgStudent2;
    @FXML
    public ImageView imgStudent3;
    @FXML
    public ImageView imgStudent4;
    private GUI gui;

    Map<StudentColor, Label> studentNumbersMap = new HashMap<>();

    @FXML
    private ImageView blockImage;

    @FXML
    private Button islandButton;

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
    private AnchorPane root;

    @FXML
    private ImageView towerImage;

    @Override
    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    @Override
    public void init() {
        studentNumbersMap.put(StudentColor.GREEN, numGreen);
        studentNumbersMap.put(StudentColor.RED, numRed);
        studentNumbersMap.put(StudentColor.YELLOW, numYellow);
        studentNumbersMap.put(StudentColor.MAGENTA, numMagenta);
        studentNumbersMap.put(StudentColor.BLUE, numBlue);

        GUIUtils.bindSize(root, islandImage);
        GUIUtils.bindSize(anchorPaneMotherNature, motherNatureImage);
        GUIUtils.bindSize(anchorPaneBlock, blockImage);
        GUIUtils.bindSize(anchorPaneTower, towerImage);
        GUIUtils.bindSize(anchorPaneStudent, imgStudent0);
        GUIUtils.bindSize(anchorPaneStudent, imgStudent1);
        GUIUtils.bindSize(anchorPaneStudent, imgStudent2);
        GUIUtils.bindSize(anchorPaneStudent, imgStudent3);
        GUIUtils.bindSize(anchorPaneStudent, imgStudent4);


        islandButton.setDisable(true);
        islandButton.setVisible(false);

        islandGrid.setGridLinesVisible(false);
        margins.setGridLinesVisible(false);

        setImageVisibility(motherNatureImage, false);
        setImageVisibility(blockImage, false);

        setIslandImage();
    }

    @Override
    public void setRootPane(Pane root) {
        this.root = (AnchorPane) root;
    }

    @Override
    public Pane getRootPane() {
        return root;
    }

    public void setIsland(IslandGroupView islandGroupView, boolean isMotherNature){
        setNumOfStudents(getTotalStudents(islandGroupView.getIslands()));
        setImageVisibility(motherNatureImage, isMotherNature);
        setImageVisibility(blockImage, islandGroupView.isBlocked());
        setTowerImage(islandGroupView.getIslands().get(0).getTower());
        setNumberLabel(islandGroupView.getIslands().size());
    }

    public void setIslandImage(){
        Random rand = new Random();
        int value = rand.nextInt(100);
        value = value % 3;
        switch (value){
            case 0 -> islandImage.setImage(ResourceLoader.loadImage(ImagePath.ISLAND1));
            case 1 -> islandImage.setImage(ResourceLoader.loadImage(ImagePath.ISLAND2));
            case 2 -> islandImage.setImage(ResourceLoader.loadImage(ImagePath.ISLAND3));
        }
    }

    public void setNumOfStudents(EnumMap<StudentColor, Integer> students){
        for (StudentColor sc : students.keySet()) {
            studentNumbersMap.get(sc).setText(students.get(sc).toString());
        }
    }

    public EnumMap<StudentColor, Integer> getTotalStudents(List<IslandView> islands){
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

    public void setImageVisibility(ImageView image, boolean isVisible){
        image.setVisible(isVisible);
        image.setDisable(!isVisible);
    }

    public void setTowerImage(Tower tower){
        if (tower != null)
            towerImage.setImage(GUIUtils.getTowerImage(tower));
        else
            towerImage.imageProperty().set(null);
    }

    public void setNumberLabel(Integer number){
        numberLabel.setText(number.toString());
    }
}
