package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.enums.ImagePath;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.server.model.enums.Tower;
import javafx.scene.image.Image;

/**
 * This class contains methods for getting the proper image path from an enumeration
 */
public abstract class GUIUtils {

    /**
     * Returns the pawns related to a Student color
     *
     * @param sc a {@link StudentColor}
     * @return the image path
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
     * Returns the pawns related to a professor
     *
     * @param sc a {@link StudentColor}
     * @return the image path
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
     * Returns the tower
     *
     * @param t a {@link Tower}
     * @return the image path
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
}
