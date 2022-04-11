package it.polimi.ingsw.network.messages.client;

import it.polimi.ingsw.network.messages.Message;

/**
 * This message signifies that the player moved mother nature.
 */
public class MovedMotherNature extends Message {

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
