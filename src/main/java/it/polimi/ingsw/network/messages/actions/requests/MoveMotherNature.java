package it.polimi.ingsw.network.messages.actions.requests;

import it.polimi.ingsw.network.messages.ActionRequest;

/**
 * This message tells the player to move mother nature.
 */
public class MoveMotherNature implements ActionRequest {

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
