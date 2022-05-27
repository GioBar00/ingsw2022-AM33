package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.server.model.enums.Tower;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;

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
        Image tower = null;
        switch (t) {
            case WHITE -> tower = ResourceLoader.loadImage(ImagePath.WHITE_TOWER);
            case GREY -> tower = ResourceLoader.loadImage(ImagePath.GRAY_TOWER);
            case BLACK -> tower = ResourceLoader.loadImage(ImagePath.BLACK_TOWER);
        }
        return tower;
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
     * @param node       to add
     */
    public static void addToAnchorPane(AnchorPane anchorPane, Node node) {
        anchorPane.getChildren().add(node);
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
    }
}
