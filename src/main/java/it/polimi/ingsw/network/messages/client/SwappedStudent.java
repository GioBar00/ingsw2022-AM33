package it.polimi.ingsw.network.messages.client;

import it.polimi.ingsw.network.messages.enums.MoveLocation;

/**
 * This message signifies that the player swapped two students.
 */
public class SwappedStudent extends MovedStudent {

    /**
     * Creates the message.
     * @param first location.
     * @param firstIndex first location index.
     * @param second location.
     * @param secondIndex second location index.
     */
    public SwappedStudent(MoveLocation first, Integer firstIndex, MoveLocation second, Integer secondIndex) {
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
            return !to.requiresIndex() || toIndex != null;
        }
        return false;
    }
}
