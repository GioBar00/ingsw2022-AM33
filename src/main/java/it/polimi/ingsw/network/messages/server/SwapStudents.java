package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.enums.MoveLocation;
import it.polimi.ingsw.server.model.enums.StudentColor;

import java.util.Set;

/**
 * This message tells the player to swap two students.
 */
public class SwapStudents extends MoveStudent {

    /**
     * Creates message
     * @param first location
     * @param firstIndexes first location indexes.
     * @param second location
     * @param secondIndexes second location indexes.
     */
    public SwapStudents(MoveLocation first, Set<Integer> firstIndexes, MoveLocation second, Set<Integer> secondIndexes) {
        super(first, firstIndexes, second, secondIndexes);
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        if(from != null && to != null) {
            if (from.requiresIndex() && (fromIndexesSet == null || fromIndexesSet.isEmpty()))
                return false;
            if (to.requiresIndex() && (toIndexesSet == null || toIndexesSet.isEmpty()))
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
            if (to != MoveLocation.ENTRANCE) {
                try {
                    for (Integer i : toIndexesSet) {
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
