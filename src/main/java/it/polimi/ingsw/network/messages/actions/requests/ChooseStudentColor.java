package it.polimi.ingsw.network.messages.actions.requests;

import it.polimi.ingsw.network.messages.ActionRequest;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.network.messages.Message;

import java.util.EnumSet;

/**
 * This message tells the player to choose a student color.
 */
public class ChooseStudentColor implements ActionRequest {

    /**
     * available student colors.
     */
    private final EnumSet<StudentColor> availableStudentColors;

    /**
     * Creates message.
     *
     * @param availableStudentColors available student colors.
     */
    public ChooseStudentColor(EnumSet<StudentColor> availableStudentColors) {
        this.availableStudentColors = availableStudentColors;
    }

    /**
     * @return available student colors.
     */
    public EnumSet<StudentColor> getAvailableStudentColors() {
        return EnumSet.copyOf(availableStudentColors);
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return availableStudentColors != null && !availableStudentColors.isEmpty();
    }
}
