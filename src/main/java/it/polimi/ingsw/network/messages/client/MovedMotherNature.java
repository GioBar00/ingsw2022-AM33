package it.polimi.ingsw.network.messages.client;

import it.polimi.ingsw.network.messages.Message;

public class MovedMotherNature extends Message {

    private final Integer numMoves;

    public MovedMotherNature(Integer numMoves) {
        this.numMoves = numMoves;
    }

    public Integer getNumMoves() {
        return numMoves;
    }

    @Override
    public boolean isValid() {
        return numMoves != null;
    }
}
