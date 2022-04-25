package it.polimi.ingsw.network.messages.actions;

import it.polimi.ingsw.network.messages.Action;

/**
 * This message signifies that the player moved mother nature.
 */
public class MovedMotherNature implements Action {

    /**
     * number of moves that mother nature made.
     */
    private final Integer numMoves;

    /**
     * Creates the message.
     * @param numMoves number of moves that mother nature made.
     */
    public MovedMotherNature(Integer numMoves) {
        this.numMoves = numMoves;
    }

    /**
     * @return number of moves that mother nature made.
     */
    public Integer getNumMoves() {
        return numMoves;
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return numMoves != null;
    }
}
