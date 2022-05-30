package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.server.model.enums.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.List;

/**
 * This class contains methods for getting the proper image path from an enumeration
 */
public abstract class GUIUtils {

    /**
     * Returns the pawns related to a Student color.
     *
     * @param sc a {@link StudentColor}.
     * @return the image path.
     */
    public static Image getStudentImage(StudentColor sc) {
        Image student = null;
        switch (sc) {
            case GREEN -> student = ResourceLoader.loadImage(ImagePath.GREEN_STUDENT);
            case RED -> student = ResourceLoader.loadImage(ImagePath.RED_STUDENT);
            case YELLOW -> student = ResourceLoader.loadImage(ImagePath.YELLOW_STUDENT);
            case MAGENTA -> student = ResourceLoader.loadImage(ImagePath.MAGENTA_STUDENT);
            case BLUE -> student = ResourceLoader.loadImage(ImagePath.BLUE_STUDENTS);
        }
        return student;
    }

    /**
     * Returns the pawns related to a professor.
     *
     * @param sc a {@link StudentColor}.
     * @return the image path.
     */
    public static Image getProfImage(StudentColor sc) {
        Image prof = null;
        switch (sc) {
            case GREEN -> prof = ResourceLoader.loadImage(ImagePath.GREEN_PROF);
            case RED -> prof = ResourceLoader.loadImage(ImagePath.RED_PROF);
            case YELLOW -> prof = ResourceLoader.loadImage(ImagePath.YELLOW_PROF);
            case MAGENTA -> prof = ResourceLoader.loadImage(ImagePath.MAGENTA_PROF);
            case BLUE -> prof = ResourceLoader.loadImage(ImagePath.BLUE_PROF);
        }
        return prof;
    }

    /**
     * Returns the tower.
     *
     * @param t a {@link Tower}.
     * @return the image.
     */
    public static Image getTowerImage(Tower t) {
        return switch (t) {
            case WHITE -> ResourceLoader.loadImage(ImagePath.WHITE_TOWER);
            case GREY -> ResourceLoader.loadImage(ImagePath.GRAY_TOWER);
            case BLACK -> ResourceLoader.loadImage(ImagePath.BLACK_TOWER);
        };
    }

    /**
     * This method returns the image view related to a specified character card.
     *
     * @param c the {@link CharacterType}.
     * @return the image.
     */
    public static Image getCharacterImage(CharacterType c) {
        Image card = null;
        switch (c) {
            case CENTAUR -> card = ResourceLoader.loadImage(ImagePath.CENTAUR);
            case FARMER -> card = ResourceLoader.loadImage(ImagePath.FARMER);
            case FRIAR -> card = ResourceLoader.loadImage(ImagePath.FRIAR);
            case HARVESTER -> card = ResourceLoader.loadImage(ImagePath.HARVESTER);
            case HERALD -> card = ResourceLoader.loadImage(ImagePath.HERALD);
            case HERBALIST -> card = ResourceLoader.loadImage(ImagePath.HERBALIST);
            case JESTER -> card = ResourceLoader.loadImage(ImagePath.JESTER);
            case KNIGHT -> card = ResourceLoader.loadImage(ImagePath.KNIGHT);
            case MAILMAN -> card = ResourceLoader.loadImage(ImagePath.MAILMAN);
            case MINSTREL -> card = ResourceLoader.loadImage(ImagePath.MINSTREL);
            case PRINCESS -> card = ResourceLoader.loadImage(ImagePath.PRINCESS);
            case THIEF -> card = ResourceLoader.loadImage(ImagePath.THIEF);
        }
        return card;
    }

    /**
     * Adds node to anchor pane and sets the anchors.
     *
     * @param anchorPane to add the node to
     * @param root       to add
     */
    public static void addToAnchorPane(AnchorPane anchorPane, Region root) {
        anchorPane.setMinHeight(0.0);
        anchorPane.setMinWidth(0.0);
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        AnchorPane.setLeftAnchor(root, 0.0);
        anchorPane.getChildren().add(root);
    }

    /**
     * This method returns image related to a specified Assistant Card
     *
     * @param card the card
     * @return the Image
     */
    public static Image getAssistantCard(AssistantCard card) {
        return switch (card) {
            case CAT -> ResourceLoader.loadImage(ImagePath.CAT);
            case CHEETAH -> ResourceLoader.loadImage(ImagePath.CHEETAH);
            case DOG -> ResourceLoader.loadImage(ImagePath.DOG);
            case ELEPHANT -> ResourceLoader.loadImage(ImagePath.ELEPHANT);
            case FOX -> ResourceLoader.loadImage(ImagePath.FOX);
            case OCTOPUS -> ResourceLoader.loadImage(ImagePath.OCTOPUS);
            case SNAKE -> ResourceLoader.loadImage(ImagePath.SNAKE);
            case TURTLE -> ResourceLoader.loadImage(ImagePath.TURTLE);
            case EAGLE -> ResourceLoader.loadImage(ImagePath.EAGLE);
            case OSTRICH -> ResourceLoader.loadImage(ImagePath.OSTRICH);
        };
    }

    /**
     * This method returns the back related to a specified Assistant Card
     *
     * @param wizard the wizard
     * @return the Image
     */
    public static Image getWizardImage(Wizard wizard) {
        return switch (wizard) {
            case SENSEI -> ResourceLoader.loadImage(ImagePath.SENSEI);
            case WITCH -> ResourceLoader.loadImage(ImagePath.WITCH);
            case MERLIN -> ResourceLoader.loadImage(ImagePath.MERLIN);
            case KING -> ResourceLoader.loadImage(ImagePath.KING);
        };
    }

    /**
     * This method binds the width and height of a node to the width and height of its container.
     *
     * @param container the container
     * @param child     the child
     */
    public static void bindSize(Region container, Region child) {
        container.widthProperty().addListener((observable, oldValue, newValue) -> child.setPrefWidth(newValue.doubleValue()));
        container.heightProperty().addListener((observable, oldValue, newValue) -> child.setPrefHeight(newValue.doubleValue()));
    }

    /**
     * This method binds the width and height of an ImageView to the width and height of its container.
     *
     * @param container the container
     * @param child     the child
     */
    public static void bindSize(Region container, ImageView child) {
        container.widthProperty().addListener((observable, oldValue, newValue) -> child.setFitWidth(newValue.doubleValue()));
        container.heightProperty().addListener((observable, oldValue, newValue) -> child.setFitHeight(newValue.doubleValue()));
    }

    /**
     * This method adds a child to a pane container filling the height and width of the container and keeping the aspect ratio.
     *
     * @param pane  the container
     * @param child to add to the container
     * @param ratio the aspect ratio
     */
    public static void addToPaneCenterKeepRatio(Pane pane, Region child, double ratio) {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        List<ColumnConstraints> columnConstraints = List.of(new ColumnConstraints(), new ColumnConstraints(), new ColumnConstraints());
        List<RowConstraints> rowConstraints = List.of(new RowConstraints(), new RowConstraints(), new RowConstraints());
        for (int i = 0; i < 3; i++) {
            columnConstraints.get(i).setMinWidth(0.0);
            rowConstraints.get(i).setMinHeight(0.0);
        }
        gridPane.getColumnConstraints().addAll(columnConstraints);
        gridPane.getRowConstraints().addAll(rowConstraints);
        gridPane.add(child, 1, 1);
        bindSize(pane, gridPane);
        pane.getChildren().add(gridPane);
        ColumnConstraints centerColumn = columnConstraints.get(1);
        RowConstraints centerRow = rowConstraints.get(1);
        pane.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() * ratio < pane.getWidth()) {
                centerRow.setPercentHeight(100.0);
                centerColumn.setPercentWidth(100.0 * newValue.doubleValue() * ratio / pane.getWidth());
            }
        });
        pane.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() < pane.getHeight() * ratio) {
                centerColumn.setPercentWidth(100.0);
                centerRow.setPercentHeight(newValue.doubleValue() / ratio / pane.getHeight() * 100.0);
            }
        });
    }

    /**
     * This method activates a button and set the on action event.
     *
     * @param button the button we want to activate.
     * @param event  the action the button has to make when it's pressed.
     */
    public static void setButton(Button button, EventHandler<ActionEvent> event) {
        button.toFront();
        button.setStyle("-fx-border-color: green; -fx-border-width: 3px; -fx-background-color: transparent;");
        button.setOnAction(event);
    }


    /**
     * This method reset a specified button.
     *
     * @param button the button we want to reset.
     */
    public static void resetButton(Button button) {
        button.setStyle("-fx-background-color: transparent;");
        button.setOnAction(e -> {
        });
    }

    /**
     * This method hide a button.
     *
     * @param button the button we want to hide.
     */
    public static void hideButton(Button button) {
        button.setVisible(false);
        button.setDisable(true);
    }

    /**
     * This method show a button.
     *
     * @param button the button we want to show.
     */
    public static void showButton(Button button) {
        button.setDisable(false);
        button.setVisible(true);
    }
}
