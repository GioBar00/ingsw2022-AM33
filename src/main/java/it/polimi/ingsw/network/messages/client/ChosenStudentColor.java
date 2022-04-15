package it.polimi.ingsw.network.messages.client;

import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.network.messages.Message;

/**
 * This message signifies that the player chose a student color.
 */
public class ChosenStudentColor extends Message {

    /**
     * chosen student color.
     */
    private final StudentColor studentColor;

    /**
     * Creates the message.
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
