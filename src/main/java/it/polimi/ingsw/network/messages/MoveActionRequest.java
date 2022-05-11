package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.messages.enums.MoveLocation;

import java.util.Set;

/**
 * Interface for the move action request messages.
 */
public interface MoveActionRequest extends Move, ActionRequest {
    /**
     * @return from location.
     */
    public MoveLocation getFrom();
    /**
     * @return from location indexes.
     */
    public Set<Integer> getFromIndexesSet();
    /**
     * @return to location.
     */
    public MoveLocation getTo();
    /**
     * @return to location indexes.
     */
    public Set<Integer> getToIndexesSet();
}
