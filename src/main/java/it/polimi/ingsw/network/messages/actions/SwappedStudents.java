package it.polimi.ingsw.network.messages.actions;

import it.polimi.ingsw.network.messages.actions.MovedStudent;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.network.messages.enums.MoveLocation;

/**
 * This message signifies that the player swapped two students.
 */
public class SwappedStudents extends MovedStudent {

    /**
     * Creates the message.
     * @param first location.
     * @param firstIndex first location index.
     * @param second location.
     * @param secondIndex second location index.
     */
    public SwappedStudents(MoveLocation first, Integer firstIndex, MoveLocation second, Integer secondIndex) {
        super(first, firstIndex, second, secondIndex);
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        if(from != null && to != null) {
            if (from.requiresIndex() && fromIndex == null)
                return false;
            if (to.requiresIndex() && toIndex == null)
                return false;
            if (from != MoveLocation.ENTRANCE) {
                try {
                    StudentColor s = StudentColor.retrieveStudentColorByOrdinal(fromIndex);
                } catch (Exception ignored) {
                    return false;
                }
            }
            if (to != MoveLocation.ENTRANCE) {
                try {
                    StudentColor s = StudentColor.retrieveStudentColorByOrdinal(toIndex);
                } catch (Exception ignored) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
