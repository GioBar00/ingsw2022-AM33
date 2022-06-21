package it.polimi.ingsw.network.messages.actions;

import it.polimi.ingsw.network.messages.MoveAction;
import it.polimi.ingsw.server.model.enums.StudentColor;
import it.polimi.ingsw.network.messages.enums.MoveLocation;

/**
 * This message signifies that the player moved a student.
 */
public class MovedStudent implements MoveAction {

    /**
     * from where the student was moved.
     */
    final MoveLocation from;
    /**
     * from which index the student was moved.
     */
    final Integer fromIndex;
    /**
     * to where the student was moved.
     */
    final MoveLocation to;
    /**
     * to which index the student was moved.
     */
    final Integer toIndex;

    /**
     * Creates the message.
     *
     * @param from      location.
     * @param fromIndex location index.
     * @param to        location.
     * @param toIndex   location index.
     */
    public MovedStudent(MoveLocation from, Integer fromIndex, MoveLocation to, Integer toIndex) {
        this.from = from;
        this.fromIndex = fromIndex;
        this.to = to;
        this.toIndex = toIndex;
    }

    /**
     * @return from location.
     */
    public MoveLocation getFrom() {
        return from;
    }

    /**
     * @return from location index.
     */
    public Integer getFromIndex() {
        return fromIndex;
    }

    /**
     * @return to location.
     */
    public MoveLocation getTo() {
        return to;
    }

    /**
     * @return to location index.
     */
    public Integer getToIndex() {
        return toIndex;
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        if (from != null && to != null) {
            if (from.requiresFromIndex() && fromIndex == null)
                return false;
            if (to.requiresToIndex() && toIndex == null)
                return false;
            if (from != MoveLocation.ENTRANCE) {
                try {
                    StudentColor s = StudentColor.retrieveStudentColorByOrdinal(fromIndex);
                } catch (Exception ignored) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
