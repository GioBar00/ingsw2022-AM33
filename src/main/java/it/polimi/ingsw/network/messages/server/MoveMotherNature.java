package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.Message;

/**
 * This message tells the player to move mother nature.
 */
public class MoveMotherNature extends Message {

    /**
     * maximum movement mother nature can do.
     */
    private final Integer maxNumMoves;

    /**
     * Creates message.
     * @param maxNumMoves maximum movement mother nature can do.
     */
    public MoveMotherNature(Integer maxNumMoves) {
        this.maxNumMoves = maxNumMoves;
    }

    /**
     * @return maximum movement mother nature can do.
     */
    public Integer getMaxNumMoves() {
        return maxNumMoves;
    }

    /**
     * @return if the message is valid.
     */
    @Override
    public boolean isValid() {
        return maxNumMoves != null;
    }
}
