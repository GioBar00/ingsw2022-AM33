package it.polimi.ingsw.network.messages.actions;

import it.polimi.ingsw.network.messages.Action;
import it.polimi.ingsw.server.model.enums.StudentColor;

/**
 * This message signifies that the player chose a student color.
 */
public class ChosenStudentColor implements Action {

    /**
     * chosen student color.
     */
    private final StudentColor studentColor;

    /**
     * Creates the message.
     *
     * @param studentColor chosen student color.
     */
    public ChosenStudentColor(StudentColor studentColor) {
        this.studentColor = studentColor;
    }

    /**
     * @return chosen student color.
     */
    public StudentColor getStudentColor() {
        return studentColor;
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return studentColor != null;
    }
}
