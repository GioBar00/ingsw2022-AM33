package it.polimi.ingsw.network.messages.server;

import it.polimi.ingsw.network.messages.Message;

public class MoveMotherNature extends Message {

    private final Integer maxNumMoves;

    public MoveMotherNature(Integer maxNumMoves) {
        this.maxNumMoves = maxNumMoves;
    }

    public Integer getMaxNumMoves() {
        return maxNumMoves;
    }

    @Override
    public boolean isValid() {
        return maxNumMoves != null;
    }
}
