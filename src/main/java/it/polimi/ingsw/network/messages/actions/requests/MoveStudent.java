package it.polimi.ingsw.network.messages.actions.requests;

import it.polimi.ingsw.network.messages.enums.MoveLocation;
import it.polimi.ingsw.network.messages.moves.MoveActionRequest;
import it.polimi.ingsw.server.model.enums.StudentColor;

import java.util.Set;

/**
 * This message tells the player to move a student.
 */
public class MoveStudent implements MoveActionRequest {

    /**
     * from location.
     */
    final MoveLocation from;
    /**
     * from location indexes.
     */
    final Set<Integer> fromIndexesSet;
    /**
     * to location.
     */
    final MoveLocation to;
    /**
     * to location indexes.
     */
    final Set<Integer> toIndexesSet;

    /**
     * Creates message.
     * @param from location.
     * @param fromIndexesSet from location indexes.
     * @param to location.
     * @param toIndexesSet to location indexes.
     */
    public MoveStudent(MoveLocation from, Set<Integer> fromIndexesSet, MoveLocation to, Set<Integer> toIndexesSet) {
        this.from = from;
        this.fromIndexesSet = fromIndexesSet;
        this.to = to;
        this.toIndexesSet = toIndexesSet;
    }

    /**
     * @return from location.
     */
    public MoveLocation getFrom() {
        return from;
    }
    /**
     * @return from location indexes.
     */
    public Set<Integer> getFromIndexesSet() {
        if (fromIndexesSet == null)
            return null;
        return Set.copyOf(fromIndexesSet);
    }
    /**
     * @return to location.
     */
    public MoveLocation getTo() {
        return to;
    }
    /**
     * @return to location indexes.
     */
    public Set<Integer> getToIndexesSet() {
        if (toIndexesSet == null)
            return null;
        return Set.copyOf(toIndexesSet);
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        if(from != null && to != null) {
            if (from.requiresFromIndex() && (fromIndexesSet == null || fromIndexesSet.isEmpty()))
                return false;
            if (to.requiresToIndex() && (toIndexesSet == null || toIndexesSet.isEmpty()))
                return false;
            if (from != MoveLocation.ENTRANCE) {
                try {
                    for (Integer i : fromIndexesSet) {
                        StudentColor s = StudentColor.retrieveStudentColorByOrdinal(i);
                    }
                } catch (Exception ignored) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
